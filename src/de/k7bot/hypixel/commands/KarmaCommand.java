
package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import de.k7bot.util.SyntaxError;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hypixel.api.HypixelAPI;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class KarmaCommand implements HypixelCommand {
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {

		HypixelAPI api = Klassenserver7bbot.INSTANCE.getHypixelAPI();

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

				e1.printStackTrace();
			}

			if (id != null) {
				channel.sendTyping().queue();
				try {
					channel.sendMessage(name + " has "
							+ api.getPlayerByUuid(id).get().getPlayer().getKarma() + " Karma.").queue();
				} catch (ExecutionException e) {
					System.err.println("Oh no, our API request failed!");
					e.getCause().printStackTrace();
				} catch (InterruptedException e) {

					System.err.println("Oh no, the player fetch thread was interrupted!");
					e.printStackTrace();
				}
			} else {
				channel.sendMessage(name + " is not a valid username " + m.getAsMention())
						.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			}

		} else {

			SyntaxError.oncmdSyntaxError(channel, "hypixel friends [playername]", m);
		}
	}
}
