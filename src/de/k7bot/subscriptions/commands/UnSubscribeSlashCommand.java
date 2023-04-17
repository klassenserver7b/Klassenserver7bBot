/**
 *
 */
package de.k7bot.subscriptions.commands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.TopLevelSlashCommand;
import de.k7bot.sql.LiteSQL;
import de.k7bot.subscriptions.types.SubscriptionDeliveryType;
import de.k7bot.subscriptions.types.SubscriptionTarget;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Klassenserver7b
 *
 */
public class UnSubscribeSlashCommand extends ListenerAdapter implements TopLevelSlashCommand {

	private final Logger log;

	public UnSubscribeSlashCommand() {
		log = LoggerFactory.getLogger(this.getClass());
		Klassenserver7bbot.getInstance().getShardManager().addEventListener(this);
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(true).complete();

		String idstr = event.getOption("subscriptionid").getAsString().split(" ")[0];

		try {

			Long id = Long.valueOf(idstr);

			if (event.getChannelType() == ChannelType.PRIVATE) {

				LinkedHashMap<Long, Long> subs = getPrivateSubs(event.getChannel().getIdLong());

				if (!subs.containsKey(id)) {
					sendInvalidIdEmbed(hook);
					return;
				}

			} else if (event.isFromGuild()) {

				LinkedHashMap<Long, String> subs = getGuildSubs(event.getGuild().getIdLong());
				if (!subs.containsKey(id)) {
					sendInvalidIdEmbed(hook);
					return;
				}

			} else {
				sendInvalidIdEmbed(hook);
				return;
			}

			Klassenserver7bbot.getInstance().getSubscriptionManager().removeSubscription(id);

			hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#00ff00"))
					.setDescription("Successfully removed subscription with id `" + id + "`").build()).queue();

		} catch (NumberFormatException e) {

			hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.red)
					.setDescription("Error while removing subscription!").build()).queue();

		}
	}

	private void sendInvalidIdEmbed(InteractionHook hook) {
		hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.red)
				.setDescription("Invalid Id for this Channel/Guild!\nPlease use Autocompletion").build()).queue();
	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("unsubscribe", "unsubscribes the selected subscription")
				.addOptions(new OptionData(OptionType.STRING, "subscriptionid",
						"The id of the subscription you want to remove").setRequired(true).setAutoComplete(true))
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
	}

	@Override
	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {

		if (event.getFullCommandName().equalsIgnoreCase("unsubscribe")) {

			ArrayList<Choice> choices = new ArrayList<>();

			if (event.getChannelType() == ChannelType.PRIVATE) {

				LinkedHashMap<Long, Long> subs = getPrivateSubs(event.getChannel().getIdLong());

				for (Entry<Long, Long> e : subs.entrySet()) {

					String cstr = e.getKey().toString() + " -> " + SubscriptionTarget.fromId(e.getValue().intValue());

					choices.add(new Choice(cstr, cstr));

				}

			} else {
				LinkedHashMap<Long, String> subs = getGuildSubs(event.getGuild().getIdLong());

				for (Entry<Long, String> e : subs.entrySet()) {

					String cstr = e.getKey().toString() + " -> " + e.getValue();

					choices.add(new Choice(cstr, e.getKey()));

				}

			}

			event.replyChoices(choices).queue();

		}

	}

	private LinkedHashMap<Long, String> getGuildSubs(Long guildid) {

		LinkedHashMap<Long, String> subs = new LinkedHashMap<>();

		ResultSet set = LiteSQL.onQuery("SELECT * FROM subscriptions;");

		try {
			while (set.next()) {

				Long targetdcid = set.getLong("targetDcId");

				GuildChannel ch = Klassenserver7bbot.getInstance().getShardManager().getGuildChannelById(targetdcid);

				if ((ch == null) || (ch.getGuild().getIdLong() != guildid)) {
					continue;
				}

				subs.put(set.getLong("subscriptionId"),
						ch.getName() + ", " + SubscriptionTarget.fromId((int) set.getLong("target")) + ", "
								+ SubscriptionDeliveryType.fromId((int) set.getLong("type")));

			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

		return subs;
	}

	private LinkedHashMap<Long, Long> getPrivateSubs(Long pvtid) {

		LinkedHashMap<Long, Long> subs = new LinkedHashMap<>();

		ResultSet set = LiteSQL.onQuery("SELECT * FROM subscriptions WHERE targetDcId = ?;", pvtid);

		try {
			while (set.next()) {

				subs.put(set.getLong("subscriptionId"), set.getLong("target"));

			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

		return subs;

	}

}
