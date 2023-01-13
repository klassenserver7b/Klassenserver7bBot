package de.k7bot.subscriptions.types;

/**
 *
 * @author Klassenserver7b
 *
 */
public class Subscription {

	private final SubscriptionTarget target;
	private final SubscriptionDeliveryType type;
	private final Long targetdcid;
	private final Long id;

	/**
	 *
	 * @param type       The {@link SubscriptionDeliveryType} of the new
	 *                   {@link Subscription}.
	 *
	 * @param target     The {@link SubscriptionTarget} of the new
	 *                   {@link Subscription}.
	 *
	 * @param targetDcId The {@link Long Id} of the target-channel given by discord.
	 *
	 * @param id         The id of the Subscription if already known
	 */
	public Subscription(SubscriptionDeliveryType type, SubscriptionTarget target, Long targetdiscordid, Long id) {
		this.target = target;
		this.type = type;
		this.targetdcid = targetdiscordid;
		this.id = id;
	}

	/**
	 *
	 * @return The {@link SubscriptionTarget} of the {@link Subscription}
	 */
	public SubscriptionTarget getTarget() {
		return this.target;
	}

	/**
	 *
	 * @return The {@link SubscriptionDeliveryType} of the {@link Subscription}
	 */
	public SubscriptionDeliveryType getDeliveryType() {
		return this.type;
	}

	/**
	 *
	 * @return The Id of the target-channel
	 */
	public Long getTargetDiscordId() {
		return this.targetdcid;
	}

	/**
	 *
	 * @return The uniwue Id of this subscription
	 */
	public Long getId() {
		return this.id;
	}
}
