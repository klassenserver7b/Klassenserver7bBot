package de.k7bot.commands.common;

import org.slf4j.Logger;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.sql.SQLManager;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.PermissionError;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RestartCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.getIdLong() == Klassenserver7bbot.getInstance().getOwnerId()) {
			Logger log = Klassenserver7bbot.getInstance().getMainLogger();
			String[] args = message.getContentDisplay().split(" ");

			if (args.length > 1) {

				try {

					Klassenserver7bbot.getInstance().getPlayerUtil().stopAllTracks();

					Klassenserver7bbot.getInstance().stopLoop();
					SQLManager.onCreate();

					Klassenserver7bbot.getInstance().getShardManager().restart(Integer.parseInt(args[1]));
					log.info("Restarting Shard " + args[1]);

					Klassenserver7bbot.getInstance().runLoop();

				} catch (NumberFormatException e) {
					SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "restart <shardId>", m);
				}

			} else {
				Klassenserver7bbot.getInstance().getShardManager().restart();
				log.info("Restarting all Shards");
			}

		} else {
			PermissionError.onPermissionError(m, channel);
		}

	}

	@Override
	public String gethelp() {
		String help = "Startet den Bot neu.\n - kann nur vom Bot Owner ausgeführt werden!";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.TOOLS;
	}

}
