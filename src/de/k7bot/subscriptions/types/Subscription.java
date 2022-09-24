package de.k7bot.subscriptions.types;

/**
 * 
 * @author Felix
 *
 */
public class Subscription {

	private final SubscriptionTarget target;
	private final SubscriptionDeliveryType type;
	private final Long targetdcid;

	/**
	 * 
	 * @param type       The {@link SubscriptionDeliveryType} of the new
	 *                   {@link Subscription}.
	 * 
	 * @param target     The {@link SubscriptionTarget} of the new
	 *                   {@link Subscription}.
	 * 
	 * @param targetDcId The {@link Long Id} of the target-channel given by discord.
	 */
	public Subscription(SubscriptionDeliveryType type, SubscriptionTarget target, Long targetdiscordid) {
		this.target = target;
		this.type = type;
		this.targetdcid = targetdiscordid;
	}

	/**
	 * 
	 */
	public void deliverSubscriptionMessage() {

	};

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
	public SubscriptionDeliveryType getType() {
		return this.type;
	}

	/**
	 * 
	 * @return The Id of the target-channel
	 */
	public Long getTargetDiscordId() {
		return this.targetdcid;
	}

}
