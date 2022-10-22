package de.k7bot.music.commands.common;

import java.awt.Color;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import core.GLA;
import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.SongJson;
import genius.SongSearch;
import genius.SongSearch.Hit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Lyricsoldcommand implements ServerCommand {

	private final Logger log;

	public Lyricsoldcommand() {
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

			GLA lapi = Klassenserver7bbot.getInstance().getLyricsAPIold();
			SongSearch search = null;

			try {

				Queue queue = controller.getQueue();
				SongJson data = queue.getCurrentSongData();

				search = lapi.search(
						(URLEncoder.encode(data.getTitle() + " " + data.getAuthorString(), StandardCharsets.UTF_8)));

				log.info("Searching Old-Lyrics Querry: " + data.getTitle() + " - " + data.getAuthorString());

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (!search.getHits().isEmpty()) {
				Hit hit = search.getHits().getFirst();
				channel.sendTyping().queue();

				EmbedBuilder builder = new EmbedBuilder();

				builder.setThumbnail(hit.getThumbnailUrl());
				builder.setAuthor(channel.getGuild().getSelfMember().getEffectiveName());
				builder.setTitle("Lyrics of " + hit.getTitle() + " from " + hit.getArtist().getName());
				builder.setFooter("Requested by @" + m.getEffectiveName() + " | Lyrics by Genius");
				builder.setTimestamp(OffsetDateTime.now());
				builder.setColor(Color.decode("#14cdc8"));

				builder.setDescription(hit.fetchLyrics());

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
		String help = "Siehe lyrics! - Unterstützt nur Genius als Lyrics-Provider (findet weniger als der Haupt-command), besitzt aber ein Embed-icon und bessere Lyrics-Gliederung (in z.B. Chorus, pre-Chorus, Strophe, etc.)";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

}
