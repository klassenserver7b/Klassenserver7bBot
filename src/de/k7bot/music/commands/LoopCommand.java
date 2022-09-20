package de.k7bot.music.commands;

import javax.annotation.Nonnull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.Color;

public class LoopCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}
		
		AudioChannel vc = MusicUtil.getMembVcConnection(m);

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
