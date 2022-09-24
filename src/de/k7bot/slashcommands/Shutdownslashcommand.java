package de.k7bot.slashcommands;

import java.util.concurrent.TimeUnit;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.util.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class Shutdownslashcommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		Member m = event.getMember();
		TextChannel channel = event.getChannel().asTextChannel();

		if (m.getIdLong()==Klassenserver7bbot.INSTANCE.getOwnerId()) {

			EmbedBuilder build = new EmbedBuilder();

			build.setColor(16711680);
			build.setFooter("Requested by @" + event.getMember().getEffectiveName());
			build.setDescription("Bot is shutting down!");
			
			event.replyEmbeds(build.build()).complete().deleteOriginal().completeAfter(10, TimeUnit.SECONDS);

			Klassenserver7bbot.INSTANCE.exit = true;
			Klassenserver7bbot.INSTANCE.onShutdown();
		} else {
			PermissionError.onPermissionError(m, channel);
		}

	}

}
