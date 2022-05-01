package de.k7bot.music.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class NoncoreCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		List<String> songs = new ArrayList<>();

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {

				MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
						.getController(vc.getGuild().getIdLong());
				Queue queue = controller.getQueue();

				queue.getQueuelist().forEach(song -> {
					String titlelow = (song.getInfo()).title.toLowerCase();

					titlelow = titlelow.replaceAll("nightcore", "");

					titlelow = titlelow.replaceAll("female version", "");

					titlelow = titlelow.replaceAll("switching vocals", "");
					titlelow = titlelow.replaceAll("female nightcore", "");
					titlelow = titlelow.replaceAll("lyrics", "");
					titlelow = titlelow.replaceAll(" - ", "");
					titlelow = titlelow.replaceAll("\\(\\)", "");
					titlelow = titlelow.replaceAll("\\[\\]", "");
					songs.add(titlelow.trim());
				});
				File file = new File("D:\\Felix\\Desktop\\Bot\\bot.txt");

				try {
					PrintStream stream = new PrintStream(file);

					songs.forEach(song -> stream.println(song));

					stream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {

				((Message) channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete()).delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		} else {

			((Message) channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete()).delete()
					.queueAfter(10L, TimeUnit.SECONDS);
		}
	}

	@Override
	public String gethelp() {
		String help = null;
		return help;
	}

	@Override
	public String getcategory() {
		String category = null;
		return category;
	}
}