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
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.StatsCategorieUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.sharding.ShardManager;

/**
 * @author Klassenserver7b
 *
 */
public class ConsoleReadThread implements Runnable {

	private final Thread t;
	private final Logger log;
	private BufferedReader reader;
	private InputStreamReader sysinr;

	public ConsoleReadThread() {
		log = LoggerFactory.getLogger(this.getClass());

		sysinr = new InputStreamReader(System.in);
		reader = new BufferedReader(sysinr);
		t = new Thread(this, "ConsoleReadThread");
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
					interpretConsoleContent(line);
				}

				Thread.sleep(5000);

			} catch (InterruptedException | IOException e) {

				if (e.getMessage().equalsIgnoreCase("Stream closed")) {
					t.interrupt();
					break;
				}
				log.error(e.getMessage(), e);
			}
		}

	}

	public void interpretConsoleContent(String s) throws IOException {

		String[] commandargs = s.split(" ");

		switch (commandargs[0]) {
		case "exit" -> {

			Klassenserver7bbot.getInstance().setEventBlocking(true);
			Klassenserver7bbot.getInstance().setexit(true);
			t.interrupt();
			reader.close();
			this.onShutdown();
			return;
		}

		case "enableCommand" -> {
			changeCommandState(true, commandargs[1]);
		}

		case "disableCommand" -> {
			changeCommandState(false, commandargs[1]);
		}

		default -> {
			System.out.println("Use Exit to Shutdown");
		}

		}

	}

	public void changeCommandState(boolean enable, String command_OR_CommandClassName) {

		Class<?> c = getClassFromString(command_OR_CommandClassName);

		if (c != null) {

			try {

				Object obj = c.getDeclaredConstructor().newInstance();

				if (!(obj instanceof ServerCommand)) {
					log.warn("Invalid CommandClassName");
					return;
				}

				if (enable) {
					enableCommandbyObj((ServerCommand) obj, command_OR_CommandClassName);
				} else {
					disableCommandbyObj((ServerCommand) obj, command_OR_CommandClassName);
				}

			} catch (IllegalArgumentException | SecurityException | ReflectiveOperationException e) {
				log.error(e.getMessage(), e);
			}

		} else {

			if (enable) {
				enableCommandbyStr(command_OR_CommandClassName);
			} else {
				disableCommandbyStr(command_OR_CommandClassName);
			}
		}
	}

	private void disableCommandbyStr(String name) {
		if (Klassenserver7bbot.getInstance().getCmdMan().disableCommand(name)) {
			log.info("successfully disabled " + name);
			return;
		} else {
			log.warn("failed to disable " + name);
			return;
		}
	}

	private void enableCommandbyStr(String name) {
		if (Klassenserver7bbot.getInstance().getCmdMan().enableCommand(name)) {
			log.info("successfully enabled " + name);
			return;
		} else {
			log.warn("failed to enable " + name);
			return;
		}
	}

	private void disableCommandbyObj(ServerCommand obj, String name) {
		if (Klassenserver7bbot.getInstance().getCmdMan().disableCommand(obj)) {
			log.info("successfully disabled all types of " + name);
			return;
		} else {
			log.warn("failed to disable all types of " + name);
			return;
		}
	}

	private void enableCommandbyObj(ServerCommand obj, String name) {
		if (Klassenserver7bbot.getInstance().getCmdMan().enableCommand(obj)) {
			log.info("successfully enabled all types of " + name);
			return;
		} else {
			log.warn("failed to enable all types of " + name);
			return;
		}
	}

	public Class<?> getClassFromString(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException e) {
			return null;
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
