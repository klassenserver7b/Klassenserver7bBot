package de.k7bot.music.commands;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.util.SpotifyConverter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayNextCommand implements ServerCommand{

	@Override
	public String gethelp() {
		return "Lädt den/die ausgewählte/-n Track / Livestream / Playlist und fügt ihn/sie als nächsten in die Queue ein.\n - kann nur ausgeführt werden wenn sich der Nutzer in einem Voice Channel befindet!\\n - z.B. [prefix]playnext [url / YouTube Suchbegriff]";
	}

	@Override
	public String getcategory() {
		return "Musik";
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");

		GuildVoiceState state;

		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {

				if (args.length > 1) {

					MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
							.getController(vc.getGuild().getIdLong());
					AudioManager manager = vc.getGuild().getAudioManager();
					AudioPlayerManager apm = new DefaultAudioPlayerManager();
					
					apm.registerSourceManager(new YoutubeAudioSourceManager());
					apm.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
					apm.registerSourceManager(new BandcampAudioSourceManager());
					apm.registerSourceManager(new VimeoAudioSourceManager());
					apm.registerSourceManager(new TwitchStreamAudioSourceManager());
					apm.registerSourceManager(new BeamAudioSourceManager());
					apm.registerSourceManager(new HttpAudioSourceManager());
					apm.registerSourceManager(new LocalAudioSourceManager());

					if (!manager.isConnected() || controller.getPlayer().getPlayingTrack() == null) {

						channel.sendMessageEmbeds(new EmbedBuilder().setFooter("requested by @" + m.getEffectiveName())
								.setTitle("Invalid Command Usage").setColor(Color.decode("#ff0000"))
								.setDescription(
										"The Bot isn't connected to a voicechannel / isn't playing a Song!\nPLEASE USE `"
												+ Klassenserver7bbot.INSTANCE.prefixl.get(vc.getGuild().getIdLong())
												+ "play` INSTEAD!")
								.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);

						return;

					}

					StringBuilder strBuilder = new StringBuilder();

					for (int i = 1; i < args.length; i++) {
						strBuilder.append(args[i]);
						strBuilder.append(" ");
					}

					String url = strBuilder.toString().trim();

					if (url.equalsIgnoreCase("party")) {

						url = "https://www.youtube.com/playlist?list=PLAzC6gV-_NVO39WbWU5K76kczhRuKOV9F";
						PlayCommand.party=true;

					} else if (url.startsWith("https://open.spotify.com/playlist/")) {

						SpotifyConverter conv = new SpotifyConverter();
						Message load = channel.sendMessage("Loading Spotify Tracks...").complete();
						channel.sendTyping().queue();
						
						conv.convertPlaylist(url.replaceAll("https://open.spotify.com/playlist/", ""), load, vc);

						return;

					} else if (url.startsWith("lf: ")) {

						url = url.substring(4);

					} else if (!(url.startsWith("http") || url.startsWith("scsearch: ")
							|| url.startsWith("ytsearch: "))) {
						url = "ytsearch: " + url;
					}

					Klassenserver7bbot.INSTANCE.getMainLogger()
							.info("Bot startet searching a track: no current track -> new Track(channelName = "
									+ vc.getName() + ", url = " + url + ")");

					try {
						apm.loadItem(url, new AudioLoadResult(controller, url, true)).get();
							Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(vc.getGuild().getIdLong(), new EmbedBuilder().setColor(Color.decode("#4d05e8")).setDescription("1 Song successful added to queue"));
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}

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
