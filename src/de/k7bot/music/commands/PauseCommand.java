package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class PauseCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		MusicUtil util = Klassenserver7bbot.INSTANCE.getMusicUtil();
		
		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {

				MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
						.getController(vc.getGuild().getIdLong());
				AudioPlayer player = controller.getPlayer();
				util.updateChannel(channel);
				if (!player.isPaused()) {

					util.updateChannel(channel);
					player.setPaused(true);
					channel.sendMessage(":pause_button: paused").queue();
				} else {
					((Message) channel.sendMessage("the player is already paused!" + m.getAsMention()).complete())
							.delete().queueAfter(10L, TimeUnit.SECONDS);
				}
			}
		}
	}
	
	@Override
	public String gethelp() {
		String help = "Pausiert den aktuellen Track.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
}
