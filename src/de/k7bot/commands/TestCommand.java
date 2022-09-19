package de.k7bot.commands;

import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TestCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String getcategory() {
		return null;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
	}
}
