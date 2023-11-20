package de.klassenserver7b.k7bot.listener;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemesReact extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)) {
			if (event.getChannel().getIdLong() == 780000480406405130L
					|| event.getChannel().getIdLong() == 1173538520162377788L) {
				long messid = event.getMessage().getIdLong();
				GuildMessageChannel chan = event.getChannel().asGuildMessageChannel();

				chan.addReactionById(messid, Emoji.fromFormatted("U+2B06")).queue();

				chan.addReactionById(messid, Emoji.fromFormatted("U+2B07")).queue();
			}
		}
	}
}