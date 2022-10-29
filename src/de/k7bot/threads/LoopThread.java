/**
 * 
 */
package de.k7bot.threads;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.StatsCategorieUtil;
import net.dv8tion.jda.api.entities.Activity;

/**
 * @author Felix
 *
 */
public class LoopThread implements Runnable {

	private final Thread t;
	private final Logger log;
	private boolean minlock = false;
	private int sec = 60;
	private int min = 0;
	private boolean hasstarted = false;
	private String[] status = new String[] { "-help", "@K7Bot", "-getprefix" };

	public LoopThread() {
		log = LoggerFactory.getLogger(this.getClass());
		t = new Thread(this, "Loop");
		t.start();
	}

	@Override
	public void run() {

		long time = System.currentTimeMillis();

		while (!Klassenserver7bbot.getInstance().isInExit()) {
			if (System.currentTimeMillis() >= time + 1000) {
				time = System.currentTimeMillis();

				onsecond();
			}
		}
	}

	public void stopLoop() {
		t.interrupt();
		log.info("interrupted");
	}

	private void onsecond() {
		if (!t.isInterrupted()) {

			if ((this.min % 10 == 0) && !this.minlock) {
				this.minlock = true;

				Klassenserver7bbot instance = Klassenserver7bbot.getInstance();

				instance.getInternalAPIManager().checkForUpdates();

				if ((!this.hasstarted)) {
					StatsCategorieUtil.onStartup(instance.isDevMode());
					this.hasstarted = true;
				}
				Random rand = new Random();

				int i = rand.nextInt(this.status.length);

				instance.getShardManager().getShards().forEach(jda -> {
					jda.getPresence().setActivity(Activity.listening(this.status[i]));
				});

			}

			if (sec <= 0) {

				sec = 60;
				min++;
				minlock = false;
				if (min >= 60) {
					min = 0;

				}
			} else {
				sec--;
			}
		}
	}

}
