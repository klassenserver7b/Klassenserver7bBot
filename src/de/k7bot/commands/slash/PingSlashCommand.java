package de.k7bot.commands.slash;

import org.jetbrains.annotations.NotNull;

import de.k7bot.commands.common.PingCommand;
import de.k7bot.commands.types.TopLevelSlashCommand;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PingSlashCommand implements TopLevelSlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply().complete();

		PingCommand pc = new PingCommand();
		TextChannel channel = event.getChannel().asTextChannel();

		long gatewayping = pc.getGatewayping(channel);
		long time = pc.getRESTping(channel);

		hook.sendMessageFormat("Pong! %dms", time, gatewayping, "s").queue();

	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("ping", "Zeigt den aktuellen Ping des Bots");
	}

}
