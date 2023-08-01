package de.klassenserver7b.k7bot.hypixel.commands;

import de.klassenserver7b.k7bot.commands.types.HypixelCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TestCommand implements HypixelCommand {
	@Override
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
	}
}
