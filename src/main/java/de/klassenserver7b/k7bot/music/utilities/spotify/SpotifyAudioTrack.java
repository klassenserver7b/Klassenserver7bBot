/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities.spotify;

import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.music.asms.SpotifyAudioSourceManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.io.CloseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @author K7
 */
public class SpotifyAudioTrack extends DelegatedAudioTrack {

    private final SpotifyAudioSourceManager sasm;
    private final Logger log;
    private final CloseableHttpClient httpclient;

    /**
     *
     */
    public SpotifyAudioTrack(AudioTrackInfo trackInfo, SpotifyAudioSourceManager sasm) {
        super(trackInfo);
        this.sasm = sasm;
        this.log = LoggerFactory.getLogger(getClass());
        httpclient = HttpClients.createSystem();
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {

        File decr = downloadTrack(super.getIdentifier());
        decr.deleteOnExit();

        log.info("Downloaded spotifytrack " + super.getIdentifier() + " to " + decr.getAbsolutePath());
        try (SeekableInputStream stream = new LocalSeekableInputStream(decr)) {
            new Mp3AudioTrack(trackInfo, stream).process(executor);
        }

        httpclient.close(CloseMode.IMMEDIATE);
        decr.delete();

    }

    protected File downloadTrack(String identifier) throws IOException, InterruptedException {

        log.info("Tempdir: " + sasm.getTempdir().getAbsolutePath());

        String pathstr = new File("").getAbsolutePath() + "/resources/spotify-dl";

        int exitCode = new ProcessBuilder()
                .command(
                        pathstr, "-d", sasm.getTempdir().getAbsolutePath(), "-n", identifier, "-u", Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("spotify-username"), "-p", Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("spotify-password"), "-f", "mp3", "https://open.spotify.com/track/" + identifier)
                .inheritIO().start().waitFor();

        if (exitCode != 0) {
            log.error("Spotify-DL exited with code " + exitCode);
            return null;
        }

        return new File(sasm.getTempdir().getAbsolutePath() + "/" + identifier + ".mp3");

    }
}
