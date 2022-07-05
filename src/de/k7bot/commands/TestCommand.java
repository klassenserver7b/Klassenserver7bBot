package de.k7bot.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.hc.core5.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

public class TestCommand implements ServerCommand {
    private String accessToken;
    private Long isoexpiration;
    private String clientId;
    private final Logger logger = LoggerFactory.getLogger("Test");

    @Override
    public String gethelp() {
        return null;
    }

    @Override
    public String getcategory() {
        return null;
    }

    @Override
    public void performCommand(Member m, TextChannel channel, Message message) {

        checkAccessToken();
        final SpotifyApi spotifyapi = new SpotifyApi.Builder().setClientId("0971d").setAccessToken(accessToken).build();
        List<Track> tracklist = new ArrayList<>();

        GetPlaylistsItemsRequest getplaylistitemsrequest = spotifyapi.getPlaylistsItems("1tkmTDXCFAY1I9sb1HREJu")
                .build();

        try {

            // Abrufen wie viele Songs in Playlist und abrufen der ersten (max.100) Songs
            Paging<PlaylistTrack> playlisttracks = getplaylistitemsrequest.execute();

            // berechnen wie oft angefragt werden muss um gesamte playlist abzurufen
            int times = (playlisttracks.getTotal() / 100);

            // Laden der Items und in YTquery list packen
            PlaylistTrack[] tracks = playlisttracks.getItems();

            for (PlaylistTrack playlistTrack : tracks) {

                Track track = (Track) playlistTrack.getTrack();
                tracklist.add(track);

            }

            // abrufen aller anderer SongPakete wenn times >= 1
            for (int i = 1; i < times; i++) {
                // berechnen des Offsets
                int offset = 100 * i;

                // Definieren der neuen API Request
                getplaylistitemsrequest = spotifyapi.getPlaylistsItems("1tkmTDXCFAY1I9sb1HREJu").limit(100)
                        .offset(offset).build();

                // Laden der Items und in YTquery list packen
                Paging<PlaylistTrack> pagedplaylisttracks = getplaylistitemsrequest.execute();
                tracks = pagedplaylisttracks.getItems();
                for (PlaylistTrack playlistTrack : tracks) {

                    Track track = (Track) playlistTrack.getTrack();
                    tracklist.add(track);

                }
            }

            /*
             * Wenn for ausgeführt wurde d.h. mehr als 100 playlist items und nicht alles
             * mit for abgedeckt d.h. z.B. 410 Items -> nach for erst 400 abgerufen -> Abruf
             * der letzten 10 Items
             */

            if (times >= 1 && (playlisttracks.getTotal() % 100) != 0) {
                // Definieren der neuen API Request
                int limit = playlisttracks.getTotal() % 100;
                getplaylistitemsrequest = spotifyapi.getPlaylistsItems("1tkmTDXCFAY1I9sb1HREJu").limit(limit)
                        .offset(times * 100).build();

                // Laden der Items und in YTquery list packen
                playlisttracks = getplaylistitemsrequest.execute();
                tracks = playlisttracks.getItems();
                for (PlaylistTrack playlistTrack : tracks) {

                    Track track = (Track) playlistTrack.getTrack();
                    tracklist.add(track);

                }

            }

        } catch (ParseException | SpotifyWebApiException | IOException e) {
            e.printStackTrace();
        }

        JsonObject main = new JsonObject();

        for (Track track : tracklist) {

            JsonObject elem = new JsonObject();
            elem.addProperty("songname", track.getName());
            elem.addProperty("dance", "");
            main.add(track.getUri(), elem);

        }

        try {
            File f = new File("resources/dance.json");

            f.createNewFile();

            BufferedWriter stream = Files.newBufferedWriter(Path.of("resources/dance.json"),
                    StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

            stream.write(main.toString());

            stream.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    public void checkAccessToken() {

        if (accessToken == null || accessToken.equalsIgnoreCase("") || clientId == null
                || (isoexpiration != null && isoexpiration <= new Date().getTime())) {


                final CloseableHttpClient client = HttpClients.createDefault();
                final HttpGet httpget = new HttpGet("https://open.spotify.com/get_access_token");

            try {

                final CloseableHttpResponse response = client.execute(httpget);

                if (response.getStatusLine().getStatusCode() == 200) {

                    JsonObject resp = JsonParser.parseString(EntityUtils.toString(response.getEntity()))
                            .getAsJsonObject();

                    String token = resp.get("accessToken").getAsString();
                    if (token != null && !token.equalsIgnoreCase("")) {
                        accessToken = token;
                        isoexpiration = resp.get("accessTokenExpirationTimestampMs").getAsLong();
                        clientId = resp.get("clientId").getAsString();
                    }

                } else {
                    logger.debug("Couldn't request a new AccessToken -> bad statuscode");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getAccessToken() {

        checkAccessToken();
        logger.debug("Spotify-Accesstoken refresh requested");
        return accessToken;

    }
}
