package de.k7bot.commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/** Interface used to specify and registrate a general Command.
 * 
 * @author Klassenserver 7b
 *
 */
public interface ServerCommand {
	/**
	 * 
	 * @return The Help for the {@link de.k7bot.commands.types.ServerCommand ServerCommand}
	 */
	String gethelp();
	/**
	 * 
	 * @return The Category for the {@link de.k7bot.commands.types.ServerCommand ServerCommand}
	 */
	String getcategory();

	void performCommand(Member m, TextChannel channel, Message message);
}
