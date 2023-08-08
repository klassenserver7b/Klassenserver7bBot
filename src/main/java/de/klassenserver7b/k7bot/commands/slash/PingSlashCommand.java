package de.klassenserver7b.k7bot.commands.slash;

import org.jetbrains.annotations.NotNull;

import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class PingSlashCommand implements TopLevelSlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply().complete();

		long gatewayping = getGatewayping(event.getJDA());
		long time = getRESTping(event.getJDA());

		hook.sendMessageFormat("Pong! %dms", time, gatewayping, "s").queue();

	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("ping", "Zeigt den aktuellen Ping des Bots");
	}

	public Long getGatewayping(JDA jda) {

		long gatewayping = jda.getGatewayPing();

		return gatewayping;
	}

	public Long getRESTping(JDA jda) {

		long time = jda.getRestPing().complete();

		return time;
	}

}
