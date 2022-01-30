package de.k7bot.commands;

import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PingCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		long gatewayping = channel.getJDA().getGatewayPing();
		channel.getJDA().getRestPing()
				.queue((time) -> channel.sendMessageFormat("Pong! %dms", time, gatewayping, "s").queue()

				);
		message.addReaction("U+1F3D3");
	}
}