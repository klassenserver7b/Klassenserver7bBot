package de.k7bot.subscriptions.types;

import javax.annotation.Nonnull;

import de.k7bot.timed.VplanNEW_XML;
import de.k7bot.util.LernsaxInteractions;

/**
 * 
 * @author Felix
 *
 */
public enum SubscriptionTarget {

	/**
	 * The Type for Systemnotification subscriptions.
	 */
	SYSNOTIFICATIONS(0, false),

	/**
	 * The Type for {@link VplanNEW_XML} Subscription, only when Vplan API enabled.
	 */
	VPLAN(1, true),

	/**
	 * The Type for BotNews such as Updates and Fixes, only fully functional when
	 * GitHub API enabled.
	 */
	BOT_NEWS(2, false),

	/**
	 * The Type for {@link LernsaxInteractions} especially 'Lernpläne', only when
	 * Lernsax API enabled
	 */
	LERNPLAN(3, true),

	/**
	 * Unknown {@link SubscriptionTarget} type. Should never happen and would only
	 * possibly happen if the K7Bot implemented a new API type and had yet to
	 * implement channnel-support for it.
	 */
	UNKNOWN(-1, false);

	private final boolean needsAPI;
	private final int id;

	private SubscriptionTarget(int id, boolean needsSpecialAPI) {

		this.id = id;
		this.needsAPI = needsSpecialAPI;

	}

	/**
	 * The K7Bot id key used to represent the {@link SubscriptionTarget}.
	 *
	 * @return The id key used by K7Bot for this target type.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Whether this {@link SubscriptionTarget} needs an enabled API to run
	 *
	 * @return Whether or not this needs an enabled API to run.
	 */
	public boolean needsApi() {
		return needsAPI;
	}

	/**
	 * Static accessor for retrieving a {@link SubscriptionTarget} based on its
	 * K7Bot id key.
	 *
	 * @param id The id key of the requested target type.
	 *
	 * @return The {@link SubscriptionTarget} that is referred to by the provided
	 *         key. If the id key is unknown, {@link #UNKNOWN} is returned.
	 */
	@Nonnull
	public static SubscriptionTarget fromId(int id) {
		for (SubscriptionTarget type : values()) {
			if (type.id == id)
				return type;
		}
		return UNKNOWN;
	}

}
