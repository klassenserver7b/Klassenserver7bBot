/**
 *
 */
package de.klassenserver7b.k7bot.threads;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.StatsCategoryUtil;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author Klassenserver7b
 *
 */
public class LoopThread implements Runnable {

	private Thread t;
	private final Logger log;
	private int min;
	private boolean hasstarted;
	private final String[] status = new String[] { "-help", "-getprefix", "YouTube", "Spotify", "SlashCommands",
			"Logging" };
	// private final VPlan_main vpold;

	public LoopThread() {
		log = LoggerFactory.getLogger(this.getClass());
		min = 0;
		hasstarted = false;

		t = new Thread(this, "Loop");
		// vpold = new
		// VPlan_main(Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("vppwold"));
		t.start();
	}

	@Override
	public void run() {

		while (!Klassenserver7bbot.getInstance().isInExit() && !t.isInterrupted()) {

			onrun();
			try {
				min += 10;
				Thread.sleep(600000);
			} catch (InterruptedException e) {

				if (e.getMessage().equalsIgnoreCase("sleep interrupted")) {
					log.warn("interrupted while sleeping");
				} else {
					log.error(e.getMessage(), e);
				}
			}

		}
	}

	private void onrun() {
		if (!t.isInterrupted()) {

			if (this.min % 10 == 0) {

				Klassenserver7bbot instance = Klassenserver7bbot.getInstance();

				instance.getLoopedEventManager().checkForUpdates();
				// vpold.VplanNotify("10b");

				if ((!this.hasstarted)) {
					StatsCategoryUtil.onStartup(instance.isDevMode());
					this.hasstarted = true;
				}
				Random rand = new Random();

				int i = rand.nextInt(this.status.length);

				instance.getShardManager().getShards().forEach(jda -> {
					jda.getPresence().setActivity(Activity.listening(this.status[i]));
				});

			}
		}
	}

	public void restart() {
		t.interrupt();
		t = new Thread(this, "Loop");
		t.start();
		Klassenserver7bbot.getInstance().getLoopedEventManager().restartAll();
		log.info("restarted");
	}

	public void stopLoop() {
		t.interrupt();
		log.info("interrupted");
	}

}
