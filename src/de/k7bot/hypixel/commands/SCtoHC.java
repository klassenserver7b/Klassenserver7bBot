package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SCtoHC implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		String mess = message.getContentStripped();

		if (mess.length() >= 8) {

			String[] args = mess.substring(8).trim().split(" ");

			if (args.length > 0) {

				if (!Klassenserver7bbot.getInstance().gethypMan().performHypixel(args[0], m, channel, message))
					((Message) channel.sendMessage("`unbekannter Hypixel - Command` - Hilfe: '-Hypixel help'")
							.complete()).delete().queueAfter(10L, TimeUnit.SECONDS);
			}
		}
	}

	@Override
	public String gethelp() {
		String help = "Siehe [prefix]hypixel help";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Games";
		return category;
	}
}