package de.k7bot.subscriptions;

import de.k7bot.subscriptions.types.SubscriptionTarget;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.sql.LiteSQL;
import de.k7bot.subscriptions.types.Subscription;
import de.k7bot.subscriptions.types.SubscriptionDeliveryType;

/**
 * 
 * @author Felix
 *
 */
public class SubscriptionManager {

	private final ArrayList<Subscription> sublist;
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 
	 */
	public SubscriptionManager() {
		sublist = new ArrayList<>();
		initialize();
	}

	/**
	 * Creates a new Subscription for the submitted target and saves it to the
	 * database.
	 * 
	 * @param type             The {@link SubscriptionDeliveryType} of the Channel
	 *                         the Bot should deliver the Subscription in
	 * 
	 * @param target           The {@link SubscriptionTarget} of the new
	 *                         Subscription
	 * 
	 * @param deliverytargetid The DiscordID of the DeliveryTarget, e.g.
	 *                         <code>TYPE.getIdLong()<code>.
	 */
	public boolean createSubscription(SubscriptionDeliveryType type, SubscriptionTarget target, Long deliverytargetid) {

		if (type.getId() != -1 && target.getId() != -1) {

			sublist.add(new Subscription(type, target, deliverytargetid));
			LiteSQL.onUpdate("INSERT INTO subscriptions(type,target,targetDcId) VALUES(" + type.getId() + ", "
					+ target.getId() + ", " + deliverytargetid + ");");
			return true;

		} else {
			log.info("Couldn't create Subscription - One of the Enums was UNKNOWN");
			return false;
		}

	}

	/**
	 * Requests the database-saved subscriptions and loads them into the local
	 * SubscriptionList
	 */
	void initialize() {

	}

}
