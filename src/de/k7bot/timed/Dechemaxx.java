package de.k7bot.timed;

import de.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class Dechemaxx{

	public static void notifymessage() {
		
		Guild guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(779024287733776454l);
		TextChannel channel = guild.getTextChannelById(908780877104959508l);
		String mess = "❗❗LEUTE DER DECHEMAXX STEHT AN❗❗";
		
		channel.sendMessage(mess).queue();;
		
	}

}
