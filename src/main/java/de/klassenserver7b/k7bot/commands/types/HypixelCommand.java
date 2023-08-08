package de.klassenserver7b.k7bot.commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public interface HypixelCommand {
	void performHypixelCommand(Member m, GuildMessageChannel channel, Message message);
}