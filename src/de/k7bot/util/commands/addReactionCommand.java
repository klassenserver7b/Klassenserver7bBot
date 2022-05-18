package de.k7bot.util.commands;

import de.k7bot.commands.types.ServerCommand;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class addReactionCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");
		List<TextChannel> channels = message.getMentionedChannels();
		List<Emote> emotes = message.getEmotes();

		if (!channels.isEmpty()) {
			TextChannel tc = message.getMentionedChannels().get(0);
			String MessageIdString = args[2];

			try {
				long MessageId = Long.parseLong(MessageIdString);

				List<String> customemotes = new ArrayList<>();

				for (Emote emote : emotes) {
					tc.addReactionById(MessageId, emote).queue();
					customemotes.add(":" + emote.getName() + ":");
				}

				for (int i = 3; i < args.length; i++) {
					String utfemote = args[i];
					if (!customemotes.contains(utfemote)) {
						tc.addReactionById(MessageId, args[i]).queue();
					}
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