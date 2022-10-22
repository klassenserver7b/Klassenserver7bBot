/**
 * 
 */
package de.k7bot.music.utilities;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author Felix
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

		if (json.get("title") == null) {
			return false;
		}

		if (json.get("artists") == null) {
			return false;
		}
		if (json.get("year") == null) {
			return false;
		}
		if (json.get("url") == null) {
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

		final CloseableHttpClient httpclient = HttpClients.createDefault();
		final HttpGet httpget = new HttpGet(json.get("apiurl").getAsString());

		try {

			final CloseableHttpResponse response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() == 200) {
				httpclient.close();
				this.isDiscogsValidated = true;
				return true;
			}
			httpclient.close();
			this.isDiscogsValidated = false;
			return false;

		} catch (IOException e) {
			this.isDiscogsValidated = false;
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
		authors = authors.substring(0, authors.length() - 2);

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
