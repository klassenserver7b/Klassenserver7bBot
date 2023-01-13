package de.k7bot.music.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ClearQueueCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		long guildid = channel.getGuild().getIdLong();
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
		MusicUtil.updateChannel(channel);
		Queue queue = controller.getQueue();
		queue.clearQueue();
	}

	@Override
	public String gethelp() {
		String help = "Löscht die aktuelle Queuelist.";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}
}
