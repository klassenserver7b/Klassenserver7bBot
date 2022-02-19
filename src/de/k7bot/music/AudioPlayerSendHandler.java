package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import java.nio.ByteBuffer;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;

	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
	}

	public boolean canProvide() {
		this.lastFrame = this.audioPlayer.provide();
		return (this.lastFrame != null);
	}

	public ByteBuffer provide20MsAudio() {
		return ByteBuffer.wrap(this.lastFrame.getData());
	}

	public boolean isOpus() {
		return true;
	}
}