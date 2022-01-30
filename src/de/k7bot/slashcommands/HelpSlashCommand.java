package de.k7bot.slashcommands;

import de.k7bot.commands.helpCommand;
import de.k7bot.commands.types.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class HelpSlashCommand implements SlashCommand {
	public void performSlashCommand(SlashCommandEvent event) {
		helpCommand help = new helpCommand();

		event.reply("** look into your DM's **" + event.getMember().getAsMention()
				+ "\n (Only available if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!)")
				.complete();
		help.onHelpEmbed(event.getMember(), event.getGuild());
	}
}