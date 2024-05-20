package de.klassenserver7b.k7bot.subscriptions;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.subscriptions.types.Subscription;
import de.klassenserver7b.k7bot.subscriptions.types.SubscriptionDeliveryType;
import de.klassenserver7b.k7bot.subscriptions.types.SubscriptionTarget;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Klassenserver7b
 */
public class SubscriptionManager {

    private final ArrayList<Subscription> sublist;
    final SubMessageSendHandler sendhandler;
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
     * @param data   The message which should be sent to all subscribers
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
     * @param target           The {@link SubscriptionTarget} of the new
     *                         Subscription
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

        } catch (Exception e) {

            log.error(e.getMessage(), e);
            return false;

        }
    }

    public boolean removeSubscription(Long subscriptionid) {

        int affectedLines = LiteSQL.onUpdate("DELETE FROM subscriptions WHERE subscriptionId = ?", subscriptionid);
        refreshList();
        return affectedLines > 0;
    }

    /**
     * Removes a Subscription from the database
     *
     * @param type             The {@link SubscriptionDeliveryType} of the Channel
     *                         the Bot should deliver the Subscription in
     * @param target           The {@link SubscriptionTarget} of the new
     *                         Subscription
     * @param deliverytargetid The DiscordID of the DeliveryTarget, e.g.
     *                         <code>TYPE.getIdLong()</code>.
     */
    @SuppressWarnings("unused")
    public boolean removeSubscription(SubscriptionDeliveryType type, SubscriptionTarget target, Long deliverytargetid) {
        Long subid = calculateSubId(type, target, deliverytargetid);
        return removeSubscription(subid);
    }

    /**
     * Requests the database-saved subscriptions and loads them into the local
     * SubscriptionList
     */
    private void refreshList() {

        try (ResultSet set = LiteSQL.onQuery("SELECT * FROM subscriptions;")) {

            if (set != null) {
                while (set.next()) {

                    int typeid = set.getInt("type");
                    int targetid = set.getInt("target");
                    long deliveryid = set.getLong("targetDcId");
                    long subid = set.getLong("subscriptionId");

                    if (typeid != 0 && targetid != 0 && deliveryid != 0 && subid != 0) {

                        Subscription s = new Subscription(SubscriptionDeliveryType.fromId(typeid),
                                SubscriptionTarget.fromId(targetid), deliveryid, subid);

                        sublist.add(s);
                    } else {
                        log.warn("Invalid Subscription in Database! - Id: {}", subid);
                    }

                }
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * @param sub The Subscription to calculate the ID for
     * @return The calculated SubscriptionID
     */
    private Long calculateSubId(Subscription sub) {
        return calculateSubId(sub.getDeliveryType(), sub.getTarget(), sub.getTargetDiscordId());
    }

    /**
     * @param type             The SubscriptionDeliveryType of the Subscription
     * @param target           The SubscriptionTarget of the Subscription
     * @param deliverytargetid The DiscordID of the DeliveryTarget, e.g.
     * @return The calculated SubscriptionID
     */
    private Long calculateSubId(SubscriptionDeliveryType type, SubscriptionTarget target, Long deliverytargetid) {
        return (type.getId() * 31L + target.getId()) * 41 + deliverytargetid;
    }

}
