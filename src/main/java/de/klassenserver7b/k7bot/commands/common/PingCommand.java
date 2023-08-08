package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class PingCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "Gibt den aktuellen Ping des Bots zurück.";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "ping" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.ALLGEMEIN;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		long gatewayping = getGatewayping(channel.getJDA());
		long time = getRESTping(channel.getJDA());
		channel.sendMessageFormat("Pong! %dms", time, gatewayping, "s").queue();
	}

	public Long getGatewayping(JDA jda) {

		long gatewayping = jda.getGatewayPing();

		return gatewayping;
	}

	public Long getRESTping(JDA jda) {

		long time = jda.getRestPing().complete();

		return time;
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