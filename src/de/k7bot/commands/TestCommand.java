package de.k7bot.commands;

import java.util.List;

import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TestCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		List<Member> memb = message.getMentionedMembers();

		for (Member u : memb) {

			u.getActiveClients().forEach(c -> {

				System.out.println(c.name());

			});

		}

	}

}
