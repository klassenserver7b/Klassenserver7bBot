/**
 * 
 */
package de.k7bot.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.StatsCategorieUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * @author Klassenserver7b
 *
 */
public class ShutdownThread implements Runnable {

	private final Thread t;
	private final Logger log;
	private BufferedReader reader;
	private InputStreamReader sysinr;

	public ShutdownThread() {
		log = LoggerFactory.getLogger(this.getClass());

		sysinr = new InputStreamReader(System.in);
		reader = new BufferedReader(sysinr);
		t = new Thread(this, "Shutdown Thread");
		t.start();
	}

	@Override
	public void run() {

		while (!t.isInterrupted()) {

			try {
				String line;

				if (System.in.available() == 0 || !sysinr.ready()) {
					continue;
				}
				
				if ((line = reader.readLine()) != null) {

					if (line.equalsIgnoreCase("exit")) {
						Klassenserver7bbot.getInstance().setEventBlocking(true);
						Klassenserver7bbot.getInstance().setexit(true);

						reader.close();
						t.interrupt();

						this.onShutdown();
						return;
					}

					System.out.println("Use Exit to Shutdown");
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}

	}

	public void onShutdown() {

		log.info("Bot is shutting down!");

		ShardManager shardMgr = Klassenserver7bbot.getInstance().getShardManager();

		Klassenserver7bbot.getInstance().setEventBlocking(true);

		for (AudioSourceManager m : Klassenserver7bbot.getInstance().getAudioPlayerManager().getSourceManagers()) {
			m.shutdown();
		}

		if (shardMgr != null) {

			ArrayList<Object> listeners = new ArrayList<>();

			for (JDA jda : shardMgr.getShards()) {
				listeners.addAll(jda.getEventManager().getRegisteredListeners());
			}

			shardMgr.removeEventListener(listeners.toArray());

			Klassenserver7bbot.getInstance().stopLoop();

			Klassenserver7bbot.getInstance().getHypixelAPI().shutdown();
			Klassenserver7bbot.getInstance().getInternalAPIManager().shutdownAPIs();

			StatsCategorieUtil.onShutdown(Klassenserver7bbot.getInstance().isDevMode());

			shardMgr.setStatus(OnlineStatus.OFFLINE);

			shardMgr.shutdown();
			log.info("Bot offline");

			LiteSQL.disconnect();
			t.interrupt();
			try {
				reader.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
			return;

		} else {
			log.info("ShardMan was null!");
		}

	}

}
