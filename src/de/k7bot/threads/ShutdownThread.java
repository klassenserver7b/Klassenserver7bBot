/**
 * 
 */
package de.k7bot.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.utilities.SpotifyConverter;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.StatsCategorieUtil;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * @author Felix
 *
 */
public class ShutdownThread implements Runnable {

	private final Thread t;
	private final Logger log;

	public ShutdownThread() {
		log = LoggerFactory.getLogger(this.getClass());
		t = new Thread(this, this.getClass().getCanonicalName());
		t.start();
	}

	@Override
	public void run() {
		String line;

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		try {
			while ((line = reader.readLine()) != null) {
				if (line.equalsIgnoreCase("exit")) {
					Klassenserver7bbot.getInstance().setEventBlocking(true);
					Klassenserver7bbot.getInstance().setexit(true);

					onShutdown();
					reader.close();
					break;
				}
				System.out.println("Use Exit to Shutdown");
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

	}

	public void onShutdown() {
		log.info("Bot is shutting down!");

		Klassenserver7bbot instance = Klassenserver7bbot.getInstance();

		instance.setEventBlocking(true);
		SpotifyConverter.interrupt();

		instance.stopLoop();
		ShardManager shardMgr = Klassenserver7bbot.getInstance().getShardManager();

		if (shardMgr != null) {

			instance.getHypixelAPI().shutdown();
			instance.getInternalAPIManager().shutdownAPIs();

			StatsCategorieUtil.onShutdown(instance.isDevMode());

			shardMgr.setStatus(OnlineStatus.OFFLINE);

			shardMgr.shutdown();
			log.info("Bot offline");

			LiteSQL.disconnect();

			t.interrupt();

		} else {
			log.info("ShardMan was null!");
		}
	}

}
