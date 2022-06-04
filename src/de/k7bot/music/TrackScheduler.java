
package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.music.commands.SkipCommand;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class TrackScheduler extends AudioEventAdapter {

	public void onPlayerPause(AudioPlayer player) {
	}

	public void onPlayerResume(AudioPlayer player) {

	}

	public void onTrackStart(AudioPlayer player, AudioTrack track) {
		if (!SkipCommand.onskip) {

			long guildid = Klassenserver7bbot.INSTANCE.playerManager.getGuildbyPlayerHash(player.hashCode());
			Guild guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildid);
			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(Color.decode("#00e640"));
			AudioTrackInfo info = track.getInfo();
			builder.setTimestamp(OffsetDateTime.now());
			builder.setDescription(" Jetzt läuft: " + info.title);

			long sekunden = info.length / 1000L;
			long minuten = sekunden / 60L;
			long stunden = minuten / 60L;
			minuten %= 60L;
			sekunden %= 60L;

			String url = info.uri;
			builder.addField(info.author, "[" + info.title + "](" + url + ")", false);
			builder.addField("length: ",
					info.isStream ? "LiveStream"
							: (((stunden > 0L) ? (stunden + "h ") : "")
									+ ((minuten > 0L) ? (minuten + "min ") : "") + sekunden + "s"),
					true);
			if (url.startsWith("https://www.youtube.com/watch?v=")) {
				String videoId = url.replace("https://www.youtube.com/watch?v=", "").trim();

				try {
					InputStream file = (new URL("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg"))
							.openStream();
					builder.setImage("attachment://thumbnail.png");
					ResultSet set = Klassenserver7bbot.INSTANCE.getDB()
							.onQuery("SELECT * FROM musicchannel WHERE guildId = " + guildid);

					try {
						if (set.next()) {

							long channelid = set.getLong("channelId");
							TextChannel channel;
							if (guild != null && (channel = guild.getTextChannelById(channelid)) != null) {
								channel.sendTyping().queue();
								channel.sendFile(file, "thumbnail.png").setEmbeds(builder.build()).complete();
							}

						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(guildid, builder);
			}
		}
	}

	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

		long guildid = Klassenserver7bbot.INSTANCE.playerManager.getGuildbyPlayerHash(player.hashCode());
		Guild guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildid);
		MusicController controller = Klassenserver7bbot.INSTANCE.playerManager.getController(guildid);
		Queue queue = controller.getQueue();
		AudioManager manager = null;
		if (guild != null) {
			manager = guild.getAudioManager();
		}

		if (endReason.mayStartNext) {

			if (queue.next(track)) {
				return;
			}
			player.stopTrack();
			manager.closeAudioConnection();

		} else {

			if (queue.emptyQueueList() && !PlayCommand.next) {

				player.stopTrack();
				manager.closeAudioConnection();

			}

		}

	}
}