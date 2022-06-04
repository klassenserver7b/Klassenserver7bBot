package de.k7bot.util;

import de.k7bot.commands.types.ServerCommand;
import java.awt.Color;
import java.time.LocalDateTime;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class DisabledAPI implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String getcategory() {
		return null;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Disabled Command")
				.setFooter("requested by @" + m.getEffectiveName()).setColor(Color.decode("#ff0000"))
				.setTimestamp(LocalDateTime.now())
				.setDescription(
						"We are sorry but you try to use an command which is currently disabled!\nPlease contact the Bot-Admin if you think that is an issue.")
				.build()).queue();

	}

}
