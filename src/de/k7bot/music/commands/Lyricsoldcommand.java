package de.k7bot.music.commands;

import java.awt.Color;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import core.GLA;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import de.k7bot.util.SongTitle;
import de.k7bot.util.SongDataStripper;
import genius.SongSearch;
import genius.SongSearch.Hit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class Lyricsoldcommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		if (player.getPlayingTrack() != null) {
			GLA lapi = Klassenserver7bbot.INSTANCE.getLyricsAPIold();
			SongSearch search = null;

			Klassenserver7bbot.INSTANCE.getMainLogger().info("Searching Old-Lyrics Querry: "
					+ SongDataStripper.stripTitle(player.getPlayingTrack().getInfo().title).getTitle());
			try {

				AudioTrackInfo info = player.getPlayingTrack().getInfo();
				SongTitle stitle = SongDataStripper.stripTitle(info.title);
				String title = stitle.getTitle();

				if (stitle.containsauthor()) {

					search = lapi.search(title);

				} else {

					search = lapi.search(info.author + " - " + title);

				}

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
	public String getcategory() {
		String category = "Musik";
		return category;
	}

}
