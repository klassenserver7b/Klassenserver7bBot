package de.k7bot.slashcommands;

import de.k7bot.commands.PingCommand;
import de.k7bot.commands.types.SlashCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class PingSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply().complete();

		PingCommand pc = new PingCommand();
		TextChannel channel = event.getTextChannel();

		long gatewayping = pc.getGatewayping(channel);
		long time = pc.getRESTping(channel);

		hook.sendMessageFormat("Pong! %dms", time, gatewayping, "s").queue();

	}

}
