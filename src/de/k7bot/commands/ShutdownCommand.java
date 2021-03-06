package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ShutdownCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		if (m.getIdLong() == Klassenserver7bbot.INSTANCE.getOwnerId()) {
			Klassenserver7bbot.INSTANCE.exit = true;
			Klassenserver7bbot.INSTANCE.onShutdown();
		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}

	@Override
	public String gethelp() {
		String help = "F?hrt den Bot herunter.\n - kann nur vom Bot Owner ausge?hrt werden!";
		return help;
	}

	@Override
	public String getcategory() {
String category = "Tools";
		return category;
	}
}