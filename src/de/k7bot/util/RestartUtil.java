/**
 *
 */
package de.k7bot.util;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.SQLManager;

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
