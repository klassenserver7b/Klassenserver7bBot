package de.klassenserver7b.k7bot.commands.types;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface TopLevelSlashCommand extends SlashCommand {
	@Nonnull
	SlashCommandData getCommandData();
}