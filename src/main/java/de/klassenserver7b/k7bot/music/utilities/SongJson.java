/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;

/**
 * @author Klassenserver7b
 */
public class SongJson {

    private final JsonObject json;
    private boolean isDiscogsValidated;

    private SongJson(JsonObject json) {
        this.json = json;
        this.isDiscogsValidated = false;
    }

    /**
     * @param title   The title of the song
     * @param artists The artists of the song
     * @param year    The year the song was released
     * @param url     The URL to the song
     * @param apiurl  The URL to the Discogs API
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
     * @return The base {@link JsonObject}
     */
    public JsonObject getSongJson() {
        return this.json;
    }

    /**
     * Checks if the base {@link JsonObject} contains all necessary information
     *
     * @return If the {@link SongJson} is valid
     */
    public boolean isNull() {

        if ((json.get("title") == null) || (json.get("artists") == null) || (json.get("year") == null)
                || (json.get("url") == null)) {
            return true;
        }

        return json.get("apiurl") == null;

    }

    /**
     * Fully validates the SongJson including {@link #isNull()}
     *
     * @return If the {@link SongJson} is invalid
     */
    public boolean invalidateViaHttpRequest() {

        if (isNull()) {
            return true;
        }

        try (final CloseableHttpClient httpclient = HttpClients.createSystem()) {

            final HttpGet httpget = new HttpGet(json.get("apiurl").getAsString());

            httpclient.execute(httpget, new BasicHttpClientResponseHandler());
            this.isDiscogsValidated = true;
            httpclient.close();
            return false;

        } catch (IOException e) {

            this.isDiscogsValidated = false;

            return true;
        }

    }

    /**
     * @param json The {@link JsonObject} to be validated
     * @return SongJson
     * @throws IllegalArgumentException If the {@link JsonObject} is not valid
     */
    public static SongJson of(JsonObject json) throws IllegalArgumentException {

        SongJson songjson = new SongJson(json);

        if (songjson.invalidateViaHttpRequest()) {
            throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
                    new Throwable().fillInStackTrace());
        }

        return songjson;
    }

    /**
     * @param title   The title of the song
     * @param artists The artists of the song
     * @param year    The year the song was released
     * @param url     The URL to the song
     * @param apiurl  The URL to the Discogs API
     * @return SongJson
     * @throws IllegalArgumentException If the {@link JsonObject} is not valid
     */
    public static SongJson of(String title, JsonArray artists, String year, String url, String apiurl)
            throws IllegalArgumentException {

        SongJson songjson = new SongJson(title, artists, year, url, apiurl);

        if (songjson.invalidateViaHttpRequest()) {
            throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
                    new Throwable().fillInStackTrace());
        }

        return songjson;
    }

    /**
     * @param title   The title of the song
     * @param artists The artists of the song
     * @param year    The year the song was released
     * @param url     The URL to the song
     * @param apiurl  The URL to the Discogs API
     * @return SongJson
     * @throws IllegalArgumentException If the {@link JsonObject} is not valid
     */
    public static SongJson ofUnvalidated(String title, JsonArray artists, String year, String url, String apiurl)
            throws IllegalArgumentException {

        SongJson songjson = new SongJson(title, artists, year, url, apiurl);

        if (songjson.isNull()) {
            throw new IllegalArgumentException("Submitted JsonObject was not a valid Object for SongJson",
                    new Throwable().fillInStackTrace());
        }

        return songjson;
    }

    /**
     * @param title   The title of the song
     * @param artists The artists of the song
     * @param year    The year the song was released
     * @param url     The URL to the song
     * @param apiurl  The URL to the Discogs API
     * @return SongJson
     * @throws IllegalArgumentException If the {@link JsonObject} is not valid
     */
    public static SongJson ofUnvalidated(String title, String artists, String year, String url, String apiurl)
            throws IllegalArgumentException {

        JsonArray arr = new JsonArray();

        for (String s : artists.split(", ")) {

            JsonObject obj = new JsonObject();
            obj.addProperty("name", s);
            arr.add(obj);
        }

        return ofUnvalidated(title, arr, year, url, apiurl);
    }

    /**
     * @return The title of the song
     */
    public String getTitle() {
        return json.get("title").getAsString();
    }

    /**
     * @return The artists of the song
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
     * @return The artists of the song
     */
    public JsonArray getAuthors() {
        return json.get("artists").getAsJsonArray();
    }

    /**
     * @return The year the song was released
     */
    public String getYear() {
        return json.get("year").getAsString();
    }

    /**
     * @return The URL to the song
     */
    public String getURL() {
        return json.get("url").getAsString();
    }

    public boolean isDiscogsValidated() {
        return this.isDiscogsValidated;
    }

}
