package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class SyntaxError {
	public static void oncmdSyntaxError( TextChannel channel, String syntax, Member memb) {
		((Message) channel.sendMessage("Please use the following syntax: "
				+ Klassenserver7bbot.INSTANCE.prefixl.get(channel.getGuild().getIdLong()) + syntax
				+ memb.getAsMention()).complete()).delete().queueAfter(10L, TimeUnit.SECONDS);
	}
}
