
package de.klassenserver7b.k7bot.hypixel.commands;

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
import net.hypixel.api.reply.PlayerReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class HypixelPlayerInfoCommand implements HypixelCommand {
	UUID id = null;

	private final Logger log;

	public HypixelPlayerInfoCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performHypixelCommand(Member m, GuildMessageChannel channel, Message message) {
		String name;

		HypixelAPI api = Klassenserver7bbot.getInstance().getHypixelAPI();

		String[] args = message.getContentDisplay().split(" ");

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
			this.id = MojangAPI.getUUID(name);
		} catch (APIException | InvalidPlayerException | IOException e1) {

			channel.sendMessage("**Invalid Playername** - please enter a valid playername").complete().delete()
					.queueAfter(10, TimeUnit.SECONDS);
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "hypixel playerinfo [playername]", m);
		}

		if (this.id != null) {

			channel.sendTyping().queue();

			try {
				PlayerReply apiReply = api.getPlayerByUuid(this.id).get();

				PlayerReply.Player player = apiReply.getPlayer();

				StringBuilder build = new StringBuilder();

				build.append("Here are some of " + player.getName() + "'s stats: \n\n");
				build.append("```UUID: " + player.getUuid() + "\n");
				build.append("Rank: " + player.getHighestRank() + "\n");
				build.append("On Build Team?: " + player.isOnBuildTeam() + "\n");
				build.append("Exact Level: " + player.getNetworkLevel() + "\n");
				build.append("Experience: " + player.getNetworkExp() + "\n");
				build.append("Karma: " + player.getKarma() + "\n");
				build.append("MC Version: " + player.getLastKnownMinecraftVersion() + "\n");
				build.append("Last Game Type: " + player.getMostRecentGameType() + "\n");
				build.append("Previous Names: " + player.getArrayProperty("knownAliases") + "\n");

			} catch (ExecutionException e) {
				System.err.println("Oh no, our API request failed!");
				e.getCause().printStackTrace();

			} catch (InterruptedException e) {
				System.err.println("Oh no, the player fetch thread was interrupted!");
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();

			}
		} else {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "hypixel playerinfo [playername]", m);
		}
	}
}