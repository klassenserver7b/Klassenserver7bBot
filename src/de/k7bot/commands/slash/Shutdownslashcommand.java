package de.k7bot.commands.slash;

import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.util.errorhandler.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Shutdownslashcommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		Member m = event.getMember();
		TextChannel channel = event.getChannel().asTextChannel();

		if (m.getIdLong() == Klassenserver7bbot.getInstance().getOwnerId()) {

			EmbedBuilder build = new EmbedBuilder();

			build.setColor(16711680);
			build.setFooter("Requested by @" + event.getMember().getEffectiveName());
			build.setDescription("Bot is shutting down!");

			event.replyEmbeds(build.build()).complete().deleteOriginal().completeAfter(10, TimeUnit.SECONDS);

			Klassenserver7bbot.getInstance().setexit(true);
			Klassenserver7bbot.getInstance().onShutdown();
		} else {
			PermissionError.onPermissionError(m, channel);
		}

	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("shutdown", "fährt den Bot herunter")
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
	}

}
