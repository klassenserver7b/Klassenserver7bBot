package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import de.k7bot.Klassenserver7bbot;

public class MusicInitializer {

	public static void onStartUp() {

		AudioPlayerManager manager = Klassenserver7bbot.INSTANCE.audioPlayerManager;

		manager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.LOW);
		manager.registerSourceManager(new YoutubeAudioSourceManager());
		manager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
		manager.registerSourceManager(new BandcampAudioSourceManager());
		manager.registerSourceManager(new VimeoAudioSourceManager());
		manager.registerSourceManager(new TwitchStreamAudioSourceManager());
		manager.registerSourceManager(new BeamAudioSourceManager());
		manager.registerSourceManager(new HttpAudioSourceManager());
		manager.registerSourceManager(new LocalAudioSourceManager());

	}
}
