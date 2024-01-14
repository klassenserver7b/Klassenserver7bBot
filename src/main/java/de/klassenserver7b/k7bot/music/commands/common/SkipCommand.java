package de.klassenserver7b.k7bot.music.commands.common;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import de.klassenserver7b.k7bot.music.lavaplayer.Queue;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.music.utilities.spotify.SpotifyAudioTrack;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class SkipCommand implements ServerCommand {

	private boolean isEnabled;
	public static boolean onskip = false;

	@Override
	public String gethelp() {
		String help = "Überspringt den aktuellen Song.";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "skip", "s" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		String[] args = message.getContentDisplay().split(" ");
		long guildid = channel.getGuild().getIdLong();
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
		Queue queue = controller.getQueue();
		AudioTrack lastTrack = controller.getPlayer().getPlayingTrack();

		if (lastTrack instanceof SpotifyAudioTrack) {
			new File(lastTrack.getIdentifier()).delete();
		}

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

				EmbedBuilder builder = EmbedUtils.getDefault(channel.getGuild().getIdLong());
				builder.setFooter("Requested by @" + m.getEffectiveName());
				builder.setTitle(Integer.parseInt(args[1]) + " tracks skipped");

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(10L, TimeUnit.SECONDS);

			} catch (NumberFormatException e) {
				SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "skip [int]", m);
			}
		}
		onskip = false;
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