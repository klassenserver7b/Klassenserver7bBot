
package de.k7bot.slashcommands;

import java.time.OffsetDateTime;

import de.k7bot.commands.ClearCommand;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.util.PermissionError;
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
			int amount = 1;
			amount = (int) amountOption.getAsLong();

			if (amount > 200) {
				hook.sendMessage("Aufgrund von Zugriffslimitierungen, kann ich nicht mehr als 200 Nachrichten löschen!")
						.queue();
			} else {

				ClearCommand.onclear(amount - 1, event.getTextChannel(), event.getMember());
			}

			hook.sendMessage(amount + " messages deleted.").queue();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(16345358);
			builder.setFooter("requested by @" + event.getMember().getEffectiveName());
			builder.setTimestamp(OffsetDateTime.now());
			builder.setDescription(amount + " messages deleted!\n\n" + "**Channel: **\n" + "#"
					+ event.getTextChannel().getName());
			event.getTextChannel().getGuild().getSystemChannel().sendMessageEmbeds(builder.build()).queue();

		} else {

			PermissionError.onPermissionError(event.getMember(), event.getTextChannel());
		}
	}
}