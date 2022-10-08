package de.k7bot.commands;

import org.slf4j.Logger;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.SQLManager;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class RestartCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.getIdLong() == Klassenserver7bbot.INSTANCE.getOwnerId()) {
			Logger log = Klassenserver7bbot.INSTANCE.getMainLogger();
			String[] args = message.getContentDisplay().split(" ");

			if (args.length > 1) {

				try {

					Klassenserver7bbot.INSTANCE.getPlayerUtil().stopAllTracks();

					Klassenserver7bbot.INSTANCE.stopLoop();
					SQLManager.onCreate();

					Klassenserver7bbot.INSTANCE.getShardManager().restart(Integer.parseInt(args[1]));
					log.info("Restarting Shard " + args[1]);

					Klassenserver7bbot.INSTANCE.runLoop();

				} catch (NumberFormatException e) {
					SyntaxError.oncmdSyntaxError(channel, "restart <shardId>", m);
				}

			} else {
				Klassenserver7bbot.INSTANCE.getShardManager().restart();
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
	public String getcategory() {
		String category = "Tools";
		return category;
	}

}
