package de.klassenserver7b.k7bot.music.commands.common;

import java.awt.Color;

import javax.annotation.Nonnull;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public class UnLoopCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "entloopt die aktuelle Queuelist";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "unloop" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		unLoop(vc.getGuild().getIdLong());
		channel.sendMessageEmbeds(
				new EmbedBuilder().setColor(Color.decode("#4d05e8")).setDescription("Queue unlooped!").build()).queue();

	}

	public static void unLoop(@Nonnull long guildId) {

		Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildId).getQueue().unLoop();

	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}

}
