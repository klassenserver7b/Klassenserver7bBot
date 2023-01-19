package de.k7bot.music.commands.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.lavaplayer.AudioLoadResult;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.lavaplayer.Queue;
import de.k7bot.music.utilities.AudioLoadOption;
import de.k7bot.music.utilities.AudioPlayerUtil;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Deprecated
public class OldPlayCommand implements ServerCommand {
	@Deprecated
	public static boolean party = false;
	@Deprecated
	private final Logger log;
	// private final SpotifyConverter conv;

	@Deprecated
	public OldPlayCommand() {

		this.log = LoggerFactory.getLogger(this.getClass());
		// conv = new SpotifyConverter();

	}

	@Override
	@Deprecated
	public String gethelp() {
		return "Spielt den/die ausgewählte/-n Track / Livestream / Playlist.\n - z.B. [prefix]play [url / YouTube Suchbegriff]";
	}

	@Override
	@Deprecated
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	@Deprecated
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.membHasVcConnection(m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());
		AudioManager manager = vc.getGuild().getAudioManager();
		AudioPlayerManager apm = Klassenserver7bbot.getInstance().getAudioPlayerManager();
		AudioPlayer player = controller.getPlayer();
		Queue queue = controller.getQueue();

		if (player.getPlayingTrack() != null && manager.getConnectedChannel() != null
				&& vc.getIdLong() != manager.getConnectedChannel().getIdLong()) {

			channel.sendMessage(
					"The Bot is already playng a song.\nPlease join the channel the bot is playing in. -> Channel: "
							+ manager.getConnectedChannel().getName() + "\n" + m.getAsMention())
					.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			return;

		}

		String[] args = message.getContentDisplay().split(" ");

		MusicUtil.updateChannel(channel);
		String url = "";

		if (args.length > 1) {

			queue.unLoop();

			setVolume(player, channel.getGuild().getIdLong());

			StringBuilder strBuilder = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				strBuilder.append(args[i]);
				strBuilder.append(" ");
			}

			url = strBuilder.toString().trim();

			url = formatQuerry(url);

			if (player.getPlayingTrack() == null) {

				Klassenserver7bbot.getInstance().getMainLogger()
						.info("Bot startet searching a track: no current track -> new Track(channelName = "
								+ vc.getName() + ", url = " + url + ")");

			} else {

				Klassenserver7bbot.getInstance().getMainLogger()
						.info("Bot startet searching a track: overwriting current track -> new Track(channelName = "
								+ vc.getName() + ", url = " + url + ")");
			}

		} else {

			url = "audio.mp4";

		}

		queue.clearQueue();
		manager.openAudioConnection(vc);

		AudioLoadResult ares = new AudioLoadResult(controller, url, AudioLoadOption.REPLACE);
		for (int i = 0; i < 5; i++) {
			if (tryLoad(url, ares, apm)) {
				log.debug("Track successfully submitted to ASM");
				break;
			} else {
				log.info("FriendlyException while loading Track - Retrying");
			}
		}

		player.setPaused(false);

	}

	@Deprecated
	private boolean tryLoad(String identifyer, AudioLoadResult ares, AudioPlayerManager apm) {
		try {
			apm.loadItem(identifyer, ares);
			return true;
		} catch (FriendlyException e) {
			return false;
		}
	}

	@Deprecated
	private String formatQuerry(String q) {

		String url = q;

		if (url.equalsIgnoreCase("party")) {

			url = "https://www.youtube.com/playlist?list=PLAzC6gV-_NVO39WbWU5K76kczhRuKOV9F";
			party = true;

		} else if (url.startsWith("lf: ")) {

			url = url.substring(4);

		} else if (!(url.startsWith("http") || url.startsWith("scsearch: ") || url.startsWith("ytsearch: "))) {
			url = "ytsearch: " + url;
		}

		return url;
	}

	@Deprecated
	private void setVolume(AudioPlayer player, Long guildId) {

		ResultSet set = LiteSQL.onQuery("SELECT volume FROM musicutil WHERE guildId = ?;", guildId);

		try {
			if (set.next()) {
				int volume = set.getInt("volume");
				if (volume != 0) {
					player.setVolume(volume);
				} else {
					LiteSQL.onUpdate("UPDATE musicutil SET volume = ? WHERE guildId = ?;",
							AudioPlayerUtil.STANDARDVOLUME, guildId);
					player.setVolume(AudioPlayerUtil.STANDARDVOLUME);
				}
			} else {
				LiteSQL.onUpdate("INSERT INTO musicutil(volume, guildId) VALUES(?,?);", AudioPlayerUtil.STANDARDVOLUME,
						guildId);
				player.setVolume(AudioPlayerUtil.STANDARDVOLUME);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

	}
}