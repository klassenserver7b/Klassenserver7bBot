package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.util.errorhandler.PermissionError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ShutdownCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "Fährt den Bot herunter.\n - kann nur vom Bot Owner ausgeführt werden!";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "shutdown" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.TOOLS;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.getIdLong() == Klassenserver7bbot.getInstance().getOwnerId()) {

			Klassenserver7bbot.getInstance().setexit(true);
			Klassenserver7bbot.getInstance().getShutdownThread().onShutdown();
			return;

		}

		PermissionError.onPermissionError(m, channel);

	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}
}