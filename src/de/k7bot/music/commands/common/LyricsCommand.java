package de.k7bot.music.commands.common;

import java.awt.Color;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.SongJson;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class LyricsCommand implements ServerCommand {

	private final Logger log;

	public LyricsCommand() {
		log = LoggerFactory.getLogger(this.getClass().getCanonicalName());
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();

		if (player.getPlayingTrack() != null) {
			LyricsClient lapi = Klassenserver7bbot.getInstance().getLyricsAPI();
			Lyrics lyrics = null;

			try {

				Queue queue = controller.getQueue();
				SongJson data = queue.getCurrentSongData();

				lyrics = lapi.getLyrics(
						URLEncoder.encode(data.getTitle() + " " + data.getAuthorString(), StandardCharsets.UTF_8))
						.get();

				log.info("Searching Lyrics Querry: " + data.getTitle() + " - " + data.getAuthorString());

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			if (lyrics != null) {
				channel.sendTyping().queue();

				EmbedBuilder builder = new EmbedBuilder();

				builder.setAuthor(channel.getGuild().getSelfMember().getEffectiveName());
				builder.setTitle("Lyrics of " + lyrics.getTitle() + " from " + lyrics.getAuthor());
				builder.setFooter("Requested by @" + m.getEffectiveName() + " | Lyrics by " + lyrics.getSource());
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
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

}
