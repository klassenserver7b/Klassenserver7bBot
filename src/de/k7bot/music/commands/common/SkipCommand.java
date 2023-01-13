package de.k7bot.music.commands.common;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SkipCommand implements ServerCommand {
	public static boolean onskip = false;

	@Override
	public String gethelp() {
		String help = "Überspringt den aktuellen Song.";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		String[] args = message.getContentDisplay().split(" ");
		long guildid = channel.getGuild().getIdLong();
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
		Queue queue = controller.getQueue();
		AudioTrack lastTrack = controller.getPlayer().getPlayingTrack();
		onskip = true;

		if (args.length == 1) {
			onskip = false;
			if (queue.next(lastTrack)) {
				return;
			}
		} else {

			try {
				for (int i = 0; i <= Integer.parseInt(args[1]) - 1; i++) {

					if (queue.getQueuelist().size() > 1) {

						queue.next(lastTrack);

					}
				}

				onskip = false;
				queue.next(lastTrack);

				EmbedBuilder builder = new EmbedBuilder();
				builder.setTimestamp(OffsetDateTime.now());
				builder.setFooter("Requested by @" + m.getEffectiveName());
				builder.setTitle(Integer.parseInt(args[1]) + " tracks skipped");

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(10L, TimeUnit.SECONDS);

			} catch (NumberFormatException e) {
				SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "skip [int]", m);
			}
		}
		onskip = false;
	}
}