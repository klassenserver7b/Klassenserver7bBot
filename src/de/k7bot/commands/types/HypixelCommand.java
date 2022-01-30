package de.k7bot.commands.types;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface HypixelCommand {
  void performHypixelCommand(Member m, TextChannel channel, Message Message);
}