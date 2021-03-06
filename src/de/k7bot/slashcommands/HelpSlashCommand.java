package de.k7bot.slashcommands;

import java.util.concurrent.TimeUnit;

import de.k7bot.commands.helpCommand;
import de.k7bot.commands.types.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class HelpSlashCommand implements SlashCommand {
	public void performSlashCommand(SlashCommandInteraction event) {
		
		helpCommand help = new helpCommand();

		event.reply("** look into your DM's **" + event.getMember().getAsMention()
				+ "\n (Only available if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!)")
				.complete().deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
		help.onHelpEmbed(event.getMember(), event.getTextChannel());
	}
}