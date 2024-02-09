/**
 * 
 */
package de.klassenserver7b.k7bot.commands.types;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import javax.annotation.Nonnull;

/**
 * @author K7
 *
 */
public interface SubSlashCommand extends SlashCommand {
	@Nonnull
    SubcommandData getSubCommandData();

	String getSubPath();

}
