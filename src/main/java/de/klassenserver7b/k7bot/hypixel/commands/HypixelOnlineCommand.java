
package de.klassenserver7b.k7bot.hypixel.commands;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.HypixelCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
import me.kbrewster.mojangapi.MojangAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.StatusReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HypixelOnlineCommand implements HypixelCommand {

	private final Logger log;

	public HypixelOnlineCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performHypixelCommand(Member m, GuildMessageChannel channel, Message message) {
		String name;

		HypixelAPI api = Klassenserver7bbot.getInstance().getHypixelAPI();

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
					log.error(e1.getMessage(), e1);
				} catch (InvalidPlayerException e) {
					channel.sendMessage(
							"**This is NOT a valid playername**! Please check if you spelled it correct! You have entered the following name: \""
									+ name + "\"")
							.complete().delete().queueAfter(15, TimeUnit.SECONDS);
				}

				if (id != null) {
					String state;
					try {

						apiReply = api.getStatus(id).get();

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

					} catch (InterruptedException | ExecutionException e) {
						channel.sendMessageEmbeds(EmbedUtils
								.getErrorEmbed("Invalid response from HypixelAPI", channel.getGuild().getIdLong())
								.build()).queue();
						log.error(e.getMessage(), e);
					}
				}
			}

		} else {

			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "hypixel online [playername]", m);

		}

	}
}