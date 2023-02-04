package de.k7bot.music.lavaplayer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.utilities.SongDataUtils;
import de.k7bot.music.utilities.SongJson;
import de.k7bot.sql.LiteSQL;

public class Queue {
	private boolean islooped = false;
	private List<AudioTrack> looplist;
	private List<AudioTrack> queuelist;
	private MusicController controller;
	private SongJson currentSong;
	private final SongDataUtils songutils;
	private final Logger log;

	public Queue(MusicController controller) {
		setController(controller);
		setQueuelist(new ArrayList<>());
		this.songutils = new SongDataUtils();
		this.currentSong = null;
		this.log = LoggerFactory.getLogger(this.getClass());
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

				logNewTrack(track);
				player.playTrack(track);

				return true;

			}

		} else if (!this.queuelist.isEmpty()) {

			if (this.islooped) {

				TrackScheduler.next = true;
				track = this.queuelist.remove(0);

				logNewTrack(track);
				player.playTrack(track);

				this.queuelist = this.looplist;
				TrackScheduler.next = false;
				return true;

			} else {

				TrackScheduler.next = true;
				track = this.queuelist.remove(0);

				logNewTrack(track);
				player.playTrack(track);

				TrackScheduler.next = false;

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

		try {

			String datetimestring = LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
			long datetime = Long.parseLong(datetimestring);

			if (track instanceof YoutubeAudioTrack) {
				SongJson data = songutils.parseYtTitle(track.getInfo().title, track.getInfo().author);
				this.currentSong = data;
				if (this.currentSong != null) {
					LiteSQL.onUpdate(
							"INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES(?, ?, ?, ?);",
							data.getTitle(), data.getAuthorString(), controller.getGuild().getIdLong(), datetime);
				} else {
					LiteSQL.onUpdate(
							"INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES(?, ?, ?, ?);",
							track.getInfo().title, track.getInfo().author, controller.getGuild().getIdLong(), datetime);
				}
			} else {

				this.currentSong = null;

				LiteSQL.onUpdate("INSERT INTO musiclogs(songname, songauthor, guildId, timestamp) VALUES(?, ?, ?, ?);",
						track.getInfo().title, track.getInfo().author, controller.getGuild().getIdLong(), datetime);

			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public void replace(AudioTrack track) {

		clearQueue();
		TrackScheduler.next = true;
		queuelist.add(track);
		next(track);
		TrackScheduler.next = false;

	}

	public void replace(AudioPlaylist playlist) {

		clearQueue();
		TrackScheduler.next = true;

		queuelist = playlist.getTracks();

		next(playlist.getTracks().get(0));
		TrackScheduler.next = false;

	}

	public void addPlaylistToQueue(AudioPlaylist playlist) {

		queuelist.addAll(playlist.getTracks());

		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.getInstance().getMainLogger()
					.debug("Queue - setNextTrack: playing track = null -> next(track)");
			next(queuelist.get(0));
		}

	}

	public void addTrackToQueue(AudioTrack track) {
		this.queuelist.add(track);

		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.getInstance().getMainLogger()
					.debug("Queue - add Track to Queue: playing track = null -> next(track)");
			next(track);
		}
	}

	public void setNextPlaylist(AudioPlaylist playlist) {

		queuelist.addAll(0, playlist.getTracks());
		if (this.controller.getPlayer().getPlayingTrack() == null) {
			Klassenserver7bbot.getInstance().getMainLogger()
					.debug("Queue - setNextTrack: playing track = null -> next(track)");
			next(queuelist.get(0));
		}

	}

	public void setNextTrack(AudioTrack track) {
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

	public SongDataUtils getSongDataUtils() {
		return songutils;
	}

	public SongJson getCurrentSongData() {
		return this.currentSong;
	}
}
