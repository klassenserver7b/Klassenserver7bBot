package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ResumeCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		MusicUtil util = Klassenserver7bbot.INSTANCE.getMusicUtil();
		
		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {

				MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
						.getController(vc.getGuild().getIdLong());
				AudioPlayer player = controller.getPlayer();
				util.updateChannel(channel);
				if (player.isPaused()) {
					util.updateChannel(channel);
					player.setPaused(false);
					channel.sendMessage(":arrow_forward: resumed").queue();
				} else {
					((Message) channel.sendMessage("player is already playing!" + m.getAsMention()).complete()).delete()
							.queueAfter(10L, TimeUnit.SECONDS);
				}
			}
		}
	}
}