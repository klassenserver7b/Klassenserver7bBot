package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class Queue {
	private boolean islooped = false;
	private List<AudioTrack> looplist;
	private List<AudioTrack> queuelist;
	private MusicController controller;

	public Queue(MusicController controller) {
		setController(controller);
		setQueuelist(new ArrayList<>());
	}

	public boolean emptyQueueList() {
		if (this.queuelist.size() == 0) {
			return true;
		}
		return false;
	}

	@Nonnull
	public boolean next(AudioTrack currentTrack) {
		AudioPlayer player = this.controller.getPlayer();
		if (this.queuelist.size() > 1) {
			AudioTrack track = this.queuelist.remove(0);
			if (track != null) {

				if (player.getPlayingTrack() != null
						&& (player.getPlayingTrack().equals(track) || player.getPlayingTrack() == track)) {

					if (!queuelist.isEmpty()) {
						next(track);
					}

				} else {

					player.playTrack(track.makeClone());
					return true;

				}
			}

		} else if (this.islooped) {

			AudioTrack track;

			this.queuelist = this.looplist;

			if (!this.queuelist.isEmpty()) {
				track = this.queuelist.remove(0);
			} else {
				track = player.getPlayingTrack();
			}

			if (track != null) {
				this.controller.getPlayer().playTrack(track.makeClone());
				return true;
			}
			
		} else if (!this.queuelist.isEmpty()) {

			player.playTrack(this.queuelist.remove(0));
			return true;

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

	public void loop() {
		this.islooped = true;
		this.looplist = this.queuelist;
		this.looplist.add(0, controller.getPlayer().getPlayingTrack());

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
