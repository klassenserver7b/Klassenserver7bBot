/**
 *
 */
package de.k7bot.music.asms.loader;

import java.util.function.Function;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.k7bot.music.utilities.spotify.SpotifyInteractions;

/**
 * @author Felix
 *
 */
public interface SpotifyTrackLoader {

	AudioTrack load(SpotifyInteractions spotifyinteract, String songid,
			Function<AudioTrackInfo, AudioTrack> trackFactory);

}
