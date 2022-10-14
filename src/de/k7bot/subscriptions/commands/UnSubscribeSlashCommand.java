/**
 * 
 */
package de.k7bot.subscriptions.commands;

import org.jetbrains.annotations.NotNull;

import de.k7bot.commands.types.SlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Felix
 *
 */
public class UnSubscribeSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("unsubscribe", "unsubscribes the selected subscription");
	}

}
