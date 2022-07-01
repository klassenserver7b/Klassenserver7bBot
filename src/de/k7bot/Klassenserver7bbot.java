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

import de.k7bot.hypixel.HypixelCommandManager;
import de.k7bot.listener.*;
import de.k7bot.manage.*;
import de.k7bot.moderation.SystemNotificationChannelHolder;
import de.k7bot.music.MusicUtil;
import de.k7bot.music.PlayerManager;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.timed.Skyblocknews;
import de.k7bot.timed.VPlan_main;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.commands.StatsCategoryCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
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
	private final HypixelAPI API;
	private final CommandManager cmdMan;
	private final HypixelCommandManager hypMan;
	private final SlashCommandManager slashMan;
	private final Logger logger;
	private final LiteSQL sqlite;
	private final MusicUtil musicutil;
	private final LyricsClient lyricsapi;
	private final GLA lyricsapiold;
	private final SystemNotificationChannelHolder syschannels;

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
	private Long ownerId;
	private String vplanpw = "";

	String[] status = new String[] { "-help", "@K7Bot", "-getprefix" };

	public static void main(String[] args) {
		try {
			if (args.length <= 0) {
				new Klassenserver7bbot(false);
			} else {
				if (args[0].equals("--devmode")) {
					new Klassenserver7bbot(true);
				} else {
					new Klassenserver7bbot(false);
				}

			}

		} catch (LoginException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public Klassenserver7bbot(boolean indev) throws LoginException, IllegalArgumentException {
		INSTANCE = this;

		List<String> conf = new ArrayList<>();

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

		try {
			conf = Files.readAllLines(Paths.get("bot.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String[] tokenarr = conf.get(0).split("\\=");
		String token = "";

		String[] canaryTokenarr = conf.get(1).split("\\=");
		String canaryToken = "";

		String[] hypixelTokenarr = conf.get(2).split("\\=");
		String hypixelToken = "";

		String[] githubtokenarr = conf.get(3).split("\\=");
		String githubtoken = "";

		String[] owneridarr = conf.get(4).split("\\=");

		String[] shardcarr = conf.get(5).split("\\=");
		int shardc = -1;

		String[] vplanpwarr = conf.get(6).split("\\=");

		if (tokenarr.length >= 2) {
			token = tokenarr[1];
		}

		if (canaryTokenarr.length >= 2) {
			canaryToken = canaryTokenarr[1];
		} else {
			indev = false;
		}

		if (hypixelTokenarr.length >= 2) {
			hypixelToken = hypixelTokenarr[1];
			this.hypixelapienabled = true;
		}

		if (githubtokenarr.length >= 2) {
			githubtoken = githubtokenarr[1];
			this.githubapienabled = true;
		}

		if (owneridarr.length >= 2) {
			this.ownerId = Long.parseLong(owneridarr[1]);
		}

		if (vplanpwarr.length >= 2) {
			this.vplanpw = vplanpwarr[1];
			this.vplanenabled = true;
		}

		this.indev = indev;
		this.sqlite = new LiteSQL();
		this.musicutil = new MusicUtil();
		this.lyricsapi = new LyricsClient();
		this.lyricsapiold = new GLA();
		this.cmdMan = new CommandManager(hypixelapienabled, githubapienabled);
		this.hypMan = new HypixelCommandManager();

		this.syschannels = new SystemNotificationChannelHolder();

		this.audioPlayerManager = new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();
		this.logger = LoggerFactory.getLogger("K7Bot-Main");

		try {
			shardc = Integer.parseInt(shardcarr[1]);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			this.logger.info("Empty Shard-Count Config");
		}

		sqlite.connect();
		SQLManager.onCreate();
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

		String key = System.getProperty("apiKey", hypixelToken);
		this.API = new HypixelAPI(new ApacheHttpClient(UUID.fromString(key)));

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

		this.shardMan = builder.build();

		InitializeMusic(this.audioPlayerManager);
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
		this.audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
		this.slashMan = new SlashCommandManager();
		this.checkpreflist();

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
		try {
			new GitHubBuilder();
			this.github = new GitHubBuilder().withOAuthToken(githubtoken).build();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!indev) {
			logger.info("Bot was started (nondev)");
		} else {
			logger.info("Bot was started in Canary mode");
		}

		Shutdown();
		Skyblocknews.onEventCheck();
		runLoop();
	}

	public void InitializeMusic(AudioPlayerManager manager) {

		manager.registerSourceManager(new YoutubeAudioSourceManager());
		manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
		manager.registerSourceManager(new BandcampAudioSourceManager());
		manager.registerSourceManager(new VimeoAudioSourceManager());
		manager.registerSourceManager(new TwitchStreamAudioSourceManager());
		manager.registerSourceManager(new BeamAudioSourceManager());
		manager.registerSourceManager(new HttpAudioSourceManager());
		manager.registerSourceManager(new LocalAudioSourceManager());

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
		
		if(PlayCommand.conv.converter!=null) {
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
		if (!loop.isInterrupted()) {

			if ((min % 10 == 0) && !minlock) {
				minlock = true;
				this.checkpreflist();
				this.getsyschannell().checkSysChannelList();

				if (this.vplanenabled) {
					new VPlan_main(vplanpw).sendvplanMessage();
				}
				Skyblocknews.onEventCheck();

				if ((!hasstarted)) {
					StatsCategoryCommand.onStartup(indev);
					hasstarted = true;
				}
				Random rand = new Random();

				int i = rand.nextInt(status.length);

				shardMan.getShards().forEach(jda -> {
					String text = status[i].replaceAll("%members", "" + shardMan.getGuilds().get(0).getMemberCount());
					jda.getPresence().setActivity(Activity.listening(text));
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
}
