/**
 * 
 */
package de.k7bot.commands.types;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author Felix
 *
 */
public interface SubSlashCommand extends SlashCommand {
	@Nonnull
	public SubcommandData getSubCommandData();

	public String getSubPath();

}
