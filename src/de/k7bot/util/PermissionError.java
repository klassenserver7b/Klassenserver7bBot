package de.k7bot.util;

import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PermissionError {
	public static void onPermissionError(Member m, TextChannel channel) {
		(channel.sendMessage("You don't have the permission to do this!" + m.getAsMention()).complete())
				.delete().queueAfter(10L, TimeUnit.SECONDS);
	}
}