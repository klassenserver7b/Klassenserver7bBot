package de.k7bot.music.utilities.gla;

import java.io.IOException;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.json.JSONObject;

public class GLACustomHttpManager {

	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36";
	public static final String HTTP_HEADER_REFERER = "referer";

	public GLACustomHttpManager() {
	}

	public JSONObject performRequest(String url) throws IOException, HttpResponseException, ParseException {
		final CloseableHttpClient httpclient = HttpClients.createSystem();
		final HttpGet httpget = new HttpGet(url);
		httpget.setHeader(HttpHeaders.ACCEPT_CHARSET, "utf-8");
		httpget.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON);
		httpget.setHeader(HttpHeaders.ACCEPT, "application/json");

		try {

			String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			httpclient.close();

			return new JSONObject(response);

		} catch (HttpResponseException e) {
			throw e;

		}

	}

}
