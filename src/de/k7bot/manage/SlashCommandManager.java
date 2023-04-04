package de.k7bot.manage;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.slash.HA3MembersCommand;
import de.k7bot.commands.slash.HelpSlashCommand;
import de.k7bot.commands.slash.PingSlashCommand;
import de.k7bot.commands.slash.Shutdownslashcommand;
import de.k7bot.commands.slash.VotingCommand;
import de.k7bot.commands.slash.WhitelistSlashCommand;
import de.k7bot.commands.types.TopLevelSlashCommand;
import de.k7bot.music.commands.slash.ChartsSlashCommand;
import de.k7bot.music.commands.slash.EqualizerSlashCommand;
import de.k7bot.music.commands.slash.PlaySlashCommandSplitter;
import de.k7bot.music.commands.slash.SpeedChangeCommand;
import de.k7bot.sql.LiteSQL;
import de.k7bot.subscriptions.commands.SubscribeSlashCommand;
import de.k7bot.subscriptions.commands.UnSubscribeSlashCommand;
import de.k7bot.util.commands.slash.ClearSlashCommand;
import de.k7bot.util.commands.slash.ReactRolesSlashCommand;
import de.k7bot.util.commands.slash.ToEmbedSlashCommand;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommandManager {

	public ConcurrentHashMap<String, TopLevelSlashCommand> commands;
	public final Logger commandlog = LoggerFactory.getLogger("Commandlog");

	public SlashCommandManager() {

		this.commands = new ConcurrentHashMap<>();

		this.commands.put("help", new HelpSlashCommand());
		this.commands.put("clear", new ClearSlashCommand());
		this.commands.put("shutdown", new Shutdownslashcommand());
		this.commands.put("ping", new PingSlashCommand());
		this.commands.put("toembed", new ToEmbedSlashCommand());
		this.commands.put("reactrole", new ReactRolesSlashCommand());
		this.commands.put("play", new PlaySlashCommandSplitter());
		this.commands.put("charts", new ChartsSlashCommand());
		this.commands.put("subscribe", new SubscribeSlashCommand());
		this.commands.put("unsubscribe", new UnSubscribeSlashCommand());
		this.commands.put("equalizer", new EqualizerSlashCommand());
		this.commands.put("whitelistadd", new WhitelistSlashCommand());
		this.commands.put("ha3members", new HA3MembersCommand());
		this.commands.put("voting", new VotingCommand());
		this.commands.put("speedchange", new SpeedChangeCommand());

		Klassenserver7bbot.getInstance().getShardManager().getShards().forEach(shard -> {
			CommandListUpdateAction commup = shard.updateCommands();

			commands.values().forEach(command -> {

				commup.addCommands(command.getCommandData());

			});

			commup.complete();

		});
	}

	public boolean perform(SlashCommandInteraction event) {
		TopLevelSlashCommand cmd;
		if ((cmd = this.commands.get(event.getName().toLowerCase())) != null) {

			String guild = "PRIVATE";
			if (event.getGuild() != null) {
				guild = event.getGuild().getName();
			}

			commandlog.info("SlashCommand - see next lines:\n\nUser: " + event.getUser().getName() + " | \nGuild: "
					+ guild + " | \nChannel: " + event.getChannel().getName() + " | \nMessage: "
					+ event.getCommandString() + "\n");

			LiteSQL.onUpdate(
					"INSERT INTO slashcommandlog (command, guildId, timestamp, commandstring) VALUES (?, ?, ?, ?)",
					event.getName(), ((event.getGuild() != null) ? event.getGuild().getIdLong() : 0),
					event.getTimeCreated().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")),
					event.getCommandString());

			cmd.performSlashCommand(event);

			return true;
		}
		return false;
	}
}
