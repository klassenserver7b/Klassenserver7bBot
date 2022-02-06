
package de.k7bot.slashcommands;

import java.util.concurrent.TimeUnit;

import de.k7bot.commands.ClearCommand;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.manage.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class ClearSlashCommand implements SlashCommand {
	public void performSlashCommand(SlashCommandInteraction event) {
		if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

			OptionMapping amountOption = event.getOption("amount");

			event.deferReply(true);
			if (amountOption != null) {
				int amount = (int) amountOption.getAsLong();
				if (amount == 0) {
					amount = 1;
				}

				else if (amount > 200) {
					event.reply("Aufgrund von Zugriffslimitierungen, kann ich nicht mehr als 200 Nachrichten löschen!")
							.queue();
				} else {

					ClearCommand.onclear(amount-1, event.getTextChannel(), event.getMember());
				}

				event.reply(String.valueOf(amount) + " messages deleted.").complete().deleteOriginal().queueAfter(15,
						TimeUnit.SECONDS);
			} else {

				event.reply("please submit an amount of messages to purge").queue();
			}
		} else {

			PermissionError.onPermissionError(event.getMember(), event.getTextChannel());
		}
	}
}