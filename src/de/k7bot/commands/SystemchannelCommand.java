package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.moderation.SystemNotificationChannelHolder;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SystemchannelCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return "Ändert den Channel für Systembenachrichtigungen (z.B. Einladungslink Logs oder gelöschte Nachrichten Logs) des Bots auf diesem Server.\n - kann nur von Personen mit der Berechtigung 'Server verwalten' ausgeführt werden!\n - z.B. [prefix]syschannel [@new syschannel]";
	}

	@Override
	public String getcategory() {
		return "Allgemein";
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.MANAGE_SERVER)) {

			if (!message.getMentionedChannels().isEmpty()) {
				
				TextChannel chan = message.getMentionedChannels().get(0);
				
				SystemNotificationChannelHolder sys = Klassenserver7bbot.INSTANCE.getsyschannell();				
				sys.insertChannel(chan);
				
			}else {
				SyntaxError.oncmdSyntaxError(channel, "syschannel [@new syschannel]", m);
			}

		}

	}

}
