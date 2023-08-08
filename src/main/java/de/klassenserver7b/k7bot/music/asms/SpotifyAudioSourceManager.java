/**
 *
 */
package de.klassenserver7b.k7bot.music.asms;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.http.ExtendedHttpConfigurable;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpConfigurable;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.music.asms.loader.DefaultSpotifyPlaylistLoader;
import de.klassenserver7b.k7bot.music.asms.loader.DefaultSpotifyTrackLoader;
import de.klassenserver7b.k7bot.music.asms.loader.SpotifyPlaylistLoader;
import de.klassenserver7b.k7bot.music.asms.loader.SpotifyTrackLoader;
import de.klassenserver7b.k7bot.music.utilities.spotify.SpotifyAudioTrack;
import de.klassenserver7b.k7bot.music.utilities.spotify.SpotifyInteractions;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;

/**
 * @author Klassenserver7b
 *
 */
public class SpotifyAudioSourceManager implements AudioSourceManager, HttpConfigurable {

	/**
	 *
	 */
	private static final String URL_REGEX = "^(https?://(?:[^.]+\\.|)spotify\\.com)/(track|playlist)/([a-zA-Z0-9-_]+)/?(?:\\?.*|)$";
	private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

	private final ExtendedHttpConfigurable combinedHttpConfiguration;
	private final SpotifyPlaylistLoader playlistLoader;
	private final SpotifyTrackLoader trackLoader;
	private final Logger log;

	private SpotifyInteractions spotifyInteract;

	/**
	 *
	 * @param combinedHttpConfiguration
	 * @param playlistLoader
	 * @param spotifyInteract
	 */
	public SpotifyAudioSourceManager(ExtendedHttpConfigurable combinedHttpConfiguration,
			SpotifyPlaylistLoader playlistLoader, SpotifyTrackLoader trackLoader,
			@Nonnull SpotifyInteractions spotifyInteract) {

		this.combinedHttpConfiguration = combinedHttpConfiguration;
		this.playlistLoader = playlistLoader;
		this.trackLoader = trackLoader;
		this.spotifyInteract = spotifyInteract;
		log = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 *
	 */
	@SuppressWarnings("resource")
	public SpotifyAudioSourceManager() {

		this(HttpClientTools.createDefaultThreadLocalManager(), new DefaultSpotifyPlaylistLoader(),
				new DefaultSpotifyTrackLoader(), Klassenserver7bbot.getInstance().getSpotifyinteractions());

	}

	/**
	 *
	 */
	@Override
	public void configureRequests(Function<RequestConfig, RequestConfig> configurator) {
		combinedHttpConfiguration.configureRequests(configurator);
	}

	/**
	 *
	 */
	@Override
	public void configureBuilder(Consumer<HttpClientBuilder> configurator) {
		combinedHttpConfiguration.configureBuilder(configurator);
	}

	/**
	 *
	 */
	@Override
	public String getSourceName() {
		return "spotify";
	}

	/**
	 *
	 */
	@Override
	public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {

		if (reference == null || reference.identifier == null || !spotifyInteract.isApienabled()) {
			return null;
		}

		String url = reference.identifier;
		Matcher matcher = URL_PATTERN.matcher(url);

		if (matcher.matches()) {
			String type = matcher.group(2);
			String id = matcher.group(3);

			log.debug("Try SpotifyLoad on trackid: '" + id + "' with url: '" + reference.identifier + "'");

			switch (type) {
			case "track" -> {
				return loadTrack(id);
			}
			case "playlist" -> {
				return loadPlaylist(id);
			}

			}
		}

		return null;
	}

	/**
	 *
	 * @param trackid
	 * @return
	 */
	private AudioItem loadTrack(String trackid) {

		Function<AudioTrackInfo, AudioTrack> trackfactory = SpotifyAudioSourceManager.this::buildTrackFromInfo;

		return trackLoader.load(spotifyInteract, trackid, trackfactory);

	}

	/**
	 *
	 * @param playlistid
	 * @return
	 */
	private AudioItem loadPlaylist(String playlistid) {

		assert spotifyInteract.isApienabled();

		Function<AudioTrackInfo, AudioTrack> trackfactory = SpotifyAudioSourceManager.this::buildTrackFromInfo;

		return playlistLoader.load(spotifyInteract, playlistid, null, trackfactory);

		// BasicAudioPlaylist playlist = new BasicAudioPlaylist(trackid, null, null,
		// false);
	}

	/**
	 *
	 * @param info
	 * @return
	 */
	private SpotifyAudioTrack buildTrackFromInfo(AudioTrackInfo info) {
		return new SpotifyAudioTrack(info, this);
	}

	@Override
	public boolean isTrackEncodable(AudioTrack track) {
		return true;
	}

	@Override
	public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
		// No custom values that need saving
	}

	@Override
	public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
		return new SpotifyAudioTrack(trackInfo, this);
	}

	@Override
	public void shutdown() {
		spotifyInteract.shutdown();
	}

	public static String getArtistString(ArtistSimplified[] artists) {
		String artist = "";

		for (ArtistSimplified a : artists) {
			artist += ", " + a.getName();
		}

		artist = artist.substring(2);

		return artist;
	}

	public SpotifyInteractions getSpotifyInteract() {
		return spotifyInteract;
	}

}
