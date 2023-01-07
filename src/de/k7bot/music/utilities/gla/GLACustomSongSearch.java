package de.k7bot.music.utilities.gla;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;

import org.apache.hc.core5.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

public class GLACustomSongSearch {

	private final GLAWrapper glaw;
	private int status;
	private int nextPage;
	private LinkedList<Hit> hits = new LinkedList<>();

	public GLACustomSongSearch(GLAWrapper gla, String query) throws ParseException, IOException {
		this.glaw = gla;
		query = URLEncoder.encode(query, "UTF-8" /*
													 * As suggested by User DimitrisStaratzis, for consistent encoding
													 * on all devices
													 */);
		String url = "https://genius.com/api/search/song?page=1&q=" + query;
		request(url);
	}

	public GLACustomSongSearch(GLAWrapper gla, String query, int page) throws ParseException, IOException {
		this.glaw = gla;
		query = URLEncoder.encode(query, "UTF-8");
		String url = "https://genius.com/api/search/song?page=" + page + "&q=" + query;
		request(url);
	}

	private void request(String uri) throws ParseException, IOException {
		JSONObject obj;
		try {
			obj = this.glaw.getHttpManager().performRequest(uri);

			if (obj == null) {
				return;
			}

			parse(obj);
		} catch (ParseException | IOException e) {
			throw e;
		}
	}

	private void parse(JSONObject jRoot) {
		this.status = jRoot.getJSONObject("meta").getInt("status");
		JSONObject response = jRoot.getJSONObject("response");
		if (!response.isNull("next_page")) {
			this.nextPage = response.getInt("next_page");
		}
		JSONObject section = response.getJSONArray("sections").getJSONObject(0);
		JSONArray hits = section.getJSONArray("hits");
		for (int i = 0; i < hits.length(); i++) {
			JSONObject hitRoot = hits.getJSONObject(i).getJSONObject("result");
			this.hits.add(new Hit(hitRoot));
		}
	}

	public GLAWrapper getGla() {
		return glaw;
	}

	public int getStatus() {
		return status;
	}

	public int getNextPage() {
		return nextPage;
	}

	public LinkedList<Hit> getHits() {
		return hits;
	}

	public class Hit {

		private long id;
		private String title;
		private String titleWithFeatured;
		private String url;
		private String imageUrl;
		private String thumbnailUrl;
		private Artist artist;

		public Hit(JSONObject jRoot) {
			this.id = jRoot.getLong("id");
			this.title = jRoot.getString("title");
			this.titleWithFeatured = jRoot.getString("title_with_featured");
			this.url = jRoot.getString("url");
			this.imageUrl = jRoot.getString("header_image_url");
			this.thumbnailUrl = jRoot.getString("song_art_image_thumbnail_url");
			this.artist = new Artist(jRoot.getJSONObject("primary_artist"));
		}

		public long getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public String getTitleWithFeatured() {
			return titleWithFeatured;
		}

		public String getUrl() {
			return url;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public String getThumbnailUrl() {
			return thumbnailUrl;
		}

		public Artist getArtist() {
			return this.artist;
		}

		public String fetchLyrics() {
			return new GLALyricsParser().get(this.id + "");
		}

	}

	public class Artist {

		private long id;
		private String imageUrl;
		private String name;
		private String slug;
		private String url;

		public Artist(JSONObject jRoot) {
			this.id = jRoot.getLong("id");
			this.imageUrl = jRoot.getString("image_url");
			this.name = jRoot.getString("name");
			this.slug = jRoot.getString("slug");
			this.url = jRoot.getString("url");
		}

		public long getId() {
			return id;
		}

		public String getImageUrl() {
			return imageUrl;
		}

		public String getName() {
			return name;
		}

		public String getSlug() {
			return slug;
		}

		public String getUrl() {
			return url;
		}

	}
}
