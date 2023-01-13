/**
 *
 */
package de.k7bot.music.utilities.spotify;

import java.io.IOException;
import java.util.function.Function;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

/**
 * @author Felix
 *
 */
public class DefaultSpotifyTrackLoader implements SpotifyTrackLoader {

	private final Logger log;

	/**
	 *
	 */
	public DefaultSpotifyTrackLoader() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public AudioTrack load(SpotifyInteractions spotifyinteract, String songid,
			Function<AudioTrackInfo, AudioTrack> trackFactory) {

		if (!spotifyinteract.isApienabled()) {
			return null;
		}

		SpotifyApi api = spotifyinteract.getSpotifyApi();

		GetTrackRequest trackrequest = api.getTrack(songid).build();

		try {

			Track track = trackrequest.execute();

			String artist = SpotifyAudioSourceManager.getArtistString(track.getArtists());

			AudioTrackInfo info = new AudioTrackInfo(track.getName(), artist, track.getDurationMs(), songid,
					false, track.getUri());

			return trackFactory.apply(info);

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

}
