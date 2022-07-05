package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.SongDataStripper;
import de.k7bot.util.SongTitle;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Queue {
	private boolean islooped = false;
	private List<AudioTrack> looplist;
	private List<AudioTrack> queuelist;
	private MusicController controller;
	private final LiteSQL sqlite = Klassenserver7bbot.INSTANCE.getDB();

	public Queue(MusicController controller) {
		setController(controller);
		setQueuelist(new ArrayList<>());
	}

	public boolean emptyQueueList() {
		return this.queuelist.size() == 0;
	}

	public boolean next(AudioTrack currentTrack) {
		String datetimestring = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
		long datetime = Long.parseLong(datetimestring);

		AudioPlayer player = this.controller.getPlayer();

		AudioTrack track;

		if (this.queuelist.size() > 1) {

			track = this.queuelist.remove(0);

			if (track != null) {

				player.playTrack(track);

				SongTitle title = SongDataStripper.stripTitle(track.getInfo().title);
				String songname = title.getTitle().replaceAll("'", "");
				String songauthor = "";

				if (!title.containsauthor()) {
					songauthor = SongDataStripper.stripAuthor(track.getInfo().author).replaceAll("'", "");
				}

				sqlite.onUpdate("INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES('" + songname
						+ "', '" + songauthor + "', " + controller.getGuild().getIdLong() + ", " + datetime + ")");

				return true;

			}

		} else if (!this.queuelist.isEmpty()) {

			if (this.islooped) {

				track = this.queuelist.remove(0);
				player.playTrack(track);

				SongTitle title = SongDataStripper.stripTitle(track.getInfo().title);
				String songname = title.getTitle().replaceAll("'", "");
				String songauthor = "";

				if (!title.containsauthor()) {
					songauthor = SongDataStripper.stripAuthor(track.getInfo().author).replaceAll("'", "");
				}

				sqlite.onUpdate("INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES('" + songname
						+ "', '" + songauthor + "', " + controller.getGuild().getIdLong() + ", " + datetime + ")");

				this.queuelist = this.looplist;
				return true;

			} else {

				track = this.queuelist.remove(0);
				player.playTrack(track);

				SongTitle title = SongDataStripper.stripTitle(track.getInfo().title);
				String songname = title.getTitle().replaceAll("'", "");
				String songauthor = "";

				if (!title.containsauthor()) {
					songauthor = SongDataStripper.stripAuthor(track.getInfo().author).replaceAll("'", "");
				}

				sqlite.onUpdate("INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES('" + songname
						+ "', '" + songauthor + "', " + controller.getGuild().getIdLong() + ", " + datetime + ")");

				return true;

			}

		} else if (this.islooped) {

			track = currentTrack.makeClone();
			player.playTrack(track);

			SongTitle title = SongDataStripper.stripTitle(track.getInfo().title);
			String songname = title.getTitle().replaceAll("'", "");
			String songauthor = "";

			if (!title.containsauthor()) {
				songauthor = SongDataStripper.stripAuthor(track.getInfo().author).replaceAll("'", "");
			}

			sqlite.onUpdate("INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES('" + songname
					+ "', '" + songauthor + "', " + controller.getGuild().getIdLong() + ", " + datetime + ")");
		}
		return false;
	}

	public void addTracktoQueue(AudioTrack track) {
		this.queuelist.add(track);

		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.INSTANCE.getMainLogger()
					.debug("Queue - add Track to Queue: playing track = null -> next(track)");
			next(track);
		}
	}
	
	public void setplaynext(AudioTrack track) {
		this.queuelist.add(0, track);
		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.INSTANCE.getMainLogger()
					.debug("Queue - setNextTrack: playing track = null -> next(track)");
			next(track);
		}
	}

	public void loop() {
		this.islooped = true;
		this.looplist = this.queuelist;

		if (this.queuelist.size() > 1) {
			this.looplist.add(this.controller.getPlayer().getPlayingTrack());
		}

	}

	public void unLoop() {
		this.islooped = false;

		if (this.looplist != null && !this.looplist.isEmpty()) {
			this.looplist.clear();
		}
	}

	public void clearQueue() {

		if (this.queuelist != null) {

			this.queuelist.clear();

		}
	}

	public MusicController getController() {
		return this.controller;
	}

	public void setController(MusicController controller) {
		this.controller = controller;
	}

	public List<AudioTrack> getQueuelist() {
		return this.queuelist;
	}

	public void setQueuelist(List<AudioTrack> queuelist) {
		this.queuelist = queuelist;
	}

	public void setLooplist(List<AudioTrack> looplist) {
		this.looplist = looplist;
	}

	public void shuffle() {
		Collections.shuffle(this.queuelist);
	}

	public boolean isLooped() {
		return islooped;
	}
}
