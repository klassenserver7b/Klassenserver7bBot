package de.k7bot.commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface ServerCommand {
	String gethelp();
	String getcategory();

	void performCommand(Member m, TextChannel channel, Message message);
}
