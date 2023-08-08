/**
 *
 */
package de.klassenserver7b.k7bot.manage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.InternalStatusCodes;
import de.klassenserver7b.k7bot.util.customapis.GitHubAPI;
import de.klassenserver7b.k7bot.util.customapis.GourmettaInteractions;
import de.klassenserver7b.k7bot.util.customapis.LernsaxInteractions;
import de.klassenserver7b.k7bot.util.customapis.Stundenplan24Vplan;
import de.klassenserver7b.k7bot.util.customapis.types.LoopedEvent;

/**
 * @author Klassenserver7b
 *
 */
public class LoopedEventManager {

	private final List<LoopedEvent> registeredEvents;
	private final List<LoopedEvent> activeEvents;
	private final List<LoopedEvent> erroredEvents;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 *
	 */
	public LoopedEventManager() {
		registeredEvents = new ArrayList<>();
		activeEvents = new ArrayList<>();
		erroredEvents = new ArrayList<>();
	}

	/**
	 *
	 */
	public void checkForUpdates() {

		List<LoopedEvent> change = new ArrayList<>();

		for (LoopedEvent erroredEvent : erroredEvents) {

			if (erroredEvent.isAvailable()) {
				log.info(erroredEvent.getIdentifier() + " is available again");
				change.add(erroredEvent);
			}

		}

		erroredEvents.removeAll(change);
		activeEvents.addAll(change);
		change.clear();

		for (LoopedEvent activeEvent : activeEvents) {
			int status = activeEvent.checkforUpdates();

			if (status != InternalStatusCodes.SUCCESS) {
				log.warn(activeEvent.getIdentifier() + " had an error - will be checked next time only");
				change.add(activeEvent);
			}
		}

		activeEvents.removeAll(change);
		erroredEvents.addAll(change);
		change.clear();
	}

	/**
	 *
	 * @param identifier
	 */
	protected void removeEvent(LoopedEvent event) {
		registeredEvents.remove(event);
	}

	/**
	 *
	 * @param identifier
	 */
	public void enableEvent(String identifier) {

		LoopedEvent selectedevent = null;

		for (LoopedEvent event : registeredEvents) {

			if (event.getIdentifier().equalsIgnoreCase(identifier)) {
				selectedevent = event;
				break;
			}

		}

		if (selectedevent == null) {
			return;
		}

		if (!activeEvents.contains(selectedevent)) {
			activeEvents.add(selectedevent);
		}

	}

	/**
	 *
	 * @param identifier
	 */
	public void disableEvent(String identifier) {
		LoopedEvent selectedevent = null;

		for (LoopedEvent event : registeredEvents) {

			if (event.getIdentifier().equalsIgnoreCase(identifier)) {
				selectedevent = event;
				break;
			}

		}

		if (selectedevent == null) {
			return;
		}

		activeEvents.remove(selectedevent);
	}

	/**
	 * 
	 * @param event
	 * @param enable wether the api should be enabled
	 */
	public void registerEvent(LoopedEvent event, boolean enable) {
		registeredEvents.add(event);

		if (enable) {
			activeEvents.add(event);
		}
	}

	/**
	 * @param enable wether the api should be enabled
	 * @param event
	 */
	public void registerEvents(boolean enable, LoopedEvent... events) {
		registerEvents(enable, Arrays.asList(events));
	}

	/**
	 * @param enable wether the api should be enabled
	 * @param event
	 */
	public void registerEvents(boolean enable, Collection<? extends LoopedEvent> events) {
		registeredEvents.addAll(events);

		if (enable) {
			activeEvents.addAll(events);
		}
	}

	/**
	 *
	 * @param identifier
	 */
	public void removeEvent(String identifier) {

		for (LoopedEvent event : registeredEvents) {

			if (event.getIdentifier().equalsIgnoreCase(identifier)) {
				registeredEvents.remove(event);
			}

		}
	}

	/**
	 *
	 * @param identifiers
	 */
	public void removeEvents(@Nonnull String... identifiers) {
		removeEvents(Arrays.asList(identifiers));
	}

	public void removeEvents(@Nonnull Collection<? extends String> identifiers) {

		for (LoopedEvent event : registeredEvents) {

			if (identifiers.contains(event.getIdentifier())) {
				registeredEvents.remove(event);
			}

		}
	}

	/**
	 * restarts only all ACTIVE {@link LoopedEvent LoopedEvents}
	 */
	public void restart() {

		for (LoopedEvent activeEvent : activeEvents) {
			activeEvent.restart();
		}

	}

	/**
	 * restarts every registered {@link LoopedEvent LoopedEvents}
	 */
	public void restartAll() {

		for (LoopedEvent event : registeredEvents) {
			event.restart();
		}

	}

	/**
	 * 
	 */
	public void shutdownLoopedEvents() {

		try {

			for (LoopedEvent event : registeredEvents) {

				event.shutdown();

			}

			registeredEvents.clear();

		} catch (Exception e) {
			log.warn("Forced APIs to shut down");
		}

	}

	/**
	 *
	 */
	public void initializeDefaultEvents() {

		log.info("Await API-ready");
		PropertiesManager propMgr = Klassenserver7bbot.getInstance().getPropertiesManager();

		if (propMgr.isApiEnabled("lernsax")) {
			registerEvent(new LernsaxInteractions(), true);
			log.info("LernsaxAPI registered");
		}

		if (propMgr.isApiEnabled("vplan")) {
			registerEvent(new Stundenplan24Vplan("10b"), true);
			log.info("VplanAPI registered");
		}

		if (propMgr.isApiEnabled("gourmetta")) {
			registerEvent(new GourmettaInteractions(), true);
			log.info("GourmettaAPI registered");
		}

		if (propMgr.isApiEnabled("github")) {
			registerEvent(new GitHubAPI(), true);
			log.info("GitHubAPI registered");
		}

		// registerAPI(new VVOInteractions());

		log.info("APIs are initialized");
	}

}
