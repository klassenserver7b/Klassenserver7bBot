package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements ServerCommand {
	public static boolean next = false;

	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		String[] args = message.getContentDisplay().split(" ");

		if (args.length > 1) {
			GuildVoiceState state;
			if ((state = m.getVoiceState()) != null) {
				AudioChannel vc;
				if ((vc = state.getChannel()) != null) {

					MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
							.getController(vc.getGuild().getIdLong());
					AudioManager manager = vc.getGuild().getAudioManager();
					AudioPlayerManager apm = Klassenserver7bbot.INSTANCE.audioPlayerManager;
					AudioPlayer player = controller.getPlayer();
					Queue queue = controller.getQueue();

					Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);

					StringBuilder strBuilder = new StringBuilder();

					ResultSet set = Klassenserver7bbot.INSTANCE.getDB()
							.onQuery("SELECT volume FROM botutil WHERE guildId = " + channel.getGuild().getIdLong());
					try {
						if (set.next()) {
							int volume = set.getInt("volume");
							if (volume != 0) {
								player.setVolume(volume);
							} else {
								Klassenserver7bbot.INSTANCE.getDB()
										.onUpdate("UPDATE botutil SET volume = 10 WHERE guildId = "
												+ channel.getGuild().getIdLong());
								player.setVolume(10);
							}
						} else {
							Klassenserver7bbot.INSTANCE.getDB().onUpdate(
									"UPDATE botutil SET volume = 10 WHERE guildId = " + channel.getGuild().getIdLong());
							player.setVolume(10);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}

					for (int i = 1; i < args.length; i++) {
						strBuilder.append(args[i] + " ");
					}

					String url = strBuilder.toString().trim();

					if (url.startsWith("lf: ")) {

						url = url.substring(4);

					} else if (!(url.startsWith("http") || url.startsWith("scsearch: ")
							|| url.startsWith("ytsearch: "))) {
						url = "ytsearch: " + url;
					}

					if (player.getPlayingTrack() == null) {
						if (!queue.emptyQueueList()) {
							queue.clearQueue();
						}
						manager.openAudioConnection(vc);

						apm.loadItem(url, new AudioLoadResult(controller, url));
					} else {

						if (!queue.emptyQueueList()) {
							queue.clearQueue();
						}
						next = true;
						player.stopTrack();
						apm.loadItem(url, new AudioLoadResult(controller, url));
						player.setPaused(false);
						next = false;
					}
				} else {

					channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
							.queueAfter(10L, TimeUnit.SECONDS);
				}
			} else {

				channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		} else {
			GuildVoiceState state;

			if ((state = m.getVoiceState()) != null) {
				AudioChannel vc;
				if ((vc = state.getChannel()) != null) {

					MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
							.getController(vc.getGuild().getIdLong());
					AudioManager manager = vc.getGuild().getAudioManager();
					AudioPlayerManager apm = Klassenserver7bbot.INSTANCE.audioPlayerManager;
					AudioPlayer player = controller.getPlayer();
					Queue queue = controller.getQueue();
					if (!queue.emptyQueueList()) {
						queue.clearQueue();
					}

					manager.openAudioConnection(vc);

					Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);

					String url = "D:\\Felix\\Desktop\\Bot\\audio.mp4";
					player.stopTrack();
					apm.loadItem(url, new AudioLoadResult(controller, url));
					player.setPaused(false);
				} else {

					channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
							.queueAfter(10L, TimeUnit.SECONDS);
				}
			} else {

				channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		}
	}
}