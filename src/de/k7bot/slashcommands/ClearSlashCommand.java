
package de.k7bot.slashcommands;

import java.time.OffsetDateTime;

import de.k7bot.commands.ClearCommand;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.manage.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class ClearSlashCommand implements SlashCommand {
	public void performSlashCommand(SlashCommandInteraction event) {

		if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

			OptionMapping amountOption = event.getOption("amount");

			InteractionHook hook = event.deferReply(true).complete();
			if (amountOption != null) {
				int amount = (int) amountOption.getAsLong();
				if (amount == 0) {
					amount = 1;
				}

				else if (amount > 200) {
					hook.sendMessage(
							"Aufgrund von Zugriffslimitierungen, kann ich nicht mehr als 200 Nachrichten löschen!")
							.queue();
				} else {

					ClearCommand.onclear(amount - 1, event.getTextChannel(), event.getMember());
				}

				hook.sendMessage(String.valueOf(amount) + " messages deleted.").queue();

				EmbedBuilder builder = new EmbedBuilder();
				builder.setColor(16345358);
				builder.setFooter("requested by @" + event.getMember().getEffectiveName());
				builder.setTimestamp(OffsetDateTime.now());
				builder.setDescription(String.valueOf(amount) + " messages deleted!\n\n" + "**Channel: **\n" + "#"
						+ event.getTextChannel().getName());
				event.getTextChannel().getGuild().getSystemChannel().sendMessageEmbeds(builder.build()).queue();
			} else {

				hook.sendMessage("please submit an amount of messages to purge").queue();
			}
		} else {

			PermissionError.onPermissionError(event.getMember(), event.getTextChannel());
		}
	}
}