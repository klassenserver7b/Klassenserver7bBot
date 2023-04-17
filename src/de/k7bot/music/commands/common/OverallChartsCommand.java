package de.k7bot.music.commands.common;

import java.util.HashMap;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.commands.generic.GenericChartsCommand;
import de.k7bot.music.utilities.ChartList;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class OverallChartsCommand extends GenericChartsCommand implements ServerCommand {

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

		ChartList chartlist = new ChartList();
		HashMap<String, Long> charts = chartlist.getcharts();

		sendMessage(new GenericMessageSendHandler(channel), charts);

	}

}
