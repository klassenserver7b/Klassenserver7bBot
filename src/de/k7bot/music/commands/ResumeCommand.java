package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.utilities.MusicUtil;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ResumeCommand implements ServerCommand {
	

	@Override
	public String gethelp() {
		String help = "Setzt den aktuellen Track fort.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
	
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.INSTANCE.getPlayerUtil().getController(vc.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		MusicUtil.updateChannel(channel);
		if (player.isPaused()) {
			MusicUtil.updateChannel(channel);
			player.setPaused(false);
			channel.sendMessage(":arrow_forward: resumed").queue();
		} else {
			channel.sendMessage("player is already playing!" + m.getAsMention()).complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
		}

	}
}