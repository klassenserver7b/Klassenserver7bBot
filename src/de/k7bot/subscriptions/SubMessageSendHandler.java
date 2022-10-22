package de.k7bot.subscriptions;

import org.slf4j.Logger;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.subscriptions.types.Subscription;
import de.k7bot.subscriptions.types.SubscriptionDeliveryType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class SubMessageSendHandler {

	Logger log;

	public SubMessageSendHandler(Logger logger) {
		this.log = logger;
	}

	public void provideSubscriptionMessage(Subscription s, MessageCreateData d) {

		SubscriptionDeliveryType type = s.getDeliveryType();
		if (Klassenserver7bbot.getInstance().isDevMode()) {
			type = SubscriptionDeliveryType.CANARY;
		}

		switch (type) {

		case TEXT_CHANNEL: {
			sendTextChannelMessage(s, d);
			break;
		}
		case PRIVATE_CHANNEL: {
			sendPrivateMessage(s, d);
			break;
		}
		case NEWS: {
			sendNewsMessage(s, d);
			break;
		}
		case CANARY: {
			sendCanaryMessage(s, d);
			break;
		}

		case UNKNOWN: {
			throw new IllegalArgumentException(
					"Unexpected value: UNKNOWN - id: " + SubscriptionDeliveryType.UNKNOWN.getId());
		}

		}

	}

	private void sendTextChannelMessage(Subscription s, MessageCreateData data) {
		Long chanid = s.getTargetDiscordId();

		TextChannel ch = Klassenserver7bbot.getInstance().getShardManager().getTextChannelById(chanid);

		if (ch != null) {

			ch.sendMessage(data).queue();

		} else {
			log.error("Could not find the Discord target for the Subscription\nSubscriptionId: " + s.getId()
					+ "\nDiscord(TextChannel)Id: " + s.getTargetDiscordId());
		}

	}

	private void sendPrivateMessage(Subscription s, MessageCreateData data) {

		Long chanid = s.getTargetDiscordId();

		PrivateChannel ch = Klassenserver7bbot.getInstance().getShardManager().getPrivateChannelById(chanid);

		if (ch != null) {

			ch.sendMessage(data).queue();

		} else {
			log.error("Could not find the Discord target for the Subscription\nSubscriptionId: " + s.getId()
					+ "\nDiscord(PrivateChannel)Id: " + s.getTargetDiscordId());
		}

	}

	private void sendNewsMessage(Subscription s, MessageCreateData data) {

		Long chanid = s.getTargetDiscordId();

		NewsChannel ch = Klassenserver7bbot.getInstance().getShardManager().getNewsChannelById(chanid);

		if (ch != null) {

			ch.sendMessage(data).queue();

		} else {
			log.error("Could not find the Discord target for the Subscription\nSubscriptionId: " + s.getId()
					+ "\nDiscord(NewsChannel)Id: " + s.getTargetDiscordId());
		}

	}

	private void sendCanaryMessage(Subscription s, MessageCreateData data) {

		if (!Klassenserver7bbot.getInstance().isDevMode()) {
			return;
		}

		if (s.getDeliveryType() != SubscriptionDeliveryType.CANARY) {
			return;
		}

		Long chanid = s.getTargetDiscordId();

		TextChannel ch = Klassenserver7bbot.getInstance().getShardManager().getTextChannelById(chanid);

		if (ch != null) {

			ch.sendMessage(data).queue();

		} else {
			log.error("Could not find the Discord target for the Subscription\nSubscriptionId: " + s.getId()
					+ "\nDiscord(Canary TextChannel)Id: " + s.getTargetDiscordId());
		}

	}
}
