package de.k7bot.music.commands;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class UnLoopCommand implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		
		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {
				
				unLoop(vc.getGuild().getIdLong());
				channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#4d05e8")).setDescription("Queue unlooped!").build()).queue();
				
			} else {

				channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		} else {

			channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
		}
		
	}
	
	@Override
	public String gethelp() {
		String help = "entloopt die aktuelle Queuelist";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
	
	public static void unLoop(@Nonnull long guildId) {
		
		Klassenserver7bbot.INSTANCE.playerManager.getController(guildId).getQueue().unLoop();
		
	}
	
}

