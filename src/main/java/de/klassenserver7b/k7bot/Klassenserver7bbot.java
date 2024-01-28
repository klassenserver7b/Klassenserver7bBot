package de.klassenserver7b.k7bot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.jdaudp.NativeAudioSendFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import de.klassenserver7b.k7bot.listener.*;
import de.klassenserver7b.k7bot.logging.listeners.LoggingListener;
import de.klassenserver7b.k7bot.manage.*;
import de.klassenserver7b.k7bot.music.asms.ExtendedLocalAudioSourceManager;
import de.klassenserver7b.k7bot.music.asms.SpotifyAudioSourceManager;
import de.klassenserver7b.k7bot.music.utilities.AudioPlayerUtil;
import de.klassenserver7b.k7bot.music.utilities.gla.GLAWrapper;
import de.klassenserver7b.k7bot.music.utilities.spotify.SpotifyInteractions;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.sql.SQLManager;
import de.klassenserver7b.k7bot.subscriptions.SubscriptionManager;
import de.klassenserver7b.k7bot.threads.ConsoleReadThread;
import de.klassenserver7b.k7bot.threads.LoopThread;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Klassenserver7bbot {

    private static Klassenserver7bbot INSTANCE;

    private final Logger logger = LoggerFactory.getLogger("K7Bot-Main");

    private ShardManager shardMgr;
    private CommandManager cmdMgr;

    private SystemNotificationChannelManager sysChannelMgr;
    private PrefixManager prefixMgr;

    private SubscriptionManager subMgr;

    private SlashCommandManager slashMgr;

    private final PropertiesManager propMgr;
    private LoopedEventManager loopedEventMgr;

    private AudioPlayerManager audioPlayerManager;
    private AudioPlayerUtil playerutil;

    private LoopThread loop;
    private ConsoleReadThread shutdownT;

    private GitHub github;
    private LyricsClient lyricsapi;
    private GLAWrapper lyricsapiold;
    private SpotifyInteractions spotifyinteractions;

    private Long ownerId;
    private JsonObject teacherslist;
    private boolean exit = false;
    private boolean indev;

    private Klassenserver7bbot(boolean indev) throws IllegalArgumentException {
        INSTANCE = this;
        this.indev = indev;
        this.propMgr = new PropertiesManager();

        if (!propMgr.loadProps() || !propMgr.isBotTokenValid()) {
            return;
        }

        initializeBot();
        awaitJDAReady();

        runShutdown();

        initListeners();
        runLoop();
    }

    protected boolean initializeBot() {

        loadTeacherList();
        LiteSQL.connect();

        SQLManager.onCreate();

        String token = propMgr.getProperty("token");

        String canaryToken;
        if ((canaryToken = propMgr.getProperty("canary-token")) == null) {
            this.indev = false;
        }

        String shards;
        int shardc;

        if ((shards = propMgr.getProperty("shardCount")) != null && !shards.equalsIgnoreCase("")) {
            shardc = Integer.parseInt(shards);
        } else {
            shardc = -1;
            this.logger.info("Empty Shard-Count Config");
        }

        this.ownerId = Long.valueOf(propMgr.getProperty("ownerId"));

        try {
            shardMgr = buildBot(token, canaryToken, shardc);
        } catch (IllegalArgumentException e) {
            invalidConfigExit("Couldn't start Bot! - EXITING", 1, e);
        }

        propMgr.checkAPIProps();
        initializeObjects();
        loopedEventMgr.initializeDefaultEvents();

        return true;
    }

    protected ShardManager buildBot(String token, String canaryToken, int shardc) throws IllegalArgumentException {

        DefaultShardManagerBuilder builder;

        if (!indev) {
            builder = DefaultShardManagerBuilder.create(token, EnumSet.allOf(GatewayIntent.class));
        } else {
            builder = DefaultShardManagerBuilder.create(canaryToken, EnumSet.allOf(GatewayIntent.class));
        }

        builder.setAudioSendFactory(new NativeAudioSendFactory(400));
        builder.setShardsTotal(shardc);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setActivity(Activity.listening("-help"));

        builder.setStatus(OnlineStatus.ONLINE);

        builder.addEventListeners(new CommandListener());
        builder.addEventListeners(new SlashCommandListener());
        builder.addEventListeners(LoggingListener.getDefault());
        builder.addEventListeners(new VoiceListener());
        builder.addEventListeners(new ReactRoleListener());
        builder.addEventListeners(new AutoRickroll());
        builder.addEventListeners(new MemesReact());
        builder.addEventListeners(new BotgetDC());
        builder.addEventListeners(new MessageListener());

        ShardManager initShardMgr = null;

        try {
            initShardMgr = builder.build();
        } catch (InvalidTokenException e) {
            invalidConfigExit("INVALID TOKEN - EXITING", 5, e);
        } catch (IllegalArgumentException e) {
            invalidConfigExit("ILLEGAL LOGIN ARGUMENT / EMPTY TOKEN - EXITING", 1, e);
        }

        if (initShardMgr != null) {
            return initShardMgr;
        } else {
            throw new IllegalArgumentException("BOT CREATION FAILED - EXITED", new Throwable().fillInStackTrace());
        }
    }

    protected void initializeObjects() {

        this.prefixMgr = new PrefixManager();

        this.subMgr = new SubscriptionManager();
        this.loopedEventMgr = new LoopedEventManager();

        this.sysChannelMgr = new SystemNotificationChannelManager();

        this.audioPlayerManager = new DefaultAudioPlayerManager();
        this.playerutil = new AudioPlayerUtil();

        this.lyricsapi = new LyricsClient("Genius");
        // this.lyricsapiold = new GLA();
        this.lyricsapiold = new GLAWrapper();

        if (propMgr.isApiEnabled("github")) {
            try {
                this.github = new GitHubBuilder().withOAuthToken(propMgr.getProperty("github-oauth-token")).build();
            } catch (IOException e) {
                propMgr.getEnabledApis().put("github", false);
                this.logger.error("couldn't start GitHub-Api");
            }

        }

        this.spotifyinteractions = new SpotifyInteractions();

        InitializeMusic(this.audioPlayerManager);

        this.cmdMgr = new CommandManager();
        this.slashMgr = new SlashCommandManager();
    }

    public void InitializeMusic(AudioPlayerManager manager) {

        manager.getConfiguration().setFilterHotSwapEnabled(true);
        manager.registerSourceManager(new SpotifyAudioSourceManager());
        manager.registerSourceManager(new ExtendedLocalAudioSourceManager());
        AudioSourceManagers.registerRemoteSources(manager);

    }

    public void awaitJDAReady() {

        logger.info("Awaiting jda ready");
        shardMgr.getShards().forEach(jda -> {

            try {
                logger.debug("Awaiting jda ready for shard: " + jda.getShardInfo());
                jda.awaitReady();
            } catch (InterruptedException e) {
                logger.info("could not start shardInfo: " + jda.getShardInfo() + " and Self-Username :"
                        + jda.getSelfUser().getName());
                logger.error(e.getMessage(), e);
            }

        });
        if (!this.indev) {
            logger.info("Bot was started (nondev)");
        } else {
            logger.info("Bot was started in Canary mode");
        }
    }

    protected void initListeners() {

        HashMap<CompletableFuture<Integer>, InitRequiringListener> futures = new HashMap<>();

        for (JDA jda : shardMgr.getShards()) {
            for (Object eventlistener : jda.getEventManager().getRegisteredListeners()) {
                if (eventlistener instanceof InitRequiringListener listener) {
                    futures.put(listener.initialize(), listener);
                }
            }
        }

        for (CompletableFuture<Integer> future : futures.keySet()) {
            int code;
            try {
                code = future.get();
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage(), e);
                return;
            }

            if (code != 0) {
                logger.warn(
                        futures.get(future).getClass().getSimpleName() + " failed to initialize, ExitCode: " + code);
                continue;
            }

            logger.info(futures.get(future).getClass().getSimpleName() + " successfully initialized");
        }

    }

    protected void invalidConfigExit(String message, int exitCode, RuntimeException e) {
        logger.error(message, e);
        try {
            Desktop.getDesktop().open(new File("resources/bot.properties"));
        } catch (IOException e1) {
            // EXIT AS USUAL;
        }
        System.exit(exitCode);
    }

    protected void runShutdown() {
        this.shutdownT = new ConsoleReadThread();
    }

    public void runLoop() {
        this.loop = new LoopThread();
    }

    public void restartLoop() {
        this.loop.restart();
    }

    public void stopLoop() {
        this.loop.stopLoop();
    }

    public void loadTeacherList() {
        File file = new File("resources/teachers.json");

        if (file.exists()) {

            try {

                String jsonstring = Files.readString(file.toPath());

                JsonElement json = JsonParser.parseString(jsonstring);
                teacherslist = json.getAsJsonObject();

            } catch (IOException e1) {

                logger.error(e1.getMessage(), e1);

            }

        }
    }

    public String getSelfName(Long guildid) {

        Guild g;

        if (guildid != null && (g = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid)) != null) {
            return g.getSelfMember().getEffectiveName();
        }

        return Klassenserver7bbot.getInstance().getShardManager().getShards().get(0).getSelfUser().getEffectiveName();
    }

    public static Klassenserver7bbot getInstance() {

        if (INSTANCE == null) {
            try {
                return new Klassenserver7bbot(false);
            } catch (IllegalArgumentException e) {
                LoggerFactory.getLogger("InstanceManager").error(e.getMessage(), e);
            }
        }

        return INSTANCE;
    }

    public static Klassenserver7bbot getInstance(boolean indev) throws IllegalArgumentException {

        if (INSTANCE == null) {
            return new Klassenserver7bbot(indev);
        }

        return INSTANCE;
    }

    public CommandManager getCmdMan() {
        return this.cmdMgr;
    }

    public SlashCommandManager getslashMan() {
        return this.slashMgr;
    }

    public GitHub getGitapi() {
        return this.github;
    }

    public LyricsClient getLyricsAPI() {
        return this.lyricsapi;
    }

    public GLAWrapper getLyricsAPIold() {
        return this.lyricsapiold;
    }

    public Logger getMainLogger() {
        return this.logger;
    }

    public Long getOwnerId() {
        return this.ownerId;
    }

    public SystemNotificationChannelManager getSysChannelMgr() {
        return sysChannelMgr;
    }

    public boolean isInExit() {
        return this.exit;
    }

    public boolean isDevMode() {
        return this.indev;
    }

    public ShardManager getShardManager() {
        return this.shardMgr;
    }

    public AudioPlayerUtil getPlayerUtil() {
        return this.playerutil;
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return this.audioPlayerManager;
    }

    public SubscriptionManager getSubscriptionManager() {
        return this.subMgr;
    }

    public PropertiesManager getPropertiesManager() {
        return this.propMgr;
    }

    public LoopedEventManager getLoopedEventManager() {
        return this.loopedEventMgr;
    }

    public JsonObject getTeacherList() {
        return this.teacherslist;
    }

    public PrefixManager getPrefixMgr() {
        return this.prefixMgr;
    }

    public ConsoleReadThread getShutdownThread() {
        return this.shutdownT;
    }

    public void setexit(boolean inexit) {
        this.exit = inexit;
    }

    public SpotifyInteractions getSpotifyinteractions() {
        return spotifyinteractions;
    }
}
