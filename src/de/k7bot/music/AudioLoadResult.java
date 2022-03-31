package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;

import java.awt.Color;
import java.time.OffsetDateTime;
import net.dv8tion.jda.api.EmbedBuilder;

public class AudioLoadResult implements AudioLoadResultHandler {
	private final String uri;
	private final MusicController controller;

	public AudioLoadResult(MusicController controller, String uri) {
		this.uri = uri;
		this.controller = controller;
	}

	public void trackLoaded(AudioTrack track) {
		this.controller.getPlayer().playTrack(track);
	}

	public void playlistLoaded(AudioPlaylist playlist) {
		Queue queue = this.controller.getQueue();

		if (this.uri.startsWith("ytsearch: ")) {

			// ytsearch liefert Liste an vorgeschlagenen Videos - nur das erste wird zur
			// Queue hinzugefügt
			queue.addTracktoQueue(playlist.getTracks().get(0));

			return;
		}
		
		if (this.uri.startsWith("scsearch: ")) {

			// scsearch liefert Liste an vorgeschlagenen Videos - nur das erste wird zur
			// Queue hinzugefügt
			queue.addTracktoQueue(playlist.getTracks().get(0));

			return;
		}

		int added = 0;

		for (AudioTrack track : playlist.getTracks()) {
			queue.addTracktoQueue(track);
			added++;
		}

		EmbedBuilder builder = (new EmbedBuilder()).setColor(Color.decode("#4d05e8")).setTimestamp(OffsetDateTime.now())
				.setTitle(added + " tracks added to queue");

		Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);
	}

	public void noMatches() {
		EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#ff0000")).setTimestamp(OffsetDateTime.now())
				.setDescription("Couldn't find the Song you Searched for! :sob:");
		Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);
	}

	public void loadFailed(FriendlyException exception) {
		EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode("#ff0000")).setTimestamp(OffsetDateTime.now())
				.setDescription(exception.getMessage());
		Klassenserver7bbot.INSTANCE.getMusicUtil().sendEmbed(this.controller.getGuild().getIdLong(), builder);
	}
}