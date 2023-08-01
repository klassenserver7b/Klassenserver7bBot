/**
 * 
 */
package de.k7bot.commands.types;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

/**
 * @author K7
 *
 */
public interface SlashCommand {
	void performSlashCommand(SlashCommandInteraction event);
}
