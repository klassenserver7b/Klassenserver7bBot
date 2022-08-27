package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.SongDataStripper;
import de.k7bot.util.SongTitle;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.dv8tion.jda.api.EmbedBuilder;

public class AudioLoadResult implements AudioLoadResultHandler {
	private final String uri;
	private final MusicController controller;
	private final LiteSQL sqlite = Klassenserver7bbot.INSTANCE.getDB();
	private final boolean setasnext;

	public AudioLoadResult(MusicController controller, String uri, boolean loadasnext) {
		this.uri = uri;
		this.controller = controller;
		this.setasnext = loadasnext;
	}

	public void trackLoaded(AudioTrack track) {
		
		Queue queue = this.controller.getQueue();
		String datetimestring = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
		long datetime = Long.parseLong(datetimestring);
		
		Klassenserver7bbot.INSTANCE.getMainLogger().info("Bot AudioLoadResult loaded a single track");
		addtoqueue(queue, track);

		SongTitle title = SongDataStripper.stripTitle(track.getInfo().title);
		String songname = title.getTitle().replaceAll("'", "");
		String songauthor = "";

		if (!title.containsauthor()) {
			songauthor = SongDataStripper.stripAuthor(track.getInfo().author).replaceAll("'", "");
		}
		
		EmbedBuilder builder = (new EmbedBuilder()).setColor(Color.decode("#4d05e8"))
				.setTimestamp(LocalDateTime.now()).setTitle("1 track added to queue");

		Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);

		sqlite.onUpdate("INSERT INTO musiclogs(songname, songauthor, timestamp) VALUES('" + songname + "', '"
				+ songauthor + "', " + datetime + ")");

	}

	public void playlistLoaded(AudioPlaylist playlist) {
		
		Queue queue = this.controller.getQueue();

		Klassenserver7bbot.INSTANCE.getMainLogger().info("Bot AudioLoadResult loaded a playlist");
		if (!playlist.getTracks().isEmpty()) {
			if (this.uri.startsWith("ytsearch: ")) {
				Klassenserver7bbot.INSTANCE.getMainLogger().debug("url starts with ytsearch:");

				// ytsearch liefert Liste an vorgeschlagenen Videos - nur das erste wird zur
				// Queue hinzugefügt
				AudioTrack track = playlist.getTracks().get(0);
				addtoqueue(queue, track);
				return;
			}

			if (this.uri.startsWith("scsearch: ")) {
				Klassenserver7bbot.INSTANCE.getMainLogger().debug("url starts with scsearch:");

				// scsearch liefert Liste an vorgeschlagenen Videos - nur das erste wird zur
				// Queue hinzugefügt
				AudioTrack track = playlist.getTracks().get(0);
				addtoqueue(queue, track);
				return;
			}

			int added = 0;

			for (AudioTrack track : playlist.getTracks()) {
				addtoqueue(queue, track);
				added++;
			}

			EmbedBuilder builder = (new EmbedBuilder()).setColor(Color.decode("#4d05e8"))
					.setTimestamp(LocalDateTime.now()).setTitle(added + " tracks added to queue");

			Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);

			if (PlayCommand.party) {
				queue.shuffle();
				queue.loop();
				PlayCommand.party = false;
			}
		} else {
			noMatches();
		}
	}
	
	private void addtoqueue(Queue queue, AudioTrack track) {
		
		if(setasnext) {
			queue.setplaynext(track);
		}else {
			queue.addTracktoQueue(track);
		}
		
	}

	public void noMatches() {
		Klassenserver7bbot.INSTANCE.getMainLogger().info("Bot AudioLoadResult couldn't find a matching audio track");
		EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#ff0000")).setTimestamp(LocalDateTime.now())
				.setDescription("Couldn't find the Song you Searched for! :sob:");
		Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);
	}

	public void loadFailed(FriendlyException exception) {
		Klassenserver7bbot.INSTANCE.getMainLogger().info("Bot AudioLoadResult failed to load the requested item.");
		EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#ff0000")).setTimestamp(LocalDateTime.now())
				.setDescription(exception.getMessage());
		Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);
	}
}