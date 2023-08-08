package de.klassenserver7b.k7bot.hypixel.commands;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.HypixelCommand;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.hypixel.api.HypixelAPI;

public class HypixelRankCommand implements HypixelCommand {

	private final Logger log;

	public HypixelRankCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performHypixelCommand(Member m, GuildMessageChannel channel, Message message) {

		HypixelAPI api = Klassenserver7bbot.getInstance().getHypixelAPI();

		UUID id = null;

		String[] args = message.getContentDisplay().split(" ");

		if (args.length >= 3) {
			String name;
			if (args.length > 3) {

				StringBuilder builder = new StringBuilder();
				for (int i = 2; i <= args.length; i++) {
					builder.append(" " + args[i]);
				}
				name = builder.toString().trim();
			} else {

				name = args[2];
			}

			try {
				id = MojangAPI.getUUID(name);
			} catch (APIException | InvalidPlayerException | IOException e1) {

				log.error(e1.getMessage(), e1);
			}

			if (id != null) {
				channel.sendTyping().queue();
				try {

					channel.sendMessage("The highest Rank of " + name + " is: "
							+ api.getPlayerByUuid(id).get().getPlayer().getHighestRank()).queue();
				} catch (ExecutionException e) {
					System.err.println("Oh no, our API request failed!");
					e.getCause().printStackTrace();
				} catch (InterruptedException e) {

					System.err.println("Oh no, the player fetch thread was interrupted!");
					log.error(e.getMessage(), e);
				}
			} else {
				channel.sendMessage(name + " is not a valid username " + m.getAsMention()).complete().delete()
						.queueAfter(10L, TimeUnit.SECONDS);
			}
		} else {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "hypixel rank [playername]", m);
		}
	}
}