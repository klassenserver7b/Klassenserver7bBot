package de.k7bot.music.utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import core.HttpManager;
import core.Statics;

public class GLACustomHttpManager extends HttpManager {

	public GLACustomHttpManager() {
	}

	public HttpURLConnection getConnection(URL url) throws IOException {
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty("User-Agent", Statics.USER_AGENT);
		con.setRequestProperty("accept-language", "*");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		return con;
	}

}
