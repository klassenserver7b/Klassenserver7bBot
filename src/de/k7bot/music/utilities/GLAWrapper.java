/**
 * 
 */
package de.k7bot.music.utilities;

import java.io.IOException;

import core.GLA;
import core.HttpManager;
import genius.SongSearch;

/**
 * @author Felix
 *
 */
public class GLAWrapper extends GLA {

	private GLACustomHttpManager httpManager = new GLACustomHttpManager();

	/**
	 * 
	 */
	public GLAWrapper() {
		super();
	}

	public SongSearch search(String query) throws IOException {
		return new SongSearch(this, query);
	}

	public HttpManager getHttpManager() {
		return this.httpManager;
	}

}
