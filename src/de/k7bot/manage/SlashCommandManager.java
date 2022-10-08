package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.slashcommands.ChartsSlashCommand;
import de.k7bot.slashcommands.ClearSlashCommand;
import de.k7bot.slashcommands.HA3MembersCommand;
import de.k7bot.slashcommands.HelpSlashCommand;
import de.k7bot.slashcommands.PingSlashCommand;
import de.k7bot.slashcommands.ReactRolesSlashCommand;
import de.k7bot.slashcommands.Shutdownslashcommand;
import de.k7bot.slashcommands.ToEmbedSlashCommand;
import de.k7bot.slashcommands.WhitelistSlashCommand;
import de.k7bot.subscriptions.commands.SubscribeSlashCommand;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommandManager {

	public ConcurrentHashMap<String, SlashCommand> commands;
	public final Logger commandlog = LoggerFactory.getLogger("Commandlog");

	public SlashCommandManager() {

		this.commands = new ConcurrentHashMap<>();

		this.commands.put("help", new HelpSlashCommand());
		this.commands.put("clear", new ClearSlashCommand());
		this.commands.put("shutdown", new Shutdownslashcommand());
		this.commands.put("ping", new PingSlashCommand());
		this.commands.put("toembed", new ToEmbedSlashCommand());
		this.commands.put("reactrole", new ReactRolesSlashCommand());
		this.commands.put("charts", new ChartsSlashCommand());
		this.commands.put("subscribe", new SubscribeSlashCommand());
		this.commands.put("whitelistadd", new WhitelistSlashCommand());
		this.commands.put("ha3members", new HA3MembersCommand());

		Klassenserver7bbot.INSTANCE.getShardManager().getShards().forEach(shard -> {
			CommandListUpdateAction commup = shard.updateCommands();
			
			commands.values().forEach(command->{
				
				commup.addCommands(command.getCommandData());
				
			});
			
			commup.complete();
			
		});
	}

	public boolean perform(SlashCommandInteraction event) {
		SlashCommand cmd;
		if ((cmd = this.commands.get(event.getName().toLowerCase())) != null) {

			commandlog.info("SlashCommand - see next lines:\n\nMember: " + event.getMember().getEffectiveName()
					+ " | \nGuild: " + event.getGuild().getName() + " | \nChannel: " + event.getChannel().getName()
					+ " | \nMessage: " + event.getName() + "\n");

			cmd.performSlashCommand(event);

			return true;
		}
		return false;
	}
}
