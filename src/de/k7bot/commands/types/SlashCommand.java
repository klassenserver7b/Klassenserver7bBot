package de.k7bot.commands.types;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface SlashCommand {
	void performSlashCommand(SlashCommandInteraction event);

	@NotNull
	SlashCommandData getCommandData();
}