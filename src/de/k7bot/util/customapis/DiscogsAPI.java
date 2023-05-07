/**
 *
 */
package de.k7bot.util.customapis;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.io.CloseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.utilities.SongJson;

/**
 * @author Klassenserver7b
 *
 */
public class DiscogsAPI {

	private final Logger log;

	public DiscogsAPI() {

		log = LoggerFactory.getLogger(this.getClass());

	}

	public SongJson getFilteredSongJson(String searchquery) throws IllegalArgumentException {

		if (!this.isApiEnabled()) {
			return null;
		}

		JsonObject songjson = getSongJson(searchquery);

		if (songjson == null) {
			return null;
		}

		String title = songjson.get("title").getAsString();
		JsonArray artists = songjson.get("artists").getAsJsonArray();
		String year = songjson.get("year").getAsString();
		String url = songjson.get("uri").getAsString();
		String apiurl = songjson.get("resource_url").getAsString();

		int dist = LevenshteinDistance.getDefaultInstance().apply(artists + " " + title, searchquery);
		int rotdist = LevenshteinDistance.getDefaultInstance().apply(title + " " + artists, searchquery);
		int titledist = LevenshteinDistance.getDefaultInstance().apply(title, searchquery);

		if (dist > 10 && rotdist > 10 && titledist > 10) {
			return null;
		}

		return SongJson.of(title, artists, year, url, apiurl);

	}

	private JsonObject getSongJson(String searchquery) {

		assert this.isApiEnabled();

		String res_url = getMasterJson(searchquery);

		if (res_url == null) {
			return null;
		}

		try (final CloseableHttpClient httpclient = HttpClients.createSystem()) {
			final HttpGet httpget = new HttpGet(res_url);

			return request(httpclient, httpget);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private String getMasterJson(String searchquery) {

		assert this.isApiEnabled();

		JsonObject queryresults = getQueryResults(searchquery);

		if (queryresults == null) {
			return null;
		}

		JsonArray results = queryresults.get("results").getAsJsonArray();

		if (results.size() < 1) {
			return null;
		}

		for (int i = 0; i < results.size(); i++) {

			if (results.get(i).getAsJsonObject().get("type").getAsString().equalsIgnoreCase("release")) {
				return results.get(i).getAsJsonObject().get("resource_url").getAsString();
			}

		}

		return null;
	}

	private JsonObject getQueryResults(String searchquery) {

		assert this.isApiEnabled();

		String token = Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("discogs-token");

		String preparedquery = URLEncoder.encode(searchquery, StandardCharsets.UTF_8);
		final HttpGet httpget = new HttpGet(
				"https://api.discogs.com/database/search?query=" + preparedquery + "&per_page=3&page=1");
		httpget.setHeader(HttpHeaders.AUTHORIZATION, "Discogs token=" + token);

		try (final CloseableHttpClient httpclient = HttpClients.createSystem()) {

			return request(httpclient, httpget);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return null;

	}

	private JsonObject request(CloseableHttpClient httpclient, HttpGet httpget) {

		try {
			final String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);
			httpclient.close();

			return elem.getAsJsonObject();

		} catch (HttpHostConnectException e1) {
			log.warn("Invalid response from api.dicogs.com" + e1.getMessage());
		}

		catch (IOException | JsonSyntaxException e) {
			log.error(e.getMessage(), e);

			httpclient.close(CloseMode.GRACEFUL);

		}
		return null;
	}

	public boolean isApiEnabled() {

		if (!Klassenserver7bbot.getInstance().getPropertiesManager().isApiEnabled("discogs")) {
			log.error("Invalid Discogs Token - API Disabled", new Throwable().fillInStackTrace());
		}

		return Klassenserver7bbot.getInstance().getPropertiesManager().isApiEnabled("discogs");

	}

}
