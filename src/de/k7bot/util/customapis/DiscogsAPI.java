/**
 * 
 */
package de.k7bot.util.customapis;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.utilities.SongJson;

/**
 * @author Felix
 *
 */
public class DiscogsAPI {

	private final Logger log;

	public DiscogsAPI() {

		log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

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

		return SongJson.of(title, artists, year, url, apiurl);

	}

	private JsonObject getSongJson(String searchquery) {

		if (!this.isApiEnabled()) {
			return null;
		}

		JsonObject master = getMasterJson(searchquery);

		if (master == null) {
			return null;
		}

		String releaseurl = master.get("main_release_url").getAsString();

		final CloseableHttpClient httpclient = HttpClients.createDefault();
		final HttpGet httpget = new HttpGet(releaseurl);

		try {

			final CloseableHttpResponse response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() != 200) {
				log.warn("Invalid response from api.dicogs.com");
				return null;
			}

			JsonElement elem = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
			httpclient.close();

			return elem.getAsJsonObject();

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private JsonObject getMasterJson(String searchquery) {

		if (!this.isApiEnabled()) {
			return null;
		}

		JsonObject queryresults = getQueryResults(searchquery);

		if (queryresults == null) {
			return null;
		}

		JsonArray results = queryresults.get("results").getAsJsonArray();

		if (results.size() < 1) {
			return null;
		}

		JsonElement masterjsonurl = results.get(0).getAsJsonObject().get("master_url");

		if (masterjsonurl.isJsonNull()) {

			JsonObject obj = new JsonObject();
			obj.addProperty("iscustom", true);

			JsonElement urljson = results.get(0).getAsJsonObject().get("resource_url");

			if (urljson.isJsonNull()) {
				return null;
			}

			obj.addProperty("main_release_url", urljson.getAsString());

			return obj;

		}

		String masterurl = masterjsonurl.getAsString();

		final CloseableHttpClient httpclient = HttpClients.createDefault();
		final HttpGet httpget = new HttpGet(masterurl);

		try {

			final CloseableHttpResponse response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() != 200) {
				log.warn("Invalid response from api.dicogs.com");
				return null;
			}

			JsonElement elem = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
			httpclient.close();

			return elem.getAsJsonObject();

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private JsonObject getQueryResults(String searchquery) {

		if (!this.isApiEnabled()) {
			return null;
		}

		final CloseableHttpClient httpclient = HttpClients.createDefault();

		String token = Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("discogs-token");

		String preparedquery = URLEncoder.encode(searchquery, StandardCharsets.UTF_8);

		final HttpGet httpget = new HttpGet(
				"https://api.discogs.com/database/search?query=" + preparedquery + "&per_page=3&page=1");
		httpget.setHeader(HttpHeaders.AUTHORIZATION, "Discogs token=" + token);

		try {

			final CloseableHttpResponse response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() != 200) {
				log.warn("Invalid response from api.dicogs.com");
				return null;
			}

			JsonElement elem = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
			httpclient.close();

			return elem.getAsJsonObject();

		} catch (IOException e) {
			log.error(e.getMessage(), e);
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
