package de.klassenserver7b.k7bot.hypixel.commands;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.HypixelCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PunishmentStatsReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class WatchdogCommand implements HypixelCommand {

	private final Logger log;

	public WatchdogCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performHypixelCommand(Member m, GuildMessageChannel channel, Message message) {
		HypixelAPI api = Klassenserver7bbot.getInstance().getHypixelAPI();

		try {
			PunishmentStatsReply reply = api.getPunishmentStats().get();
			channel.sendMessage(
					"Staff-Rolling-Daily: " + reply.getStaffRollingDaily() + "\nStaff-Total: " + reply.getStaffTotal()
							+ "\nWatchdog-Last-Minute: " + reply.getWatchdogLastMinute() + "\nWatchdog-Rolling-Daily: "
							+ reply.getWatchdogRollingDaily() + "\nWatchdog Total: " + reply.getWatchdogTotal())
					.queue();
		} catch (ExecutionException e) {
			System.err.println("Oh no, our API request failed!");
			e.getCause().printStackTrace();
		} catch (InterruptedException e) {

			System.err.println("Oh no, the player fetch thread was interrupted!");
			log.error(e.getMessage(), e);
		}
	}
}