package de.k7bot.util.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EveryoneCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		String[] args = message.getContentDisplay().split(" ");
		StringBuilder builder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			builder.append(" " + args[i]);
		}

		channel.sendMessage(channel.getGuild().getPublicRole().getAsMention() + " " + builder.toString().trim())
				.queue();
	}

	@Override
	public String gethelp() {
		return "Sendet die aktuelle Nachricht als @everyone.\n - z.B. [prefix]everyone [Nachricht]";
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.TOOLS;
	}
}