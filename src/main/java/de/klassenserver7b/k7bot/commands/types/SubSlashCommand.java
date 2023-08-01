/**
 * 
 */
package de.klassenserver7b.k7bot.commands.types;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author K7
 *
 */
public interface SubSlashCommand extends SlashCommand {
	@Nonnull
	public SubcommandData getSubCommandData();

	public String getSubPath();

}
