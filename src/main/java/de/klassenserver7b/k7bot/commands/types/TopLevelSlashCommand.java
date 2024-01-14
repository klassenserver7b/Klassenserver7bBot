package de.klassenserver7b.k7bot.commands.types;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import javax.annotation.Nonnull;

public interface TopLevelSlashCommand extends SlashCommand {
	@Nonnull
	SlashCommandData getCommandData();
}