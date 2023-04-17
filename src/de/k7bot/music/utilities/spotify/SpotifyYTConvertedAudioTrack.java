/**
 *
 */
package de.k7bot.music.utilities.spotify;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchMusicProvider;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

/**
 * @author Felix
 *
 */
public class SpotifyYTConvertedAudioTrack extends DelegatedAudioTrack {

	private final YoutubeAudioSourceManager ytm;

	/**
	 * @param trackInfo
	 */
	public SpotifyYTConvertedAudioTrack(AudioTrackInfo trackInfo) {
		super(trackInfo);
		ytm = new YoutubeAudioSourceManager();
	}

	@Override
	public void process(LocalAudioTrackExecutor executor) throws Exception {

		BasicAudioPlaylist pl = (BasicAudioPlaylist) new YoutubeSearchMusicProvider().loadSearchMusicResult(
				super.getInfo().author + " - " + super.getInfo().title,
				SpotifyYTConvertedAudioTrack.this::buildTrackFromInfo);
		YoutubeAudioTrack track = (YoutubeAudioTrack) pl.getTracks().get(0);

		track.process(executor);

	}

	private YoutubeAudioTrack buildTrackFromInfo(AudioTrackInfo info) {
		return new YoutubeAudioTrack(info, ytm);
	}

}
