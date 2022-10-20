package de.k7bot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import core.GLA;
import de.k7bot.sql.LiteSQL;
import de.k7bot.sql.SQLManager;
import de.k7bot.subscriptions.SubscriptionManager;
import de.k7bot.commands.common.HelpCommand;
import de.k7bot.hypixel.HypixelCommandManager;
import de.k7bot.listener.*;
import de.k7bot.manage.*;
import de.k7bot.music.AudioPlayerUtil;
import de.k7bot.music.commands.common.PlayCommand;
import de.k7bot.util.commands.common.StatsCategoryCommand;
import de.k7bot.util.internalapis.InternalAPIManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import javax.security.auth.login.LoginException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private SubscriptionManager subMgr;
	private SystemNotificationChannelManager syschannels;
	private SlashCommandManager slashMgr;
	private HypixelCommandManager hypMgr;
	private PropertiesManager propMgr;
	private InternalAPIManager internalApiMgr;

	private HashMap<Long, String> prefixl = new HashMap<>();

	private AudioPlayerManager audioPlayerManager;
	private AudioPlayerUtil playerutil;

	private Thread loop;
	private Thread shutdownT;

	private GitHub github;
	private HypixelAPI hypixelApi;
	private LyricsClient lyricsapi;
	private GLA lyricsapiold;

	private Long ownerId;
	private JsonObject teacherslist;

	private boolean minlock = false;
	private int sec = 60;
	private int min = 0;
	private boolean blockevents = false;
	private boolean exit = false;
	private boolean hasstarted = false;
	private boolean indev;

	String[] status = new String[] { "-help", "@K7Bot", "-getprefix" };

	private Klassenserver7bbot(boolean indev) throws LoginException, IllegalArgumentException {
		INSTANCE = this;
		this.indev = indev;
		this.propMgr = new PropertiesManager();

		if (!propMgr.loadProps()) {
			return;
		}
		if (!propMgr.validate()) {
			return;
		}

		initializeBot();
		checkpreflist();
		awaitJDAReady();

		Shutdown();
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
			this.logger.error("Couldn't start Bot! - Please check the config -> Message='" + e.getMessage() + "'");
			e.printStackTrace();
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
		builder.addEventListeners(new SlashCommandAutocomplete());
		builder.addEventListeners(new ButtonListener());

		this.shardMgr = builder.build();
	}

	private void initializeObjects() {

		this.cmdMgr = new CommandManager();
		this.hypMgr = new HypixelCommandManager();
		this.slashMgr = new SlashCommandManager();

		this.subMgr = new SubscriptionManager();
		this.internalApiMgr = new InternalAPIManager();

		this.syschannels = new SystemNotificationChannelManager();
		this.syschannels.initialize();

		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerutil = new AudioPlayerUtil();

		this.lyricsapi = new LyricsClient();
		this.lyricsapiold = new GLA();

		if (propMgr.getEnabledApis().get("hypixel")) {
			this.hypixelApi = new HypixelAPI(
					new ApacheHttpClient(UUID.fromString(propMgr.getProperty("hypixel-api-key"))));
		}

		if (propMgr.getEnabledApis().get("github")) {
			try {
				new GitHubBuilder();
				this.github = new GitHubBuilder().withOAuthToken(propMgr.getProperty("github-oauth-token")).build();
			} catch (IOException e) {
				propMgr.getEnabledApis().put("github", false);
				this.logger.error("couldn't start GitHub-Api");
			}

		}

		HelpCommand.updateCategoryList();

		InitializeMusic(this.audioPlayerManager);
	}

	public void InitializeMusic(AudioPlayerManager manager) {

		AudioSourceManagers.registerRemoteSources(manager);
		AudioSourceManagers.registerLocalSource(manager);
		manager.getConfiguration().setFilterHotSwapEnabled(true);

		manager.registerSourceManager(new YoutubeAudioSourceManager());
		manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
		manager.registerSourceManager(new BandcampAudioSourceManager());
		manager.registerSourceManager(new VimeoAudioSourceManager());
		manager.registerSourceManager(new TwitchStreamAudioSourceManager());
		manager.registerSourceManager(new BeamAudioSourceManager());
		manager.registerSourceManager(new HttpAudioSourceManager());
		manager.registerSourceManager(new LocalAudioSourceManager());

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
				e.printStackTrace();
			}

		});
		if (!this.indev) {
			logger.info("Bot was started (nondev)");
		} else {
			logger.info("Bot was started in Canary mode");
		}
	}

	public void Shutdown() {
		this.shutdownT = new Thread(() -> {
			String line;

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			try {
				while ((line = reader.readLine()) != null) {
					if (line.equalsIgnoreCase("exit")) {
						this.blockevents = true;
						this.exit = true;

						onShutdown();
						reader.close();
						break;
					}
					System.out.println("Use Exit to Shutdown");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		this.shutdownT.setName("Shutdown");
		this.shutdownT.start();
	}

	public void runLoop() {
		this.loop = new Thread(() -> {
			if (!this.loop.isInterrupted()) {
				long time = System.currentTimeMillis();

				while (!this.exit) {
					if (System.currentTimeMillis() >= time + 1000) {
						time = System.currentTimeMillis();

						onsecond();
					}
				}
			}
		});

		this.loop.setName("loop");
		this.loop.start();
	}

	public void onShutdown() {
		logger.info("Bot is shutting down!");
		this.blockevents = true;

		if (PlayCommand.conv.converter != null) {
			PlayCommand.conv.converter.interrupt();
		}

		if (this.loop != null) {
			this.loop.interrupt();
		}
		if (this.shardMgr != null) {

			this.hypixelApi.shutdown();
			internalApiMgr.shutdownAPIs();
			StatsCategoryCommand.onShutdown(indev);
			this.shardMgr.setStatus(OnlineStatus.OFFLINE);
			logger.info("Bot offline");
			this.shardMgr.shutdown();
			LiteSQL.disconnect();
			this.shutdownT.interrupt();
		} else {
			logger.info("ShardMan was null!");
		}
	}

	public void stopLoop() {
		this.loop.interrupt();
	}

	public void onsecond() {
		if (!this.loop.isInterrupted()) {

			if ((this.min % 10 == 0) && !this.minlock) {
				this.minlock = true;
				this.checkpreflist();

				internalApiMgr.checkForUpdates();

				if ((!this.hasstarted)) {
					StatsCategoryCommand.onStartup(this.indev);
					this.hasstarted = true;
				}
				Random rand = new Random();

				int i = rand.nextInt(this.status.length);

				this.shardMgr.getShards().forEach(jda -> {
					jda.getPresence().setActivity(Activity.listening(this.status[i]));
				});

			}

			if (sec <= 0) {

				sec = 60;
				min++;
				minlock = false;
				if (min >= 60) {
					min = 0;

				}
			} else {
				sec--;
			}
		}
	}

	public void checkpreflist() {

		try {

			ResultSet set = LiteSQL.onQuery("SELECT guildId, prefix FROM botutil");
			if (set != null) {

				while (set.next()) {

					this.prefixl.put(set.getLong("guildId"), set.getString("prefix"));

					if (set.getString("prefix") == null) {
						LiteSQL.onUpdate("UPDATE botutil SET prefix='-' WHERE guildId= ? ", set.getLong("guildId"));
					}

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.shardMgr.getGuilds().forEach(gu -> {

			Long id = gu.getIdLong();

			if (!this.prefixl.containsKey(id)) {
				this.prefixl.put(id, "-");
				LiteSQL.onUpdate("INSERT INTO botutil(guildId, prefix) VALUES(?,?)", id, "-");
			}

		});

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

	public GLA getLyricsAPIold() {
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

	public HashMap<Long, String> getPrefixList() {
		return this.prefixl;
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

	public void setexit(boolean inexit) {
		this.exit = inexit;
	}

	public void setEventBlocking(boolean inshutdown) {
		this.blockevents = inshutdown;
	}
}
