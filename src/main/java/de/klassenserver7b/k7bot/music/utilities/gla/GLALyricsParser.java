package de.klassenserver7b.k7bot.music.utilities.gla;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public class GLALyricsParser {

	private static final String GENIUS_EMBED_URL_HEAD = "https://genius.com/songs/";
	private static final String GENIUS_EMBED_URL_TAIL = "/embed.js";

	public String get(String id) {
		try {
			return parseLyrics(id);
		} catch (IOException e) {
			return null;
		}
	}

	private String parseLyrics(String id) throws IOException {

		try (final CloseableHttpClient httpclient = HttpClients.createSystem()) {
			final HttpGet httpget = new HttpGet(GENIUS_EMBED_URL_HEAD + id + GENIUS_EMBED_URL_TAIL);

			final String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());
			return getReadable(response);
		}

	}

	private String getReadable(String rawLyrics) {
		// Remove start
		rawLyrics = rawLyrics
				.replaceAll("[\\S\\s]*<div class=\\\\\\\\\\\\\"rg_embed_body\\\\\\\\\\\\\">[ (\\\\\\\\n)]*", "");
		// Remove end
		rawLyrics = rawLyrics.replaceAll("[ (\\\\\\\\n)]*<\\\\/div>[\\S\\s]*", "");
		// Remove tags between
		rawLyrics = rawLyrics.replaceAll("<[^<>]*>", "");
		// Unescape spaces
		rawLyrics = rawLyrics.replaceAll("\\\\\\\\n", "\n");
		// Unescape '
		rawLyrics = rawLyrics.replaceAll("\\\\'", "'");
		// Unescape "
		rawLyrics = rawLyrics.replaceAll("\\\\\\\\\\\\\"", "\"");
		// replace &amp;
		rawLyrics = rawLyrics.replaceAll("&amp;", "&");
		return rawLyrics;
	}
}
