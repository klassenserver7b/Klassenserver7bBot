package de.k7bot.commands;

import org.slf4j.Logger;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.manage.PermissionError;
import de.k7bot.manage.SyntaxError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RestartCommand implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		if(m.hasPermission(Permission.ADMINISTRATOR)) {
			Logger log = Klassenserver7bbot.INSTANCE.logger;
			String[] args = message.getContentDisplay().split(" ");
			
			if(args.length>1) {
				
				try {
				Klassenserver7bbot.INSTANCE.shardMan.restart(Integer.parseInt(args[1]));
				log.info("Restarting Shard "+ args[1]);
				}catch(NumberFormatException e) {
					SyntaxError.oncmdSyntaxError(channel, "restart <shardId>", m);
				}
				
			}else {			
			Klassenserver7bbot.INSTANCE.shardMan.restart();
			log.info("Restarting all Shards");
			}
			
		}else {
			PermissionError.onPermissionError(m, channel);
		}
		
	}

}
