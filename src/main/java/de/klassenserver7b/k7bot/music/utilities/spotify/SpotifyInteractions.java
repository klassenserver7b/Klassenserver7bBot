/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities.spotify;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.threads.TokenFetchThread;
import se.michaelthelin.spotify.SpotifyApi;

/**
 * @author K7
 *
 */
public class SpotifyInteractions {

	private String cookie;
	private boolean apienabled;
	private SpotifyApi spotifyApi;
	public TokenFetchThread fetchthread;

	public SpotifyInteractions() {

		apienabled = false;

		if (!initialize()) {
			return;
		}

		startfetchcycle();

	}

	/**
	 *
	 * @return
	 */
	public boolean initialize() {

		this.spotifyApi = new SpotifyApi.Builder().build();

		cookie = Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("spotify-cookie");

		if (cookie == null || cookie.isBlank()) {
			return false;
		}

		if (cookie != null && !cookie.isBlank()) {
			apienabled = true;
			return true;
		}

		return false;
	}

	/**
	 *
	 */
	public void startfetchcycle() {

		this.fetchthread = TokenFetchThread.getINSTANCE(cookie);

	}

	public void shutdown() {
		fetchthread.shutdown();
	}

	public void restart() {
		fetchthread.restart();
	}

	public boolean isApienabled() {
		return apienabled;
	}

	public SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}

}