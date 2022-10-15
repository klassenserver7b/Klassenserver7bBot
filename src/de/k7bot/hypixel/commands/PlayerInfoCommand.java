
package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import de.k7bot.util.errorhandler.SyntaxError;

import java.io.IOException;
import java.util.Objects;
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
import net.hypixel.api.reply.FriendsReply;
import net.hypixel.api.reply.PlayerReply;

public class PlayerInfoCommand implements HypixelCommand {
	UUID id = null;
	String friends = "";

	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		String name;
		friends = "";

		HypixelAPI api = Klassenserver7bbot.INSTANCE.getHypixelAPI();

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
			SyntaxError.oncmdSyntaxError(channel, "hypixel playerinfo [playername]", m);
		}

		if (this.id != null) {

			channel.sendTyping().queue();

			try {
				PlayerReply apiReply = api.getPlayerByUuid(this.id).get();
				FriendsReply freply = api.getFriends(this.id).get();

				PlayerReply.Player player = apiReply.getPlayer();
				freply.getFriendShips().forEach(friend -> {

					if (friend.getUuidSender().compareTo(this.id) != 0) {

						try {
							if (!Objects.equals(MojangAPI.getName(this.id), MojangAPI.getName(friend.getUuidSender()))) {
								this.friends = this.friends
										+ MojangAPI.getUsername(friend.getUuidSender()) + ", ";
							}
						} catch (APIException | IOException e) {

							e.printStackTrace();
						}
					} else {

						try {

							this.friends = this.friends
									+ MojangAPI.getUsername(friend.getUuidReceiver()) + ", ";
						} catch (APIException | IOException e) {
							e.printStackTrace();
						}
					}
				});

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

				String friendstring = "Friends: " + this.friends;

				if (build.toString().length() + friendstring.length() > 2000) {

					build.append("```");
					channel.sendMessage(build.toString().trim()).queue();
					channel.sendMessage("```" + friendstring + "```").queue();

				} else {

					build.append(friendstring + "```");
					channel.sendMessage(build.toString().trim()).queue();

				}

			} catch (ExecutionException e) {
				System.err.println("Oh no, our API request failed!");
				e.getCause().printStackTrace();

			} catch (InterruptedException e) {
				System.err.println("Oh no, the player fetch thread was interrupted!");
				e.printStackTrace();
				Thread.currentThread().interrupt();

			}
		} else {
			SyntaxError.oncmdSyntaxError(channel, "hypixel playerinfo [playername]", m);
		}
	}
}