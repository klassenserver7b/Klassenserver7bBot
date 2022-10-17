package de.k7bot.commands.common;

import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PingCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		long gatewayping = getGatewayping(channel);
		long time = getRESTping(channel);
		channel.sendMessageFormat("Pong! %dms", time, gatewayping, "s").queue();
	}

	@Override
	public String gethelp() {
		String help = "Gibt den aktuellen Ping des Bots zurück.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Allgemein";
		return category;
	}

	public Long getGatewayping(TextChannel channel) {

		long gatewayping = channel.getJDA().getGatewayPing();

		return gatewayping;
	}

	public Long getRESTping(TextChannel channel) {

		long time = channel.getJDA().getRestPing().complete();

		return time;
	}
}