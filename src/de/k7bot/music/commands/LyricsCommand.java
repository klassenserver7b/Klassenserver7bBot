package de.k7bot.music.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import de.k7bot.util.SongTitle;
import de.k7bot.util.SongDataStripper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class LyricsCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}
		
		AudioChannel vc = MusicUtil.getMembVcConnection(m);

				MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
						.getController(vc.getGuild().getIdLong());
				AudioPlayer player = controller.getPlayer();
				
				if (player.getPlayingTrack() != null) {
					LyricsClient lapi = Klassenserver7bbot.INSTANCE.getLyricsAPI();
					Lyrics lyrics = null;

					try {

						AudioTrackInfo info = player.getPlayingTrack().getInfo();
						SongTitle stitle = SongDataStripper.stripTitle(info.title);
						String title = stitle.getTitle();

						if (stitle.containsauthor()) {

							lyrics = lapi.getLyrics(title).get();
							Klassenserver7bbot.INSTANCE.getMainLogger().info("Searching Lyrics Querry: " + title);

						} else {

							lyrics = lapi.getLyrics(info.author + " - " + title).get();

							Klassenserver7bbot.INSTANCE.getMainLogger()
									.info("Searching Lyrics Querry: " + info.author + " - " + title);

						}

					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}

					if (lyrics != null) {
						channel.sendTyping().queue();

						EmbedBuilder builder = new EmbedBuilder();

						builder.setAuthor(channel.getGuild().getSelfMember().getEffectiveName());
						builder.setTitle("Lyrics of " + lyrics.getTitle() + " from " + lyrics.getAuthor());
						builder.setFooter(
								"Requested by @" + m.getEffectiveName() + " | Lyrics by " + lyrics.getSource());
						builder.setTimestamp(OffsetDateTime.now());
						builder.setColor(Color.decode("#14cdc8"));

						builder.setDescription(lyrics.getContent());

						channel.sendMessageEmbeds(builder.build()).queue();

					} else {

						EmbedBuilder build = new EmbedBuilder();

						build.setColor(16711680);
						build.setDescription("Couldn't find the song you searched for");

						channel.sendMessageEmbeds(build.build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);
					}

				} else {

					EmbedBuilder build = new EmbedBuilder();

					build.setColor(16711680);
					build.setDescription("There is no currently playing track!");

					channel.sendMessageEmbeds(build.build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);

				}
			

	}

	@Override
	public String gethelp() {
		String help = "Sendet die Lyrics des aktuell gespielten Songs in den aktuellen channel.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}

}
