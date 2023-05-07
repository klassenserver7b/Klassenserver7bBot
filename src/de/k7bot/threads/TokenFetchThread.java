/**
 *
 */
package de.k7bot.threads;

import java.io.IOException;
import java.util.Date;

import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.k7bot.Klassenserver7bbot;

/**
 * @author Felix
 *
 */
public class TokenFetchThread implements Runnable {

	private Thread t;
	private final Logger log;
	private long expires;
	private final String cookie;
	private static TokenFetchThread INSTANCE;
	private int errorcount;
	private int errorstage;

	/**
	 *
	 */
	private TokenFetchThread(String cookie) {

		errorcount = 0;
		errorstage = 1;
		INSTANCE = this;
		log = LoggerFactory.getLogger(this.getClass());
		this.cookie = cookie;
		t = new Thread(this, "Spotify_Token_Fetch");
		t.start();

	}

	public void restart() {

		t.interrupt();

		t = new Thread(this, "Spotify_Token_Fetch");
		log.info("Fetchthread restarted");
		t.start();

	}

	@Override
	public void run() {

		while (!Klassenserver7bbot.getInstance().isInExit() && !t.isInterrupted()) {

			if (!(this.expires >= new Date().getTime() - 30000)) {
				refreshToken();
				log.debug("spotify_authcode_refresh");
			}
			try {
				Thread.sleep(29000);
			}
			catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
			}
		}
		if (Klassenserver7bbot.getInstance().isInExit()) {
			return;
		}

	}

	public void shutdown() {

		if (t != null && !t.isInterrupted()) {
			t.interrupt();
			INSTANCE = null;
		}

	}

	/**
	 *
	 */
	public void refreshToken() {

		String url = "https://open.spotify.com/get_access_token";

		try (final CloseableHttpClient httpclient = HttpClients.createSystem()) {

			final HttpGet httpget = new HttpGet(url);

			httpget.setHeader("Cookie", cookie);
			httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			final String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);

			httpclient.close();

			Klassenserver7bbot.getInstance().getSpotifyinteractions().getSpotifyApi()
					.setAccessToken(elem.getAsJsonObject().get("accessToken").getAsString());
			expires = elem.getAsJsonObject().get("accessTokenExpirationTimestampMs").getAsLong();
			errorcount = 0;
			errorstage = 1;

		}
		catch (HttpHostConnectException e1) {
			log.warn("Invalid response from " + url);
			timeoutFetch();
			return;

		}
		catch (IOException | JsonSyntaxException e) {
			log.error(e.getMessage(), e);
			timeoutFetch();
			return;

		}
	}

	protected void timeoutFetch() {
		errorcount++;

		if (errorcount >= 10) {
			long add = 120000 * errorstage;
			expires = new Date().getTime() + add;
			log.warn("TokenFetch failed - retrying in " + (add / 1000) + "s");
			errorstage++;
		}
	}

	public static TokenFetchThread getINSTANCE() {

		if (INSTANCE == null) {
			throw new NullPointerException();
		}

		return INSTANCE;
	}

	public static TokenFetchThread getINSTANCE(String cookie) {
		if (INSTANCE != null) {
			return INSTANCE;
		}

		return INSTANCE = new TokenFetchThread(cookie);
	}

}
