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
import de.k7bot.commands.HelpCommand;
import de.k7bot.hypixel.HypixelCommandManager;
import de.k7bot.listener.*;
import de.k7bot.manage.*;
import de.k7bot.moderation.SystemNotificationChannelHolder;
import de.k7bot.music.AudioPlayerUtil;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.timed.Skyblocknews;
import de.k7bot.util.commands.StatsCategoryCommand;
import de.k7bot.util.internalapis.LernsaxInteractions;
import de.k7bot.util.internalapis.VplanNEW_XML;

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

	private final Logger logger = LoggerFactory.getLogger("K7Bot-Main");

	private ShardManager shardmgr;
	private CommandManager cmdmgr;
	private SubscriptionManager submgr;
	private SystemNotificationChannelHolder syschannels;
	private SlashCommandManager slashmgr;
	private HypixelCommandManager hypmgr;

	private HashMap<Long, String> prefixl = new HashMap<>();

	private AudioPlayerManager audioPlayerManager;
	private AudioPlayerUtil playerutil;

	private Thread loop;
	private Thread shutdownT;

	private GitHub github;
	private HypixelAPI API;
	private VplanNEW_XML vplan;
	private LernsaxInteractions lernsax;
	private LyricsClient lyricsapi;
	private GLA lyricsapiold;

	private Long ownerId;
	private String vplanpw;
	private Integer schoolID;
	private JsonObject teacherslist;

	private boolean hypixelapienabled = false;
	private boolean githubapienabled = false;

	private boolean minlock = false;
	private int sec = 60;
	private int min = 0;
	private boolean vplanenabled = false;
	private boolean blockevents = false;
	private boolean exit = false;
	private boolean hasstarted = false;
	private boolean indev;

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
		LiteSQL.connect();

		SQLManager.onCreate();

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
		
		this.lernsax = new LernsaxInteractions();
		String lsaxemail;
		String lsaxtoken;
		String lsaxappid;
		
		if((lsaxemail = prop.getProperty("lsaxemail"))!=null && (lsaxtoken = prop.getProperty("lsaxtoken"))!=null && (lsaxappid = prop.getProperty("lsaxappid"))!=null){
		
		lernsax.connect(lsaxemail, lsaxtoken, lsaxappid);
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
		builder.addEventListeners(new SlashCommandAutocomplete());
		builder.addEventListeners(new ButtonListener());

		this.shardmgr = builder.build();
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

		this.cmdmgr = new CommandManager(hypixelapienabled, githubapienabled);
		this.hypmgr = new HypixelCommandManager();
		this.slashmgr = new SlashCommandManager();

		this.submgr = new SubscriptionManager();

		this.syschannels = new SystemNotificationChannelHolder();

		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerutil = new AudioPlayerUtil();

		this.vplan = new VplanNEW_XML();

		this.lyricsapi = new LyricsClient();
		this.lyricsapiold = new GLA();

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
		shardmgr.getShards().forEach(jda -> {

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
		if (this.shardmgr != null) {

			this.API.shutdown();
			this.lernsax.disconnect();
			StatsCategoryCommand.onShutdown(indev);
			this.shardmgr.setStatus(OnlineStatus.OFFLINE);
			logger.info("Bot offline");
			this.shardmgr.shutdown();
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
				this.getsyschannell().checkSysChannelList();

				if (this.vplanenabled) {
					vplan.VplanNotify("10b");
				}

				lernsax.sendLernsaxEmbeds(lernsax.checkForLernplanMessages());
				Skyblocknews.onEventCheck();

				if ((!this.hasstarted)) {
					StatsCategoryCommand.onStartup(this.indev);
					this.hasstarted = true;
				}
				Random rand = new Random();

				int i = rand.nextInt(this.status.length);

				this.shardmgr.getShards().forEach(jda -> {
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
						LiteSQL.onUpdate("UPDATE botutil SET prefix='-' WHERE guildId=" + set.getLong("guildId"));
					}

				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.shardmgr.getGuilds().forEach(gu -> {

			Long id = gu.getIdLong();

			if (!this.prefixl.containsKey(id)) {
				this.prefixl.put(id, "-");
				LiteSQL.onUpdate("INSERT INTO botutil(guildId, prefix) VALUES(" + id + ", '-')");
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
			prop.setProperty("lsaxemail", "");
			prop.setProperty("lsaxtoken", "");

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

				String jsonstring = Files.readString(file.toPath());

				JsonElement json = JsonParser.parseString(jsonstring);
				teacherslist = json.getAsJsonObject();

			} catch (IOException e1) {

				logger.error(e1.getMessage(), e1);

			}

		}
	}

	public CommandManager getCmdMan() {
		return this.cmdmgr;
	}

	public HypixelCommandManager gethypMan() {
		return this.hypmgr;
	}

	public SlashCommandManager getslashMan() {
		return this.slashmgr;
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
		return this.API;
	}

	public SystemNotificationChannelHolder getsyschannell() {
		return syschannels;
	}

	public String getVplanPW() {
		return this.vplanpw;
	}

	public boolean isInExit() {
		return this.exit;
	}

	public int getSchoolID() {
		return this.schoolID;
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
		return this.shardmgr;
	}

	public AudioPlayerUtil getPlayerUtil() {
		return this.playerutil;
	}

	public AudioPlayerManager getAudioPlayerManager() {
		return this.audioPlayerManager;
	}

	public SubscriptionManager getSubscriptionManager() {
		return this.submgr;
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
