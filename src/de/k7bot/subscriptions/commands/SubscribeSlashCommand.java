package de.k7bot.subscriptions.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.subscriptions.types.SubscriptionDeliveryType;
import de.k7bot.subscriptions.types.SubscriptionTarget;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class SubscribeSlashCommand implements SlashCommand {

	Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(true).complete();

		String path = event.getCommandPath();

		SubscriptionTarget target = switch (event.getOption("target").getAsString()) {

		case "BOT_NEWS":
			yield SubscriptionTarget.BOT_NEWS;

		case "LERNPLAN":
			yield SubscriptionTarget.LERNPLAN;

		case "VPLAN":
			yield SubscriptionTarget.VPLAN;

		case "GOURMETTA":
			yield SubscriptionTarget.GOURMETTA;

		case "KAUFLAND":
			yield SubscriptionTarget.KAUFLAND;

		default:
			throw new IllegalArgumentException("Unexpected value: " + event.getOption("target").getAsString());
		};

		if (target.isprivileged() && event.getUser().getIdLong() != Klassenserver7bbot.INSTANCE.getOwnerId()) {
			hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#ff0000"))
					.setTimestamp(OffsetDateTime.now())
					.setDescription(
							"You must be the Botowner to access this target! - please ask him to create the subscription\nIf you are the Botowner check if you have inserted your discord userid in the configfile!")
					.build()).queue();
			return;
		}

		if (path.equalsIgnoreCase("subscribe/privatechannel")) {

			try {

				Klassenserver7bbot.INSTANCE.getSubscriptionManager().createSubscription(
						SubscriptionDeliveryType.PRIVATE_CHANNEL, target,
						event.getUser().openPrivateChannel().complete().getIdLong());

				hook.sendMessageEmbeds(
						new EmbedBuilder().setColor(Color.decode("#00ff00")).setTimestamp(OffsetDateTime.now())
								.setDescription("The subscription was created successfull!").build())
						.queue();

			} catch (Exception e) {

				log.error(e.getMessage(), e);
				hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#ff0000"))
						.setTimestamp(OffsetDateTime.now())
						.setDescription(
								"Could not open a private channel! please check if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!")
						.build()).queue();

			}

		} else {
			GuildChannelUnion union = event.getOption("channel").getAsChannel();
			SubscriptionDeliveryType delivery;

			switch (union.getType()) {

			case TEXT: {
				delivery = SubscriptionDeliveryType.TEXT_CHANNEL;
				break;
			}
			case NEWS: {
				delivery = SubscriptionDeliveryType.NEWS;
			}
			default:
				delivery = SubscriptionDeliveryType.UNKNOWN;
				hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#ff0000"))
						.setTimestamp(OffsetDateTime.now()).setDescription("Can't create subscription in "
								+ union.getType() + "!\nPlease use a Text or News Channel")
						.build()).queue();
			}

			if (delivery != SubscriptionDeliveryType.UNKNOWN) {

				Klassenserver7bbot.INSTANCE.getSubscriptionManager().createSubscription(delivery, target,
						union.getIdLong());
				hook.sendMessageEmbeds(
						new EmbedBuilder().setColor(Color.decode("#00ff00")).setTimestamp(OffsetDateTime.now())
								.setDescription("The subscription was created successfull!").build())
						.queue();
			}

		}

	}

	@NotNull
	@Override
	public SlashCommandData getCommandData() {

		List<Choice> choices = new ArrayList<>();
		choices.add(new Choice("Bot News", "BOT_NEWS"));
		choices.add(new Choice("Lernplan", "LERNPLAN"));
		choices.add(new Choice("Vertretungsplan", "VPLAN"));
		choices.add(new Choice("Gourmetta", "GOURMETTA"));
		choices.add(new Choice("Kaufland", "KAUFLAND"));

		SubcommandData textchannelsub = new SubcommandData("textchannel",
				"Use this if you want to recieve your messages in a Text-Channel")
				.addOption(OptionType.CHANNEL, "channel", "The channel where the message should be send to")
				.addOptions(new OptionData(OptionType.STRING, "target",
						"The target the subscription should check for updates").addChoices(choices));
		;

		SubcommandData privatechannelsub = new SubcommandData("privatechannel",
				"Use this if you want to recieve your messages in a private-Channel")
				.addOptions(new OptionData(OptionType.STRING, "target",
						"The target the subscription should check for updates").addChoices(choices));

		return Commands.slash("subscribe", "Addes a subscription for the given type in the given channel.")
				.addSubcommands(textchannelsub, privatechannelsub)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
	}

}
