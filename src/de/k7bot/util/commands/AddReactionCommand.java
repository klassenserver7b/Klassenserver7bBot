package de.k7bot.util.commands;

import de.k7bot.commands.types.ServerCommand;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

public class AddReactionCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");
		List<TextChannel> channels = message.getMentions().getChannels(TextChannel.class);
		List<CustomEmoji> emotes = message.getMentions().getCustomEmojis();

		if (!channels.isEmpty()) {
			TextChannel tc = message.getMentions().getChannels(TextChannel.class).get(0);
			String MessageIdString = args[2];

			try {
				long MessageId = Long.parseLong(MessageIdString);

				for (CustomEmoji emote : emotes) {
					tc.addReactionById(MessageId, emote).queue();
				}

			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String gethelp() {
		return "Reagiert als Bot auf die ausgewählte Nachricht.\n - z.B. [prefix]react #textchannel [messageid] [:emote:] <:emote:> <:emote:> usw.";
	}

	@Override
	public String getcategory() {
		return "Tools";
	}
}