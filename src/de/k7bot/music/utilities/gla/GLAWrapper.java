/**
 *
 */
package de.k7bot.music.utilities.gla;

import java.io.IOException;

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

	public GLACustomSongSearch search(String query) throws IOException {
		return new GLACustomSongSearch(this, query);
	}

	public GLACustomHttpManager getHttpManager() {
		return this.httpManager;
	}

}
