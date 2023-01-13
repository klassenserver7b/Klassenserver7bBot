package de.k7bot.util.customapis;

import java.awt.Color;
import java.time.LocalDateTime;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DisabledAPI implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
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
