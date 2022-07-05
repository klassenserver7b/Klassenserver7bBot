package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.util.SpotifyConverter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements ServerCommand {
	public static boolean next = false;
	public static boolean party = false;

	public static final SpotifyConverter conv = new SpotifyConverter();

	public void performCommand(Member m, TextChannel channel, Message message) {
		
		String[] args = message.getContentDisplay().split(" ");

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
				if (args.length > 1) {

					queue.unLoop();

					Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);

					StringBuilder strBuilder = new StringBuilder();

					ResultSet set = Klassenserver7bbot.INSTANCE.getDB()
							.onQuery("SELECT volume FROM botutil WHERE guildId = " + channel.getGuild().getIdLong());

					try{
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
						strBuilder.append(args[i]);
						strBuilder.append(" ");
					}

					String url = strBuilder.toString().trim();

					if (url.equalsIgnoreCase("party")) {

						url = "https://www.youtube.com/playlist?list=PLAzC6gV-_NVO39WbWU5K76kczhRuKOV9F";
						party = true;

					} else if (url.startsWith("https://open.spotify.com/playlist/")) {
						
						queue.clearQueue();
						manager.openAudioConnection(vc);
						
						Message load = channel.sendMessage("Loading Spotify Tracks...").complete();
						channel.sendTyping().queue();

						url = url.replaceAll("https://open.spotify.com/playlist/", "");
						conv.convertPlaylist(url, load, vc);

						return;
						
					} else if (url.startsWith("lf: ")) {

						url = url.substring(4);

					} else if (!(url.startsWith("http") || url.startsWith("scsearch: ")
							|| url.startsWith("ytsearch: "))) {
						url = "ytsearch: " + url;
					}

					if (player.getPlayingTrack() == null) {

						queue.clearQueue();
						manager.openAudioConnection(vc);

						Klassenserver7bbot.INSTANCE.getMainLogger()
								.info("Bot startet searching a track: no current track -> new Track(channelName = "
										+ vc.getName() + ", url = " + url + ")");

						try {
							apm.loadItem(url, new AudioLoadResult(controller, url, false)).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}

					} else {

						queue.clearQueue();

						next = true;
						player.stopTrack();
						Klassenserver7bbot.INSTANCE.getMainLogger().info(
								"Bot startet searching a track: overwriting current track -> new Track(channelName = "
										+ vc.getName() + ", url = " + url + ")");
						try {
							apm.loadItem(url, new AudioLoadResult(controller, url, false)).get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
						player.setPaused(false);
						next = false;
					}

				} else {

					if (!queue.emptyQueueList()) {
						queue.clearQueue();
					}

					manager.openAudioConnection(vc);

					Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);

					String url = "D:\\Felix\\Desktop\\Bot\\audio.mp4";
					player.stopTrack();
					apm.loadItem(url, new AudioLoadResult(controller, url, false));
					player.setPaused(false);
				}
			} else {

				channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		} else {

			channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
		}
	}
	

	@Override
	public String gethelp() {
		return "Spielt den/die ausgewählte/-n Track / Livestream / Playlist.\n - kann nur ausgeführt werden wenn sich der Nutzer in einem Voice Channel befindet!\n - z.B. [prefix]play [url / YouTube Suchbegriff]";
	}

	@Override
	public String getcategory() {
		return "Musik";
	}
}