
package de.klassenserver7b.k7bot.util.commands.slash;

import java.awt.Color;
import java.time.OffsetDateTime;

import org.jetbrains.annotations.NotNull;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.util.MessageClearUtil;
import de.klassenserver7b.k7bot.util.errorhandler.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ClearSlashCommand implements TopLevelSlashCommand {
	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

			OptionMapping amountOption = event.getOption("amount");

			InteractionHook hook = event.deferReply(true).complete();
			int amount;
			amount = (int) amountOption.getAsLong();

			if (amount > 200) {
				hook.sendMessage("Aufgrund von Zugriffslimitierungen, kann ich nicht mehr als 200 Nachrichten löschen!")
						.queue();
				return;
			}

			if (event.getChannel().getType() != ChannelType.TEXT) {
				hook.sendMessageEmbeds(new EmbedBuilder().setDescription("Can't do this in this channel!")
						.setTimestamp(OffsetDateTime.now()).setColor(Color.red).build()).queue();
				return;
			}

			MessageClearUtil.onclear(amount - 1, event.getChannel().asTextChannel());

			hook.sendMessage(amount + " messages deleted.").queue();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(16345358);
			builder.setFooter("requested by @" + event.getMember().getEffectiveName());
			builder.setTimestamp(OffsetDateTime.now());
			builder.setDescription(amount + " messages deleted!\n\n" + "**Channel: **\n" + "#"
					+ event.getChannel().asTextChannel().getName());
			TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(event.getGuild());

			if (system != null) {
				system.sendMessageEmbeds(builder.build()).queue();
			}

		} else {

			PermissionError.onPermissionError(event.getMember(), event.getChannel().asTextChannel());
		}
	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("clear", "Löscht die ausgewählte Anzahl an Nachrichten.")
				.addOptions(
						new OptionData(OptionType.INTEGER, "amount", "Wie viele Nachrichten sollen gelöscht werden?")
								.setRequired(true))
				.setGuildOnly(true)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE));
	}
}