package de.klassenserver7b.k7bot.subscriptions;

import org.slf4j.Logger;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.subscriptions.types.Subscription;
import de.klassenserver7b.k7bot.subscriptions.types.SubscriptionDeliveryType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
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

		case TEXT_CHANNEL, NEWS: {
			sendGuildMessageChannelMessage(s, d);
			break;
		}
		case PRIVATE_CHANNEL: {
			sendPrivateMessage(s, d);
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

	private void sendGuildMessageChannelMessage(Subscription s, MessageCreateData data) {
		Long chanid = s.getTargetDiscordId();

		GuildChannel gchan = Klassenserver7bbot.getInstance().getShardManager().getGuildChannelById(chanid);
		GuildMessageChannel channel;

		if ((gchan) != null && (gchan instanceof GuildMessageChannel)
				&& (channel = (GuildMessageChannel) gchan) != null) {

			channel.sendMessage(data).queue();

		} else {
			log.error("Could not find the Discord target for the Subscription\nSubscriptionId: " + s.getId()
					+ "\nDiscord(GuildMessageChannel)Id: " + s.getTargetDiscordId());
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

	private void sendCanaryMessage(Subscription s, MessageCreateData data) {

		if (!Klassenserver7bbot.getInstance().isDevMode() || (s.getDeliveryType() != SubscriptionDeliveryType.CANARY)) {
			return;
		}

		Long chanid = s.getTargetDiscordId();

		GuildChannel gchan = Klassenserver7bbot.getInstance().getShardManager().getGuildChannelById(chanid);
		GuildMessageChannel channel;

		if ((gchan) != null && (gchan instanceof GuildMessageChannel)
				&& (channel = (GuildMessageChannel) gchan) != null) {

			channel.sendMessage(data).queue();

		} else {
			log.error("Could not find the Discord target for the Subscription\nSubscriptionId: " + s.getId()
					+ "\nDiscord(Canary GuildMessageChannel)Id: " + s.getTargetDiscordId());
		}

	}
}
