
package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import de.k7bot.util.SyntaxError;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.StatusReply;

public class OnlineCommand implements HypixelCommand {
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		String name;

		HypixelAPI api = Klassenserver7bbot.INSTANCE.getHypixelAPI();

		StatusReply apiReply = null;

		UUID id = null;

		String[] args = message.getContentDisplay().trim().split(" ");

		if (args.length > 2) {

			StringBuilder builder = new StringBuilder();

			for (int i = 2; i < args.length; i++) {
				builder.append(" " + args[i]);

				name = builder.toString().trim();

				try {
					id = MojangAPI.getUUID(name);
				} catch (APIException | IOException e1) {
					e1.printStackTrace();
				} catch (InvalidPlayerException e) {
					channel.sendMessage("**This is NOT a valid playername**! Please check if you spelled it correct! You have entered the following name: \""+name+"\"")
							.complete().delete().queueAfter(15, TimeUnit.SECONDS);
				}

				if (id != null) {
					String state;
					try {
						apiReply = api.getStatus(id).get();
					} catch (InterruptedException | ExecutionException e) {

						e.printStackTrace();
					}

					channel.sendTyping().queue();

					if (apiReply.getSession().isOnline()) {

						if (apiReply.getSession().getMap() == null) {
							state = "online and plays " + apiReply.getSession().getMode();
						} else {
							state = "online and plays " + apiReply.getSession().getMode() + " on "
									+ apiReply.getSession().getMap();
						}

					} else {

						state = "offline";
					}

					channel.sendMessage(name + " is currently " + state).queue();
				}
			}

		} else {

			SyntaxError.oncmdSyntaxError(channel,"hypixel online [playername]", m);

		}

	}
}