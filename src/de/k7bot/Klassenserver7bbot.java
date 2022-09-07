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
import de.k7bot.commands.HelpCommand;
import de.k7bot.hypixel.HypixelCommandManager;
import de.k7bot.listener.*;
import de.k7bot.manage.*;
import de.k7bot.moderation.SystemNotificationChannelHolder;
import de.k7bot.music.MusicUtil;
import de.k7bot.music.PlayerManager;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.timed.Skyblocknews;
import de.k7bot.timed.VplanNEW_XML;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.commands.StatsCategoryCommand;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Properties;
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
	public static Klassenserver7bbot INSTANCE;
	public static JsonObject teacherslist;

	private final Logger logger = LoggerFactory.getLogger("K7Bot-Main");
	private final LiteSQL sqlite = new LiteSQL();
	
	public HashMap<Long, String> prefixl = new HashMap<>();
	public ShardManager shardMan;
	public AudioPlayerManager audioPlayerManager;
	public PlayerManager playerManager;

	public Thread loop;
	public Thread shutdownT;

	public boolean hypixelapienabled = false;
	public boolean githubapienabled = false;
	public boolean vplanenabled = false;

	public boolean imShutdown = false;
	public boolean exit = false;
	public boolean hasstarted = false;
	public boolean indev;

	public boolean minlock = false;
	public int sec = 60;
	public int min = 0;

	private GitHub github;
	private HypixelAPI API;
	private Long ownerId;
	private String vplanpw;
	private Integer schoolID;
	private CommandManager cmdMan;
	private HypixelCommandManager hypMan;
	private SlashCommandManager slashMan;
	private MusicUtil musicutil;
	private LyricsClient lyricsapi;
	private GLA lyricsapiold;
	private SystemNotificationChannelHolder syschannels;
	private VplanNEW_XML vplan;

	String[] status = new String[] { "-help", "@K7Bot", "-getprefix" };

	public Klassenserver7bbot(boolean indev) throws LoginException, IllegalArgumentException {
		INSTANCE = this;
		this.indev = indev;

		Properties prop = new Properties();
		FileInputStream in;

		try {
			in = new FileInputStream("resources/bot.properties");
			prop.load(in);
			in.close();
		} catch (IOException e) {

			logger.error("No valid config File found! generating a new one");
			File f = new File("resources/bot.properties");

			if (!f.exists()) {
				generateConfigFile(f);
			}

		}

		initialize(prop);
		checkpreflist();
		awaitReady();

		Shutdown();
		Skyblocknews.onEventCheck();
		runLoop();
	}

	public boolean initialize(Properties prop) {
		loadTeacherList();
		sqlite.connect();

		String token = prop.getProperty("token");

		String canaryToken;
		if ((canaryToken = prop.getProperty("canary-token")) == null) {
			this.indev = false;
		}

		String hypixelToken;
		if ((hypixelToken = prop.getProperty("hypixel-api-key")) != null) {
			this.hypixelapienabled = true;
		}

		String githubtoken;
		if ((githubtoken = prop.getProperty("github-oauth-token")) != null) {
			this.githubapienabled = true;
		}

		this.ownerId = Long.valueOf(prop.getProperty("ownerId"));

		if ((this.vplanpw = prop.getProperty("vplanpw")) != null && prop.getProperty("schoolID") != null) {
			this.schoolID = Integer.valueOf(prop.getProperty("schoolID"));
			this.vplanenabled = true;
		}

		String shards;
		int shardc;

		if ((shards = prop.getProperty("shardCount")) != null && !shards.equalsIgnoreCase("")) {
			shardc = Integer.valueOf(shards);
		} else {
			shardc = -1;
			this.logger.info("Empty Shard-Count Config");
		}

		try {
			buildBot(token, canaryToken, shardc);
		} catch (LoginException | IllegalArgumentException e) {
			this.logger.error("Couldn't start Bot! - Please check the config -> Message='" + e.getMessage() + "'");
			e.printStackTrace();
		}

		initializeObjects();
		initializeApis(hypixelToken, githubtoken);
		return true;
	}

	public void buildBot(String token, String canaryToken, int shardc) throws LoginException, IllegalArgumentException {
		
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
		builder.addEventListeners(new ChartsAutocomplete());
		builder.addEventListeners(new ButtonListener());

		this.shardMan = builder.build();
	}

	public void initializeApis(String hypixelToken, String githubtoken) {

		if (this.hypixelapienabled) {

			String key = System.getProperty("apiKey", hypixelToken);
			this.API = new HypixelAPI(new ApacheHttpClient(UUID.fromString(key)));

		}

		if (this.githubapienabled) {

			try {
				new GitHubBuilder();
				this.github = new GitHubBuilder().withOAuthToken(githubtoken).build();
			} catch (IOException e) {
				this.logger.error("couldn't start GitHub-API");
			}

		}
	}

	public void initializeObjects() {

		this.musicutil = new MusicUtil();
		this.lyricsapi = new LyricsClient();
		this.lyricsapiold = new GLA();
		this.cmdMan = new CommandManager(hypixelapienabled, githubapienabled);
		this.hypMan = new HypixelCommandManager();

		this.syschannels = new SystemNotificationChannelHolder();

		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();

		this.slashMan = new SlashCommandManager();
		this.vplan = new VplanNEW_XML();

		SQLManager.onCreate();

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

	public void awaitReady() {

		logger.info("Awaiting jda ready");
		shardMan.getShards().forEach(jda -> {

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
						this.imShutdown = true;
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
		this.imShutdown = true;

		if (PlayCommand.conv.converter != null) {
			PlayCommand.conv.converter.interrupt();
		}

		if (this.loop != null) {
			this.loop.interrupt();
		}
		if (this.shardMan != null) {

			this.API.shutdown();
			StatsCategoryCommand.onShutdown(indev);
			this.shardMan.setStatus(OnlineStatus.OFFLINE);
			logger.info("Bot offline");
			this.shardMan.shutdown();
			sqlite.disconnect();
			this.shutdownT.interrupt();
		} else {
			logger.info("ShardMan was null!");
		}
	}

	public void onsecond() {
		if (!this.loop.isInterrupted()) {

			if ((this.min % 10 == 0) && !this.minlock) {
				this.minlock = true;
				this.checkpreflist();
				this.getsyschannell().checkSysChannelList();
				vplan.sendVplanMessage(false, "10b", null);
				Skyblocknews.onEventCheck();

				if ((!this.hasstarted)) {
					StatsCategoryCommand.onStartup(this.indev);
					this.hasstarted = true;
				}
				Random rand = new Random();

				int i = rand.nextInt(this.status.length);

				this.shardMan.getShards().forEach(jda -> {
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

			ResultSet set = sqlite.onQuery("SELECT guildId, prefix FROM botutil");
			if (set != null) {

				while (set.next()) {

					if (!this.prefixl.containsKey(set.getLong("guildId"))) {
						this.prefixl.put(set.getLong("guildId"), set.getString("prefix"));
					}

					if (set.getString("prefix") == null) {
						this.getDB().onUpdate("UPDATE botutil SET prefix='-' WHERE guildId=" + set.getLong("guildId"));
					}

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.shardMan.getGuilds().forEach(gu -> {

			Long id = gu.getIdLong();

			if (!this.prefixl.containsKey(id)) {
				this.prefixl.put(id, "-");
				this.getDB().onUpdate("INSERT INTO botutil(guildId, prefix) VALUES(" + id + ", '-')");
			}

		});

	}

	public void generateConfigFile(File f) {

		try {
			f.createNewFile();

			BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/config.properties"),
					Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);

			Properties prop = new Properties();

			prop.setProperty("token", "");
			prop.setProperty("canary-token", "");
			prop.setProperty("hypixel-api-key", "");
			prop.setProperty("github-oauth-token", "");
			prop.setProperty("ownerId", "");
			prop.setProperty("shardCount", "");
			prop.setProperty("vplanpw", "");
			prop.setProperty("schoolID", "");

			prop.store(stream, "Bot-Configfile\n 'token' is required!");
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void loadTeacherList() {
		File file = new File("resources/teachers.json");

		if (file.exists()) {

			try {

				String jsonstring = Files.readString(Path.of(file.getPath()));

				JsonElement json = JsonParser.parseString(jsonstring);
				teacherslist = json.getAsJsonObject();

			} catch (IOException e1) {

				e1.printStackTrace();

			}

		}
	}

	public CommandManager getCmdMan() {
		return this.cmdMan;
	}

	public HypixelCommandManager gethypMan() {
		return this.hypMan;
	}

	public SlashCommandManager getslashMan() {
		return this.slashMan;
	}

	public GitHub getGitapi() {
		return this.github;
	}

	public LiteSQL getDB() {
		return this.sqlite;
	}

	public MusicUtil getMusicUtil() {
		return this.musicutil;
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
		return this.API;
	}

	public SystemNotificationChannelHolder getsyschannell() {
		return syschannels;
	}

	public String getVplanPW() {
		return this.vplanpw;
	}

	public int getSchoolID() {
		return this.schoolID;
	}
}
