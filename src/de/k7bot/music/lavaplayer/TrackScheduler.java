
package de.k7bot.music.lavaplayer;

import java.awt.Color;
import java.io.File;
import java.time.OffsetDateTime;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.commands.common.SkipCommand;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.SongJson;
import de.k7bot.music.utilities.spotify.SpotifyAudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter {

	public static boolean next = false;

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {

		if (!SkipCommand.onskip) {

			long guildid = Klassenserver7bbot.getInstance().getPlayerUtil().getGuildbyPlayerHash(player.hashCode());

			MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
			Queue queue = controller.getQueue();

			AudioTrackInfo info = track.getInfo();

			SongJson jsinfo = null;
			if (track instanceof YoutubeAudioTrack) {
				jsinfo = queue.getCurrentSongData();
			}

			EmbedBuilder builder = new EmbedBuilder();

			String author = (jsinfo == null ? info.author : jsinfo.getAuthorString());
			String title = (jsinfo == null ? info.title : jsinfo.getTitle());

			builder.setColor(Color.decode("#00e640"));
			builder.setTimestamp(OffsetDateTime.now());
			builder.setDescription(" Jetzt läuft: " + title);

			long sekunden = info.length / 1000L;
			long minuten = sekunden / 60L;
			long stunden = minuten / 60L;
			minuten %= 60L;
			sekunden %= 60L;

			String url = info.uri;

			if (!(track instanceof LocalAudioTrack)) {
				builder.addField("Name", "[" + author + " - " + title + "](" + url + ")", false);
			} else {
				builder.addField("Name", author + " - " + title, false);
			}
			builder.addField("Länge: ",
					info.isStream ? "LiveStream"
							: (((stunden > 0L) ? (stunden + "h ") : "") + ((minuten > 0L) ? (minuten + "min ") : "")
									+ sekunden + "s"),
					true);

			MusicUtil.sendIconEmbed(guildid, builder, track);

		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

		long guildid = Klassenserver7bbot.getInstance().getPlayerUtil().getGuildbyPlayerHash(player.hashCode());
		Guild guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
		Queue queue = controller.getQueue();

		if (track instanceof SpotifyAudioTrack) {
			new File(track.getIdentifier()).delete();
		}

		AudioManager manager;
		if (guild == null || (manager = guild.getAudioManager()) == null) {
			return;
		}

		if (endReason.mayStartNext) {

			if (queue.next(track) || player.getPlayingTrack() != null) {
				return;
			}

			player.stopTrack();
			queue.clearQueue();
			manager.closeAudioConnection();

		} else {

			if (queue.isemptyQueueList() && !next && !queue.isLooped()) {

				player.stopTrack();
				manager.closeAudioConnection();

			}

		}

	}

}