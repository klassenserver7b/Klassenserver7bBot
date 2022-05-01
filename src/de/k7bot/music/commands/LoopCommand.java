package de.k7bot.music.commands;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.Color;

public class LoopCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {

				MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
						.getController(vc.getGuild().getIdLong());
				if (controller.getPlayer().getPlayingTrack() != null
						|| !(controller.getQueue().getQueuelist().isEmpty())) {

					onLoop(controller);
					channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#4d05e8"))
							.setDescription("Queue looped!").build()).queue();

				} else {

					channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#ff0000"))
							.setDescription("There isn't a song/playlist to loop!").build()).queue();

				}

			} else {

				channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		} else {

			channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
		}

	}

	@Override
	public String gethelp() {
		String help = "loopt die aktuelle Queuelist";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}

	public static void onLoop(@Nonnull MusicController controller) {

		controller.getQueue().loop();

	}

}
