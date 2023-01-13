/**
 *
 */
package de.k7bot.music.utilities.spotify;

import java.io.IOException;
import java.util.Date;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.k7bot.Klassenserver7bbot;
import se.michaelthelin.spotify.SpotifyApi;

/**
 * @author felix
 *
 */
public class SpotifyInteractions {

	private Logger log;
	private String cookie;
	private boolean apienabled;
	private SpotifyApi spotifyApi;
	public Thread fetchthread;
	public long expires;

	public SpotifyInteractions() {

		log = LoggerFactory.getLogger("spotifylog");
		apienabled = false;

		if (!initialize()) {
			return;
		}

		refreshToken();

		startfetchcycle();

	}

	/**
	 *
	 * @param prop
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

		this.fetchthread = new Thread(() -> {

			while (!Klassenserver7bbot.getInstance().isInExit() && !fetchthread.isInterrupted()) {

				if (!apienabled) {
					continue;
				}

				if (!(this.expires >= new Date().getTime() - 5000)) {
					refreshToken();
					log.debug("authcode_refresh");
				}
			}
			if (Klassenserver7bbot.getInstance().isInExit()) {
				return;
			}

		});
		this.fetchthread.setName("token_fetch_cycle");
		this.fetchthread.start();

	}

	/**
	 *
	 */
	public void refreshToken() {

		if (!apienabled) {
			return;
		}

		String url = "https://open.spotify.com/get_access_token";

		final CloseableHttpClient httpclient = HttpClients.createSystem();

		final HttpGet httpget = new HttpGet(url);

		httpget.setHeader("Cookie", cookie);
		httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

		try (final CloseableHttpResponse response = httpclient.execute(httpget)) {

			if (response.getCode() != 200) {
				log.warn("Invalid response from " + url);
			}

			JsonElement elem = JsonParser.parseString(EntityUtils.toString(response.getEntity()));

			response.close();
			httpclient.close();

			spotifyApi.setAccessToken(elem.getAsJsonObject().get("accessToken").getAsString());
			expires = elem.getAsJsonObject().get("accessTokenExpirationTimestampMs").getAsLong();

		} catch (IOException | JsonSyntaxException | ParseException e) {
			log.error(e.getMessage(), e);

			try {
				httpclient.close();
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}

		}
	}

	public void shutdown() {
		if (fetchthread != null) {
			fetchthread.interrupt();
		}
	}

	public boolean isApienabled() {
		return apienabled;
	}

	public SpotifyApi getSpotifyApi() {
		return spotifyApi;
	}

}