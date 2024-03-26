package de.klassenserver7b.k7bot.music.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import de.klassenserver7b.k7bot.music.lavaplayer.Queue;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class ClearQueueCommand implements ServerCommand { 

 	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "Löscht die aktuelle Queuelist.";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "clearqueue", "cq" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIC;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

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
