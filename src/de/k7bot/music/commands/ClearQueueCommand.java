package de.k7bot.music.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ClearQueueCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		long guildid = channel.getGuild().getIdLong();
		MusicController controller = Klassenserver7bbot.INSTANCE.playerManager.getController(guildid);
		Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);
		Queue queue = controller.getQueue();
		queue.clearQueue();
	}
}
