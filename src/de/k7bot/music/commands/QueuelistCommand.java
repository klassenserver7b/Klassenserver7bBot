package de.k7bot.music.commands;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class QueuelistCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		

		MusicController contr = Klassenserver7bbot.INSTANCE.playerManager.getController(channel.getGuild().getIdLong());
		Queue queue = contr.getQueue();
		List<AudioTrack> queuelist = queue.getQueuelist();

		if (!queuelist.isEmpty()) {

			EmbedBuilder builder = new EmbedBuilder();
			builder.setFooter("Requested by @" + m.getEffectiveName());
			builder.setTitle("Queue for Guild: " + channel.getGuild().getName());
			builder.setColor(Color.decode("#14cdc8"));
			builder.setThumbnail("https://openclipart.org/image/800px/211805");

			StringBuilder strbuild = new StringBuilder();

			for (int i = 0; i < queuelist.size(); i++) {
				AudioTrack t = queuelist.get(i);
				String content = "- " + t.getInfo().title.replaceAll("\\|", "-") + "\n";

				if (!((strbuild.toString().length() + content.length()) >= 4000)) {
					strbuild.append("- " + t.getInfo().title.replaceAll("\\|", "-") + "\n");
				} else {

					strbuild.append("... \n...");
					break;

				}

			}

			builder.setDescription(strbuild.toString().trim());

			channel.sendMessageEmbeds(builder.build()).queue();

		} else {

			EmbedBuilder build = new EmbedBuilder();

			build.setColor(16711680);
			build.setDescription("The Queue for this guild is empty!");

			channel.sendMessageEmbeds(build.build()).complete().delete().queueAfter(15, TimeUnit.SECONDS);

		}

	}
	
	@Override
	public String gethelp() {
		String help = "Zeigt die aktuelle Queuelist an.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}

}
