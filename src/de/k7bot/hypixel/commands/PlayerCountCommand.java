package de.k7bot.hypixel.commands;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.CountsReply;

public class PlayerCountCommand implements HypixelCommand {

	private final Logger log;

	public PlayerCountCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {

		HypixelAPI api = Klassenserver7bbot.getInstance().getHypixelAPI();

		try {
			CountsReply gescount = api.getCounts().get();

			channel.sendMessage("Auf Hypixel spielen aktuell " + gescount.getPlayerCount() + " Spieler.").queue();
		} catch (ExecutionException e) {
			System.err.println("Oh no, our API request failed!");
			e.getCause().printStackTrace();
		} catch (InterruptedException e) {

			System.err.println("Oh no, the player fetch thread was interrupted!");
			log.error(e.getMessage(), e);
		}
	}
}