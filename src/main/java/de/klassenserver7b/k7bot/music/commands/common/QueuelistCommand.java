package de.klassenserver7b.k7bot.music.commands.common;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import de.klassenserver7b.k7bot.music.lavaplayer.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class QueuelistCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "Zeigt die aktuelle Queuelist an.";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "queuelist", "ql" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		MusicController contr = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(channel.getGuild().getIdLong());
		Queue queue = contr.getQueue();
		List<AudioTrack> queuelist = queue.getQueuelist();

		if (!queuelist.isEmpty()) {

			EmbedBuilder builder = new EmbedBuilder();
			builder.setFooter("Requested by @" + m.getEffectiveName());
			builder.setTitle("Queue for Guild: " + channel.getGuild().getName() + " (" + queuelist.size() + " entrys)");
			builder.setColor(Color.decode("#14cdc8"));
			builder.setThumbnail("https://openclipart.org/image/800px/211805");

			StringBuilder strbuild = new StringBuilder();

			for (AudioTrack t : queuelist) {
				String content;
				if (t instanceof YoutubeAudioTrack) {
					content = "- " + t.getInfo().title.replaceAll("\\|", "-") + "\n";
				} else {
					content = "- " + t.getInfo().author + " - " + t.getInfo().title.replaceAll("\\|", "-") + "\n";
				}

				if (!((strbuild.toString().length() + content.length()) >= 4000)) {
					strbuild.append(content);
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
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}
}