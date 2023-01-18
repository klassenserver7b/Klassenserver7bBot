/**
 *
 */
package de.k7bot.music.utilities;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Klassenserver7b
 *
 */
public class SongJson {

	private final JsonObject json;
	private boolean isDiscogsValidated;

	private SongJson(JsonObject json) {
		this.json = json;
		this.isDiscogsValidated = false;
	}

	/**
	 *
	 * @param title
	 * @param artists
	 * @param year
	 * @param url
	 * @param apiurl
	 */
	private SongJson(String title, JsonArray artists, String year, String url, String apiurl) {

		JsonObject song = new JsonObject();

		song.addProperty("title", title);
		song.add("artists", artists);
		song.addProperty("year", year);
		song.addProperty("url", url);
		song.addProperty("apiurl", apiurl);

		this.json = song;
		this.isDiscogsValidated = false;

	}

	/**
	 *
	 * @return
	 */
	public JsonObject getSongJson() {
		return this.json;
	}

	/**
	 * Checks if the base {@link JsonObject} contains all necessary information
	 *
	 * @return If the {@link SongJson} is valid
	 */
	public boolean isNotnull() {

		if ((json.get("title") == null) || (json.get("artists") == null) || (json.get("year") == null)
				|| (json.get("url") == null)) {
			return false;
		}
		if (json.get("apiurl") == null) {
			return false;
		}

		return true;

	}

	/**
	 * Fully validates the SongJson including {@link #isNotnull()}
	 *
	 * @return If the {@link SongJson} is valid
	 */
	public boolean validateViaHttpRequest() {

		if (!isNotnull()) {
			return false;
		}

		final CloseableHttpClient httpclient = HttpClients.createSystem();
		final HttpGet httpget = new HttpGet(json.get("apiurl").getAsString());

		try {

			httpclient.execute(httpget, new BasicHttpClientResponseHandler());
			this.isDiscogsValidated = true;
			httpclient.close();
			return false;

		} catch (IOException e) {

			this.isDiscogsValidated = false;

			try {
				httpclient.close();
			} catch (IOException e1) {
				// do smth
			}

			return false;
		}

	}

	/**
	 *
	 * @param json
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SongJson of(JsonObject json) throws IllegalArgumentException {

		SongJson songjson = new SongJson(json);

		if (!songjson.validateViaHttpRequest()) {
			throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
					new Throwable().fillInStackTrace());
		}

		return songjson;
	}

	/**
	 *
	 * @param title
	 * @param artists
	 * @param year
	 * @param url
	 * @param apiurl
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SongJson of(String title, JsonArray artists, String year, String url, String apiurl)
			throws IllegalArgumentException {

		SongJson songjson = new SongJson(title, artists, year, url, apiurl);

		if (!songjson.validateViaHttpRequest()) {
			throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
					new Throwable().fillInStackTrace());
		}

		return songjson;
	}

	/**
	 *
	 * @param title
	 * @param artists
	 * @param year
	 * @param url
	 * @param apiurl
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SongJson ofUnvalidated(String title, JsonArray artists, String year, String url, String apiurl)
			throws IllegalArgumentException {

		SongJson songjson = new SongJson(title, artists, year, url, apiurl);

		if (!songjson.isNotnull()) {
			throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
					new Throwable().fillInStackTrace());
		}

		return songjson;
	}

	/**
	 *
	 * @param title
	 * @param artists
	 * @param year
	 * @param url
	 * @param apiurl
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static SongJson ofUnvalidated(String title, String artists, String year, String url, String apiurl)
			throws IllegalArgumentException {

		JsonArray arr = new JsonArray();

		for (String s : artists.split(", ")) {

			JsonObject obj = new JsonObject();
			obj.addProperty("name", s);
			arr.add(obj);
		}

		SongJson songjson = new SongJson(title, arr, year, url, apiurl);

		if (!songjson.isNotnull()) {
			throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
					new Throwable().fillInStackTrace());
		}

		return songjson;
	}

	/**
	 *
	 * @return
	 */
	public String getTitle() {
		return json.get("title").getAsString();
	}

	/**
	 *
	 * @return
	 */
	public String getAuthorString() {

		StringBuilder b = new StringBuilder();

		for (JsonElement e : json.get("artists").getAsJsonArray()) {
			b.append(e.getAsJsonObject().get("name").getAsString().replaceAll("\\(.*\\)", ""));
			b.append(", ");
		}

		String authors = b.toString();
		authors = authors.trim();
		authors = authors.substring(0, authors.length() - 1);

		return authors;
	}

	/**
	 *
	 * @return
	 */
	public JsonArray getAuthors() {
		return json.get("artists").getAsJsonArray();
	}

	/**
	 *
	 * @return
	 */
	public String getYear() {
		return json.get("year").getAsString();
	}

	/**
	 *
	 * @return
	 */
	public String getURL() {
		return json.get("url").getAsString();
	}

	public boolean isDiscogsValidated() {
		return this.isDiscogsValidated;
	}

}
