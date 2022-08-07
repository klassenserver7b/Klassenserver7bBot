
package de.k7bot.slashcommands;

import java.time.OffsetDateTime;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.commands.ClearCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class ClearSlashCommand implements SlashCommand {
	public void performSlashCommand(SlashCommandInteraction event) {

		if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {

			OptionMapping amountOption = event.getOption("amount");

			InteractionHook hook = event.deferReply(true).complete();
			int amount;
			amount = (int) amountOption.getAsLong();

			if (amount > 200) {
				hook.sendMessage("Aufgrund von Zugriffslimitierungen, kann ich nicht mehr als 200 Nachrichten löschen!")
						.queue();
			} else {

				ClearCommand.onclear(amount - 1, event.getChannel().asTextChannel());
			}

			hook.sendMessage(amount + " messages deleted.").queue();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(16345358);
			builder.setFooter("requested by @" + event.getMember().getEffectiveName());
			builder.setTimestamp(OffsetDateTime.now());
			builder.setDescription(
					amount + " messages deleted!\n\n" + "**Channel: **\n" + "#" + event.getChannel().asTextChannel().getName());
			TextChannel system = Klassenserver7bbot.INSTANCE.getsyschannell().getSysChannel(event.getGuild());

			if (system != null) {
				system.sendMessageEmbeds(builder.build()).queue();
			}

		} else {

			PermissionError.onPermissionError(event.getMember(), event.getChannel().asTextChannel());
		}
	}
}