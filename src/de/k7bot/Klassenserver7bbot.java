package de.k7bot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import de.k7bot.hypixel.HypixelCommandManager;
import de.k7bot.listener.AutoRickroll;
import de.k7bot.listener.BanListener;
import de.k7bot.listener.BotgetDC;
import de.k7bot.listener.ButtonListener;
import de.k7bot.listener.ChannelCreateRemoveListener;
import de.k7bot.listener.CommandListener;
import de.k7bot.listener.JoinandLeaveListener;
import de.k7bot.listener.MemesReact;
import de.k7bot.listener.ReactionListener;
import de.k7bot.listener.RoleEditListener;
import de.k7bot.listener.Role_InviteListener;
import de.k7bot.listener.SlashCommandListener;
import de.k7bot.listener.VoiceListener;
import de.k7bot.manage.CommandManager;
import de.k7bot.manage.PrefixManager;
import de.k7bot.manage.PropertiesManager;
import de.k7bot.manage.SlashCommandManager;
import de.k7bot.manage.SystemNotificationChannelManager;
import de.k7bot.music.AudioPlayerUtil;
import de.k7bot.music.utilities.gla.GLAWrapper;
import de.k7bot.music.utilities.spotify.SpotifyAudioSourceManager;
import de.k7bot.sql.LiteSQL;
import de.k7bot.sql.SQLManager;
import de.k7bot.subscriptions.SubscriptionManager;
import de.k7bot.threads.LoopThread;
import de.k7bot.threads.ShutdownThread;
import de.k7bot.util.customapis.InternalAPIManager;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;

public class Klassenserver7bbot {

	private static Klassenserver7bbot INSTANCE;

	private final Logger logger = LoggerFactory.getLogger("K7Bot-Main");

	private ShardManager shardMgr;
	private CommandManager cmdMgr;

	private SystemNotificationChannelManager syschannels;
	private PrefixManager prefixMgr;

	private SubscriptionManager subMgr;

	private SlashCommandManager slashMgr;
	private HypixelCommandManager hypMgr;

	private PropertiesManager propMgr;
	private InternalAPIManager internalApiMgr;

	private AudioPlayerManager audioPlayerManager;
	private AudioPlayerUtil playerutil;

	private LoopThread loop;
	private ShutdownThread shutdownT;

	private GitHub github;
	private HypixelAPI hypixelApi;
	private LyricsClient lyricsapi;
	private GLAWrapper lyricsapiold;

	private Long ownerId;
	private JsonObject teacherslist;

	private boolean blockevents = false;
	private boolean exit = false;
	private boolean indev;

	private Klassenserver7bbot(boolean indev) throws LoginException, IllegalArgumentException {
		INSTANCE = this;
		this.indev = indev;
		this.propMgr = new PropertiesManager();

		if (!propMgr.loadProps() || !propMgr.isBotTokenValid()) {
			return;
		}

		initializeBot();
		awaitJDAReady();

		runShutdown();
		runLoop();
	}

	private boolean initializeBot() {

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
			shardc = Integer.valueOf(shards);
		} else {
			shardc = -1;
			this.logger.info("Empty Shard-Count Config");
		}

		this.ownerId = Long.valueOf(propMgr.getProperty("ownerId"));

		try {
			buildBot(token, canaryToken, shardc);
		} catch (LoginException | IllegalArgumentException e) {
			logger.warn("Couldn't start Bot! - Please check the config -> Message='" + e.getMessage() + "'");
			logger.error(e.getMessage(), e);
		}

		propMgr.checkAPIProps();
		initializeObjects();
		internalApiMgr.initializeApis();

		return true;
	}

	private void buildBot(String token, String canaryToken, int shardc)
			throws LoginException, IllegalArgumentException {

		DefaultShardManagerBuilder builder;

		if (!indev) {
			builder = DefaultShardManagerBuilder.create(token, EnumSet.allOf(GatewayIntent.class));
		} else {
			builder = DefaultShardManagerBuilder.create(canaryToken, EnumSet.allOf(GatewayIntent.class));
		}

		builder.setShardsTotal(shardc);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		builder.setActivity(Activity.listening("-help"));

		builder.setStatus(OnlineStatus.ONLINE);

		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		builder.addEventListeners(new ReactionListener());
		builder.addEventListeners(new Role_InviteListener());
		builder.addEventListeners(new RoleEditListener());
		builder.addEventListeners(new BanListener());
		builder.addEventListeners(new JoinandLeaveListener());
		builder.addEventListeners(new ChannelCreateRemoveListener());
		builder.addEventListeners(new SlashCommandListener());
		builder.addEventListeners(new AutoRickroll());
		builder.addEventListeners(new MemesReact());
		builder.addEventListeners(new BotgetDC());
		builder.addEventListeners(new ButtonListener());

		this.shardMgr = builder.build();
	}

	private void initializeObjects() {

		this.prefixMgr = new PrefixManager();

		this.subMgr = new SubscriptionManager();
		this.internalApiMgr = new InternalAPIManager();

		this.syschannels = new SystemNotificationChannelManager();

		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerutil = new AudioPlayerUtil();

		this.lyricsapi = new LyricsClient("Genius");
		// this.lyricsapiold = new GLA();
		this.lyricsapiold = new GLAWrapper();

		if (propMgr.isApiEnabled("hypixel")) {
			this.hypixelApi = new HypixelAPI(
					new ApacheHttpClient(UUID.fromString(propMgr.getProperty("hypixel-api-key"))));
		}

		if (propMgr.isApiEnabled("github")) {
			try {
				new GitHubBuilder();
				this.github = new GitHubBuilder().withOAuthToken(propMgr.getProperty("github-oauth-token")).build();
			} catch (IOException e) {
				propMgr.getEnabledApis().put("github", false);
				this.logger.error("couldn't start GitHub-Api");
			}

		}

		this.cmdMgr = new CommandManager();
		this.hypMgr = new HypixelCommandManager();
		this.slashMgr = new SlashCommandManager();

		InitializeMusic(this.audioPlayerManager);
	}

	public void InitializeMusic(AudioPlayerManager manager) {

		manager.getConfiguration().setFilterHotSwapEnabled(true);

		manager.registerSourceManager(new SpotifyAudioSourceManager());

//		manager.registerSourceManager(new YoutubeAudioSourceManager());
//		manager.registerSourceManager(new LocalAudioSourceManager());
//		manager.registerSourceManager(new HttpAudioSourceManager());
//		manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
//		manager.registerSourceManager(new BandcampAudioSourceManager());
//		manager.registerSourceManager(new VimeoAudioSourceManager());
//		manager.registerSourceManager(new TwitchStreamAudioSourceManager());
//		manager.registerSourceManager(new BeamAudioSourceManager());

		AudioSourceManagers.registerRemoteSources(manager);
		AudioSourceManagers.registerLocalSource(manager);

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

	private void runShutdown() {
		this.shutdownT = new ShutdownThread();
	}

	public void runLoop() {
		this.loop = new LoopThread();
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

	public static Klassenserver7bbot getInstance() {

		if (INSTANCE == null) {
			try {
				new Klassenserver7bbot(false);
			} catch (LoginException | IllegalArgumentException e) {
				LoggerFactory.getLogger("InstanceManager").error(e.getMessage(), e);
			}
		}

		return INSTANCE;
	}

	public static Klassenserver7bbot getInstance(boolean indev) throws LoginException, IllegalArgumentException {

		if (INSTANCE == null) {
			new Klassenserver7bbot(indev);
		}

		return INSTANCE;
	}

	public CommandManager getCmdMan() {
		return this.cmdMgr;
	}

	public HypixelCommandManager gethypMan() {
		return this.hypMgr;
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

	public HypixelAPI getHypixelAPI() {
		return this.hypixelApi;
	}

	public SystemNotificationChannelManager getsyschannell() {
		return syschannels;
	}

	public boolean isInExit() {
		return this.exit;
	}

	public boolean isEventBlocked() {
		return this.blockevents;
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

	public InternalAPIManager getInternalAPIManager() {
		return this.internalApiMgr;
	}

	public JsonObject getTeacherList() {
		return this.teacherslist;
	}

	public PrefixManager getPrefixMgr() {
		return this.prefixMgr;
	}

	public ShutdownThread getShutdownThread() {
		return this.shutdownT;
	}

	public void setexit(boolean inexit) {
		this.exit = inexit;
	}

	public void setEventBlocking(boolean inshutdown) {
		this.blockevents = inshutdown;
	}
}
