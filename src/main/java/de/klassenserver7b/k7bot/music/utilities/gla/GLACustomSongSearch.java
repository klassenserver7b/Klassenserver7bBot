package de.klassenserver7b.k7bot.music.utilities.gla;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedList;

public class GLACustomSongSearch {

	private final GLAWrapper glaw;
	private int status;
	private int nextPage;
	private LinkedList<Hit> hits = new LinkedList<>();

	public GLACustomSongSearch(GLAWrapper gla, String query) throws IOException {
		this.glaw = gla;
		query = URLEncoder.encode(query, "UTF-8" /*
													 * As suggested by User DimitrisStaratzis, for consistent encoding
													 * on all devices
													 */);
		System.out.println(query);
		String url = "https://genius.com/api/search/song?page=1&q=" + query;
		request(url);
	}

	public GLACustomSongSearch(GLAWrapper gla, String query, int page) throws IOException {
		this.glaw = gla;
		query = URLEncoder.encode(query, "UTF-8");
		String url = "https://genius.com/api/search/song?page=" + page + "&q=" + query;
		request(url);
	}

	private void request(String uri) throws IOException {
		JsonObject obj;
		try {
			obj = this.glaw.getHttpManager().performRequest(uri);

			if (obj == null) {
				return;
			}

			parse(obj);
		} catch (IOException e) {
			throw e;
		}
	}

	private void parse(JsonObject jRoot) {

		this.status = jRoot.getAsJsonObject("meta").get("status").getAsInt();
		JsonObject response = jRoot.getAsJsonObject("response");
		if (!(response.get("next_page") == null || response.get("next_page").isJsonNull())) {
			this.nextPage = response.get("next_page").getAsInt();
		}
		JsonObject section = response.getAsJsonArray("sections").get(0).getAsJsonObject();
		JsonArray hits = section.getAsJsonArray("hits");
		for (int i = 0; i < hits.size(); i++) {
			JsonObject hitRoot = hits.get(i).getAsJsonObject().getAsJsonObject("result");
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

		public Hit(JsonObject jRoot) {
			this.id = jRoot.get("id").getAsLong();
			this.title = jRoot.get("title").getAsString();
			this.titleWithFeatured = jRoot.get("title_with_featured").getAsString();
			this.url = jRoot.get("url").getAsString();
			this.imageUrl = jRoot.get("header_image_url").getAsString();
			this.thumbnailUrl = jRoot.get("song_art_image_thumbnail_url").getAsString();
			this.artist = new Artist(jRoot.get("primary_artist").getAsJsonObject());
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

		public Artist(JsonObject jRoot) {
			this.id = jRoot.get("id").getAsLong();
			this.imageUrl = jRoot.get("image_url").getAsString();
			this.name = jRoot.get("name").getAsString();
			this.slug = jRoot.get("slug").getAsString();
			this.url = jRoot.get("url").getAsString();
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
