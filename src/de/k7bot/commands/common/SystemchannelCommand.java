package de.k7bot.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.manage.SystemNotificationChannelManager;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SystemchannelCommand implements ServerCommand { 

 	private boolean isEnabled;

	@Override
	public String gethelp() {
		return "Ändert den Channel für Systembenachrichtigungen (z.B. Logs für Einladungen oder gelöschte Nachrichten) des Bots auf diesem Server.\n - kann nur von Personen mit der Berechtigung 'Server Verwalten' ausgeführt werden!\n - z.B. [prefix]syschannel [@new syschannel]";
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "syschannel" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.ALLGEMEIN;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.MANAGE_SERVER)) {

			if (!message.getMentions().getChannels(TextChannel.class).isEmpty()) {

				TextChannel chan = message.getMentions().getChannels(TextChannel.class).get(0);

				SystemNotificationChannelManager sys = Klassenserver7bbot.getInstance().getsyschannell();
				sys.insertChannel(chan);

				channel.sendMessage("Systemchannel was sucsessful set to " + chan.getAsMention()).queue();

			} else {
				SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "syschannel [@new syschannel]", m);
			}

		}

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
