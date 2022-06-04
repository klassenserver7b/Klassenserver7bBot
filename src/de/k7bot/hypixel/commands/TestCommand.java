package de.k7bot.hypixel.commands;

import de.k7bot.commands.types.HypixelCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TestCommand implements HypixelCommand {
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
	}
}
