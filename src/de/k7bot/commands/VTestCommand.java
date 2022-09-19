package de.k7bot.commands;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.timed.VplanNEW_XML;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class VTestCommand implements ServerCommand {

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

		String[] args = message.getContentDisplay().split(" ");

		if (args.length > 1 && !message.getMentions().getChannels().isEmpty()) {
			new VplanNEW_XML().sendVplanMessage(true, args[1], message.getMentions().getChannels().get(0));
		}else {
			SyntaxError.oncmdSyntaxError(channel, "vtest [klasse] #channel", m);
		}

	}

}
