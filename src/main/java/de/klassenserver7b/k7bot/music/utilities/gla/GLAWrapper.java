/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities.gla;

import java.io.IOException;

/**
 * @author K7
 */
public class GLAWrapper {

    private final GLACustomHttpManager httpManager = new GLACustomHttpManager();

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
