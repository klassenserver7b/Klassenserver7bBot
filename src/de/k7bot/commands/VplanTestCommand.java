package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.timed.VPlan_main;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class VplanTestCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		VPlan_main vmain = Klassenserver7bbot.INSTANCE.getvmain();

		vmain.sendvplanMessage("next");

	}

}
