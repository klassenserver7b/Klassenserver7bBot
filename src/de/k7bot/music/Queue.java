package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.utilities.SongDataUtils;
import de.k7bot.music.utilities.SongJson;
import de.k7bot.sql.LiteSQL;

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
	private SongJson currentSong;
	private final SongDataUtils songutils;

	public Queue(MusicController controller) {
		setController(controller);
		setQueuelist(new ArrayList<>());
		this.songutils = new SongDataUtils();
		this.currentSong = null;
	}

	public boolean emptyQueueList() {
		return this.queuelist.size() == 0;
	}

	public boolean next(AudioTrack currentTrack) {

		AudioPlayer player = this.controller.getPlayer();

		AudioTrack track;

		if (this.queuelist.size() > 1) {

			track = this.queuelist.remove(0);

			if (track != null) {

				player.playTrack(track);

				logNewTrack(track);

				return true;

			}

		} else if (!this.queuelist.isEmpty()) {

			if (this.islooped) {

				track = this.queuelist.remove(0);
				player.playTrack(track);

				logNewTrack(track);

				this.queuelist = this.looplist;
				return true;

			} else {

				track = this.queuelist.remove(0);
				player.playTrack(track);

				logNewTrack(track);

				return true;

			}

		} else if (this.islooped) {

			track = currentTrack.makeClone();
			player.playTrack(track);

			logNewTrack(track);
		}
		return false;
	}

	public void logNewTrack(AudioTrack track) {

		String datetimestring = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
		long datetime = Long.parseLong(datetimestring);

		SongJson data = songutils.parseYtTitle(track.getInfo().title, track.getInfo().author);
		this.currentSong = data;

		LiteSQL.onUpdate("INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES(?, ?, ?, ?);",
				data.getTitle(), data.getAuthorString(), controller.getGuild().getIdLong(), datetime);

	}

	public void addTracktoQueue(AudioTrack track) {
		this.queuelist.add(track);

		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.getInstance().getMainLogger()
					.debug("Queue - add Track to Queue: playing track = null -> next(track)");
			next(track);
		}
	}

	public void setplaynext(AudioTrack track) {
		this.queuelist.add(0, track);
		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.getInstance().getMainLogger()
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

	public SongJson getCurrentSongData() {
		return this.currentSong;
	}
}
