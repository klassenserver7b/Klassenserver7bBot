package de.k7bot.util.errorhandler;

import java.util.concurrent.TimeUnit;

import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.entities.Member;

public class SyntaxError {
	public static void oncmdSyntaxError(GenericMessageSendHandler channel, String syntax, Member memb) {
		channel.sendMessage("Please use the following syntax: " + "[prefix]" + syntax + memb.getAsMention()).complete()
				.delete().queueAfter(10L, TimeUnit.SECONDS);
	}
}
