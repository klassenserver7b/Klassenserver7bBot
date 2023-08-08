/**
 *
 */
package de.klassenserver7b.k7bot.util;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.sql.SQLManager;

/**
 * @author K7
 *
 */
public class RestartUtil {

	public static void restart() {
		Klassenserver7bbot INSTANCE = Klassenserver7bbot.getInstance();

		INSTANCE.getPlayerUtil().stopAllTracks();

		SQLManager.onCreate();

		INSTANCE.getSpotifyinteractions().restart();
		INSTANCE.restartLoop();

	}

}
