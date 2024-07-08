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
        startfetchcycle();

    }

    public void initialize() {
        this.spotifyApi = new SpotifyApi.Builder().build();
        this.apienabled = true;
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