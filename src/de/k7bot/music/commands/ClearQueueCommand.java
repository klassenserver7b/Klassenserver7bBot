package de.k7bot.music.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ClearQueueCommand implements ServerCommand {
	
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		long guildid = channel.getGuild().getIdLong();
		MusicController controller = Klassenserver7bbot.INSTANCE.playerManager.getController(guildid);
		Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);
		Queue queue = controller.getQueue();
		queue.clearQueue();
	}
	

	@Override
	public String gethelp() {
		String help = "Löscht die aktuelle Queuelist.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
}
