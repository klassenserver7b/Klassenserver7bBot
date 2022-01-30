package de.k7bot.slashcommands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.manage.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class Shutdownslashcommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandEvent event) {

		Member m = event.getMember();
		TextChannel channel = event.getTextChannel();

		if (m.hasPermission(new Permission[] { Permission.ADMINISTRATOR })) {
			Klassenserver7bbot.INSTANCE.exit = true;
			Klassenserver7bbot.INSTANCE.onShutdown();
			event.deferReply(false);
		} else {
			PermissionError.onPermissionError(m, channel);
		}

	}

}
