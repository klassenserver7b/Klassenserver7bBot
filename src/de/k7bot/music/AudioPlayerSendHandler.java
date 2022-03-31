package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {
	private final AudioPlayer audioPlayer;
	private final ByteBuffer buffer;
	private final MutableAudioFrame frame;

	public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
		 this.audioPlayer = audioPlayer;
		    this.buffer = ByteBuffer.allocate(2048);
		    this.frame = new MutableAudioFrame();
		    this.frame.setBuffer(buffer);
	}

	public boolean canProvide() {
		return audioPlayer.provide(frame);
	}

	public ByteBuffer provide20MsAudio() {
		
	    ((Buffer) buffer).flip();
	    
	    return buffer;
	}

	public boolean isOpus() {
		return true;
	}
}