package de.klassenserver7b.k7bot.util.customapis;

import java.awt.Color;
import java.time.LocalDateTime;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class DisabledAPI implements ServerCommand {

	private final String[] commandstrings;

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String[] getCommandStrings() {
		return commandstrings;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	/**
	 * @param commandStrings
	 */
	public DisabledAPI(String[] commandStrings) {
		this.commandstrings = commandStrings;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		channel.sendMessageEmbeds(new EmbedBuilder().setTitle("Disabled Command")
				.setFooter("requested by @" + m.getEffectiveName()).setColor(Color.red)
				.setTimestamp(LocalDateTime.now())
				.setDescription(
						"We are sorry but you try to use an command which is currently disabled!\nPlease contact the Bot-Admin if you think that is an issue.")
				.build()).queue();

	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void disableCommand() {
		// nothing to do here

	}

	@Override
	public void enableCommand() {
		// nothing to do here
	}

}
