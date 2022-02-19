package de.k7bot;

import com.google.gdata.client.youtube.YouTubeService;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import core.GLA;
import de.k7bot.commands.StatsChannelCommand;
import de.k7bot.hypixel.HypixelCommandManager;
import de.k7bot.listener.AutoRickroll;
import de.k7bot.listener.BanListener;
import de.k7bot.listener.ChannelCreateRemoveListener;
import de.k7bot.listener.CommandListener;
import de.k7bot.listener.JoinandLeaveListener;
import de.k7bot.listener.MemesReact;
import de.k7bot.listener.ReactionListener;
import de.k7bot.listener.Role_InviteListener;
import de.k7bot.listener.SlashCommandListener;
import de.k7bot.listener.VoiceListener;
import de.k7bot.manage.CommandManager;
import de.k7bot.manage.LiteSQL;
import de.k7bot.manage.SQLManager;
import de.k7bot.manage.SlashCommandManager;
import de.k7bot.music.MusicUtil;
import de.k7bot.music.PlayerManager;
import de.k7bot.timed.Skyblocknews;
import de.k7bot.timed.VPlan_main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
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

import me.kbrewster.mojangapi.MojangAPI;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;

public class Klassenserver7bbot {
	public static Klassenserver7bbot INSTANCE;
	public HashMap<Long, String> prefixl = new HashMap<>();
	public final HypixelAPI API;
	public MojangAPI MAPI;
	public ShardManager shardMan;
	private CommandManager cmdMan;
	private HypixelCommandManager hypMan;
	private SlashCommandManager slashMan;
	private VPlan_main vmain;
	public Thread loop;
	public Thread shutdownT;
	public AudioPlayerManager audioPlayerManager;
	public PlayerManager playerManager;
	private GitHub github;
	private Logger logger;
	private LiteSQL sqlite;
	private MusicUtil musicutil;
	private LyricsClient lyricsapi;
	private GLA lyricsapiold;
	public YouTubeService ytservice;
	private Long ownerId;

	public int sec = 60;
	public int min = 0;
	String[] status = new String[] { "-help", "@K7Bot", "-getprefix" };

	public boolean imShutdown = false;
	public boolean minlock = false;
	public boolean exit = false;
	public boolean hasstarted = false;
	public boolean indev;

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

		} catch (LoginException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public Klassenserver7bbot(boolean indev) throws LoginException, IllegalArgumentException {
		INSTANCE = this;

		List<String> conf = new ArrayList<>();

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

		// String yttoken = "";
		String[] owneridarr = conf.get(4).split("\\=");

		String[] shardcarr = conf.get(5).split("\\=");
		int shardc = -1;

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
		}

		if (githubtokenarr.length >= 2) {
			githubtoken = githubtokenarr[1];
		}

		// yttoken = conf.get(6).substring(16);

		if (owneridarr.length >= 2) {
			this.ownerId = Long.parseLong(owneridarr[1]);
		}

		try {
			shardc = Integer.parseInt(shardcarr[1]);
		} catch (NumberFormatException | IndexOutOfBoundsException e) {
			shardc = -1;
		}

		this.indev = indev;
		this.sqlite = new LiteSQL();
		this.musicutil = new MusicUtil();
		this.lyricsapi = new LyricsClient();
		this.lyricsapiold = new GLA();
		// this.ytservice = new YouTubeService("Klassenserver7bBot", yttoken);
		this.cmdMan = new CommandManager();
		this.hypMan = new HypixelCommandManager();
		this.vmain = new VPlan_main();
		this.audioPlayerManager = (AudioPlayerManager) new DefaultAudioPlayerManager();
		this.playerManager = new PlayerManager();
		this.logger = LoggerFactory.getLogger("K7Bot-Main");

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
		this.API = new HypixelAPI((HypixelHttpClient) new ApacheHttpClient(UUID.fromString(key)));

		this.MAPI = new MojangAPI();

		builder.addEventListeners(new CommandListener());
		builder.addEventListeners(new VoiceListener());
		builder.addEventListeners(new ReactionListener());
		builder.addEventListeners(new Role_InviteListener());
		builder.addEventListeners(new BanListener());
		builder.addEventListeners(new JoinandLeaveListener());
		builder.addEventListeners(new ChannelCreateRemoveListener());
		builder.addEventListeners(new SlashCommandListener());
		builder.addEventListeners(new AutoRickroll());
		builder.addEventListeners(new MemesReact());

		this.shardMan = builder.build();
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
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
		runLoop();
		Skyblocknews.onEventCheck();
	}

	public void Shutdown() {
		this.shutdownT = new Thread(() -> {
			String line = "";

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
		if (this.loop != null) {
			this.loop.interrupt();
		}
		if (this.shardMan != null) {
			this.API.shutdown();
			StatsChannelCommand.onShutdown(indev);
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

			if (loop == null) {
				loop.interrupt();
				return;
			}

			if ((min % 10 == 0) && !minlock) {
				minlock = true;
				this.checkpreflist();
				this.getvmain().sendvplanMessage("next");
				// this.getvmain().sendvplanMessage("current");
				Skyblocknews.onEventCheck();

				if (min % 60 == 0) {

					/*
					 * OffsetDateTime time = OffsetDateTime.now(); if
					 * (time.getDayOfWeek().getDisplayName(TextStyle.FULL,
					 * Locale.GERMAN).toLowerCase() .equalsIgnoreCase("dienstag") ||
					 * time.getDayOfWeek().getDisplayName(TextStyle.FULL,
					 * Locale.GERMAN).toLowerCase() .equalsIgnoreCase("mittwoch")) {
					 * 
					 * if (time.getHour() >= 15 && time.getHour() <= 21) {
					 * Dechemaxx.notifymessage(); } }
					 */
				}

				if ((!hasstarted)) {
					StatsChannelCommand.onStartup(indev);
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
			while (set.next()) {
				prefixl.put(set.getLong("guildId"), set.getString("prefix"));

				if (set.getString("prefix") == null) {
					this.getDB().onUpdate("UPDATE botutil SET prefix='-'");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.shardMan.getGuilds().forEach(gu -> {

			Long id = gu.getIdLong();

			if (this.prefixl.get(id) == null) {
				this.prefixl.put(id, "-");
				this.getDB().onUpdate("INSERT INTO botutil(guildId, prefix) VALUES(" + id + ", '-')");
			}

		});

	}

	public VPlan_main getvmain() {
		return this.vmain;
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
}
