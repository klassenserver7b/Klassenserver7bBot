package de.klassenserver7b.k7bot.music.utilities.gla;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GLACustomSongSearch {

	private final GLAWrapper glaw;
	private int status;
	private final LinkedList<Hit> hits = new LinkedList<>();

	public GLACustomSongSearch(GLAWrapper gla, String query) throws IOException {
		this.glaw = gla;
		query = URLEncoder.encode(query, StandardCharsets.UTF_8);
		System.out.println(query);
		String url = "https://genius.com/api/search/song?page=1&q=" + query;
		request(url);
	}

	private void request(String uri) throws IOException {
		JsonObject obj;
		obj = this.glaw.getHttpManager().performRequest(uri);

		if (obj == null) {
			return;
		}

		parse(obj);
	}

	private void parse(JsonObject jRoot) {

		this.status = jRoot.getAsJsonObject("meta").get("status").getAsInt();
		JsonObject response = jRoot.getAsJsonObject("response");
		if (!(response.get("next_page") == null || response.get("next_page").isJsonNull())) {
			// FIXME warum wird das nicht genutzt?
			@SuppressWarnings("unused")
			int nextPage = response.get("next_page").getAsInt();
		}
		JsonObject section = response.getAsJsonArray("sections").get(0).getAsJsonObject();
		JsonArray hits = section.getAsJsonArray("hits");
		for (int i = 0; i < hits.size(); i++) {
			JsonObject hitRoot = hits.get(i).getAsJsonObject().getAsJsonObject("result");
			this.hits.add(new Hit(hitRoot));
		}
	}

	public int getStatus() {
		return status;
	}

	public LinkedList<Hit> getHits() {
		return hits;
	}

	public static class Hit {

		private final long id;
		private final String title;
		private final String titleWithFeatured;
		private final String url;
		private final String imageUrl;
		private final String thumbnailUrl;
		private final Artist artist;

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

	public static class Artist {

		private final long id;
		private final String imageUrl;
		private final String name;
		private final String slug;
		private final String url;

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
