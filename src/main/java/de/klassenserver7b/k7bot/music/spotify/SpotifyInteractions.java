/**
 *
 */
package de.klassenserver7b.k7bot.music.spotify;

import de.klassenserver7b.k7bot.threads.TokenFetchThread;
import se.michaelthelin.spotify.SpotifyApi;

/**
 * @author K7
 */
public class SpotifyInteractions {

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
     * @return true if the api was successfully initialized
     */
    public boolean initialize() {

        this.spotifyApi = new SpotifyApi.Builder().build();
        this.apienabled = true;
        return true;
    }

    /**
     *
     */
    public void startfetchcycle() {

        this.fetchthread = TokenFetchThread.getINSTANCE();

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