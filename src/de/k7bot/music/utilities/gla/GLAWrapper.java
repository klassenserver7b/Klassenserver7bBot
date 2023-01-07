/**
 * 
 */
package de.k7bot.music.utilities.gla;

import java.io.IOException;

import org.apache.hc.core5.http.ParseException;

/**
 * @author Felix
 *
 */
public class GLAWrapper {

	private GLACustomHttpManager httpManager = new GLACustomHttpManager();

	/**
	 * 
	 */
	public GLAWrapper() {
		super();
	}

	public GLACustomSongSearch search(String query) throws ParseException, IOException {
		return new GLACustomSongSearch(this, query);
	}

	public GLACustomHttpManager getHttpManager() {
		return this.httpManager;
	}

}
