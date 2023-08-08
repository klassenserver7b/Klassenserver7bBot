package de.klassenserver7b.k7bot.listener;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemesReact extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)) {
			if (event.getGuild().getIdLong() == 779024287733776454L
					&& event.getChannel().getIdLong() == 780000480406405130L) {
				Long messid = event.getMessage().getIdLong();
				GuildMessageChannel chan = event.getChannel().asGuildMessageChannel();

				chan.addReactionById(messid, chan.getGuild().getEmojiById(896482215473610812L)).queue();

				chan.addReactionById(messid, chan.getGuild().getEmojiById(896482181759778897L)).queue();
			}
		}
	}
}