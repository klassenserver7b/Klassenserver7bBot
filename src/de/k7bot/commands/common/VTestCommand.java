package de.k7bot.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.customapis.VplanNEW_XML;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class VTestCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");

		if (args.length > 1 && !message.getMentions().getChannels().isEmpty()) {

			GuildChannel chan = message.getMentions().getChannels().get(0);

			if (chan.getType() == ChannelType.TEXT) {
				new VplanNEW_XML().sendVplanToChannel(true, args[1], (TextChannel) chan);
			}
		} else {
			SyntaxError.oncmdSyntaxError(channel, "vtest [klasse] #channel", m);
		}

	}

}
