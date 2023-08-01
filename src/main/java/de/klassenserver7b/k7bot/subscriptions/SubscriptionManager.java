package de.klassenserver7b.k7bot.subscriptions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.subscriptions.types.Subscription;
import de.klassenserver7b.k7bot.subscriptions.types.SubscriptionDeliveryType;
import de.klassenserver7b.k7bot.subscriptions.types.SubscriptionTarget;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 *
 * @author Klassenserver7b
 *
 */
public class SubscriptionManager {

	private final ArrayList<Subscription> sublist;
	SubMessageSendHandler sendhandler;
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 *
	 */
	public SubscriptionManager() {
		sendhandler = new SubMessageSendHandler(log);
		sublist = new ArrayList<>();
		refreshList();
	}

	/**
	 * Provides the {@link Message} to every (target)-matching subscription
	 *
	 * @param target The target of the notification e.g a new Lernplan
	 * @param data   The message which schould be send to all subscribers
	 */
	public void provideSubscriptionNotification(SubscriptionTarget target, MessageCreateData data) {

		if (data == null) {
			return;
		}

		sublist.forEach(s -> {

			if (s.getTarget() == target) {
				sendhandler.provideSubscriptionMessage(s, data);
			}

		});
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
	 *                         <code>TYPE.getIdLong()</code>.
	 */

	public boolean createSubscription(SubscriptionDeliveryType type, SubscriptionTarget target, Long deliverytargetid) {

		if (type.getId() != -1 && target.getId() != -1) {

			Long subscriptionid = calculateSubId(type, target, deliverytargetid);

			return registerSubscription(new Subscription(type, target, deliverytargetid, subscriptionid));

		}

		log.warn("Couldn't create Subscription - One of the Enums was UNKNOWN");
		return false;

	}

	private boolean registerSubscription(Subscription sub) {

		try {
			Long subid = calculateSubId(sub);

			sublist.add(sub);

			LiteSQL.onUpdate("INSERT INTO subscriptions(type, target, targetDcId, subscriptionId) VALUES(?, ?, ?, ?);",
					sub.getDeliveryType().getId(), sub.getTarget().getId(), sub.getTargetDiscordId(), subid);

			return true;

		}
		catch (Exception e) {

			log.error(e.getMessage(), e);
			return false;

		}
	}

	public boolean removeSubscription(Long subscriptionid) {

		LiteSQL.onUpdate("DELETE FROM subscriptions WHERE subscriptionId = ?", subscriptionid);
		refreshList();

		return false;
	}

	public void removeSubscription(SubscriptionDeliveryType type, SubscriptionTarget target, Long deliverytargetid) {
		Long subid = calculateSubId(type, target, deliverytargetid);
		LiteSQL.onUpdate("DELETE FROM subscriptions WHERE subscriptionId = ?", subid);
		refreshList();
	}

	/**
	 * Requests the database-saved subscriptions and loads them into the local
	 * SubscriptionList
	 */
	private void refreshList() {

		try (ResultSet set = LiteSQL.onQuery("SELECT * FROM subscriptions;")) {

			if (set != null) {
				while (set.next()) {

					Integer typeid = set.getInt("type");
					Integer targetid = set.getInt("target");
					Long deliveryid = set.getLong("targetDcId");
					Long subid = set.getLong("subscriptionId");

					if (typeid != 0 && targetid != 0 && deliveryid != 0 && subid != 0) {

						Subscription s = new Subscription(SubscriptionDeliveryType.fromId(typeid),
								SubscriptionTarget.fromId(targetid), deliveryid, subid);

						sublist.add(s);
					} else {
						log.warn("Invalid Subscription in Database! - Id: " + subid);
					}

				}
			}
		}
		catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 *
	 * @param sub
	 * @return
	 */
	private Long calculateSubId(Subscription sub) {
		return (sub.getTarget().getId() * 31 + sub.getDeliveryType().getId()) * 41 + sub.getTargetDiscordId();
	}

	/**
	 *
	 * @param type
	 * @param target
	 * @param deliverytargetid
	 * @return
	 */
	private Long calculateSubId(SubscriptionDeliveryType type, SubscriptionTarget target, Long deliverytargetid) {
		return (type.getId() * 32 + target.getId()) * 32 + deliverytargetid;
	}

}
