package de.k7bot.music.commands.common;

import java.awt.Color;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.lavaplayer.Queue;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.SongJson;
import de.k7bot.music.utilities.gla.GLACustomSongSearch;
import de.k7bot.music.utilities.gla.GLACustomSongSearch.Hit;
import de.k7bot.music.utilities.gla.GLAWrapper;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public class LyricsCommand implements ServerCommand {

	private final Logger log;

	public LyricsCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)
				|| !MusicUtil.isPlayingSong(channel, m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());
		Queue queue = controller.getQueue();
		SongJson data = queue.getCurrentSongData();

		String query;
		if (data != null) {
			query = data.getTitle() + " " + data.getAuthorString();
		} else {
			AudioTrackInfo info = queue.getController().getPlayer().getPlayingTrack().getInfo();
			query = info.author + " - " + info.title;
		}

		log.info("Searching Lyrics Querry: " + data.getTitle() + " - " + data.getAuthorString());

		try {

			GLACustomSongSearch genius = getGeniusLyrics(query);

			if (genius != null) {
				sendGeniusEmbed(genius, channel, m);
				return;
			}

			LyricsClient lapi = Klassenserver7bbot.getInstance().getLyricsAPI();

			Lyrics lyrics = lapi.getLyrics(data.getTitle() + " " + data.getAuthorString()).get();

			if (lyrics != null) {
				sendJLyricsEmbed(lyrics, channel, m);
				return;
			}

			sendErrorEmbed(channel);

		} catch (IOException | InterruptedException | ExecutionException | ParseException e) {
			sendErrorEmbed(channel);
		}

	}

	private void sendErrorEmbed(TextChannel c) {

		EmbedBuilder build = new EmbedBuilder();

		build.setColor(16711680);
		build.setDescription("Couldn't find the song you searched for");

		c.sendMessageEmbeds(build.build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);

	}

	private void sendGeniusEmbed(GLACustomSongSearch data, TextChannel c, Member m) {

		c.sendTyping().queue();

		if (data.getHits().isEmpty()) {
			sendErrorEmbed(c);
			return;
		}

		Hit hit = data.getHits().getFirst();

		EmbedBuilder builder = new EmbedBuilder();

		builder.setThumbnail(hit.getThumbnailUrl());
		builder.setTitle("Lyrics of " + hit.getTitle() + " from " + hit.getArtist().getName());
		builder.setFooter("Requested by @" + m.getEffectiveName() + " | Lyrics by Genius");
		builder.setTimestamp(OffsetDateTime.now());
		builder.setColor(Color.decode("#14cdc8"));

		builder.setDescription(hit.fetchLyrics());

		c.sendMessageEmbeds(builder.build()).queue();

	}

	private void sendJLyricsEmbed(Lyrics data, TextChannel c, Member m) {

		c.sendTyping().queue();

		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("Lyrics of " + data.getTitle() + " from " + data.getAuthor());
		builder.setFooter("Requested by @" + m.getEffectiveName() + " | Lyrics by " + data.getSource());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setColor(Color.decode("#14cdc8"));

		builder.setDescription(data.getContent());

		c.sendMessageEmbeds(builder.build()).queue();

	}

	private GLACustomSongSearch getGeniusLyrics(String query) throws IOException, ParseException {

		GLAWrapper lapi = Klassenserver7bbot.getInstance().getLyricsAPIold();

		return lapi.search(query);

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
