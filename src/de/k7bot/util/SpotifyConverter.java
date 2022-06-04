package de.k7bot.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.hc.core5.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

public class SpotifyConverter {

	private String accessToken;
	private Long isoexpiration;
	private String clientId;

	public void checkAccessToken() {

		if (accessToken == null || accessToken.equalsIgnoreCase("") || clientId == null ||  (isoexpiration != null && isoexpiration <= new Date().getTime())) {

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

				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public String getAccessToken() {

		checkAccessToken();
		return accessToken;

	}

	public List<AudioTrack> convertPlaylist(String playlistId) {
		checkAccessToken();
		
		final SpotifyApi spotifyapi = new SpotifyApi.Builder().setClientId(clientId).setAccessToken(accessToken)
				.build();
		final GetPlaylistsItemsRequest getPlaylistItemsRequest = spotifyapi.getPlaylistsItems(playlistId).build();
		List<String> searchquery = new ArrayList<>();
		List<AudioTrack> yttracks = new ArrayList<>();

		try {

			Paging<PlaylistTrack> playlisttracks = getPlaylistItemsRequest.execute();
			PlaylistTrack[] tracks = playlisttracks.getItems();

			for (PlaylistTrack playlistTrack : tracks) {

				Track track = (Track) playlistTrack.getTrack();
				searchquery.add(track.getName() + " - " + track.getArtists()[0].getName());

			}

			searchquery.forEach(trackinfo -> {

				AudioPlayerManager manager = new DefaultAudioPlayerManager();
				manager.registerSourceManager(new YoutubeAudioSourceManager());
				AudioLoadResultHandler handler =  new AudioLoadResultHandler() {

					@Override
					public void trackLoaded(AudioTrack track) {

						yttracks.add(track);

					}

					@Override
					public void playlistLoaded(AudioPlaylist playlist) {

						List<AudioTrack> tracklist = playlist.getTracks();

						if (!tracklist.isEmpty()) {
							yttracks.add(tracklist.get(0));
						}

					}

					@Override
					public void noMatches() {
					}

					@Override
					public void loadFailed(FriendlyException exception) {
					}

				};
				
				try {
					manager.loadItem("ytsearch: " + trackinfo, handler).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

			});

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}

		return yttracks;
	}
}
