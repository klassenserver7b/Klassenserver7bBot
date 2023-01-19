package de.k7bot.music.commands.common;

import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;

public class PauseCommand implements ServerCommand {
	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		MusicUtil.updateChannel(channel);
		if (!player.isPaused()) {

			MusicUtil.updateChannel(channel);
			player.setPaused(true);
			channel.sendMessage(":pause_button: paused").queue();
		} else {
			channel.sendMessage("the player is already paused!" + m.getAsMention()).complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
		}

	}

	@Override
	public String gethelp() {
		String help = "Pausiert den aktuellen Track.";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}
}
