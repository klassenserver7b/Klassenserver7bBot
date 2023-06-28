package de.k7bot.manage;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.slash.CheckRoomSlashCommand;
import de.k7bot.commands.slash.HA3MembersCommand;
import de.k7bot.commands.slash.HelpSlashCommand;
import de.k7bot.commands.slash.PingSlashCommand;
import de.k7bot.commands.slash.SearchForRoomSlashCommand;
import de.k7bot.commands.slash.Shutdownslashcommand;
import de.k7bot.commands.slash.StableDiffusionCommand;
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
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommandManager {

	public ConcurrentHashMap<String, TopLevelSlashCommand> commands;
	public final Logger commandlog = LoggerFactory.getLogger("Commandlog");

	public SlashCommandManager() {

		this.commands = new ConcurrentHashMap<>();

		List<TopLevelSlashCommand> registerschedule = new ArrayList<>();

		registerschedule.add(new HelpSlashCommand());
		registerschedule.add(new ClearSlashCommand());
		registerschedule.add(new Shutdownslashcommand());
		registerschedule.add(new PingSlashCommand());
		registerschedule.add(new ToEmbedSlashCommand());
		registerschedule.add(new ReactRolesSlashCommand());
		registerschedule.add(new PlaySlashCommandSplitter());
		registerschedule.add(new ChartsSlashCommand());
		registerschedule.add(new SubscribeSlashCommand());
		registerschedule.add(new UnSubscribeSlashCommand());
		registerschedule.add(new EqualizerSlashCommand());
		registerschedule.add(new WhitelistSlashCommand());
		registerschedule.add(new HA3MembersCommand());
		registerschedule.add(new VotingCommand());
		registerschedule.add(new SpeedChangeCommand());
		registerschedule.add(new StableDiffusionCommand());
		registerschedule.add(new CheckRoomSlashCommand());
		registerschedule.add(new SearchForRoomSlashCommand());

		Klassenserver7bbot.getInstance().getShardManager().getShards().forEach(shard -> {
			CommandListUpdateAction commup = shard.updateCommands();

			for (TopLevelSlashCommand command : registerschedule) {

				SlashCommandData cdata = command.getCommandData();
				this.commands.put(cdata.getName(), command);
				commup.addCommands(cdata);
			}

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
					"INSERT INTO slashcommandlog (command, guildId, userId, timestamp, commandstring) VALUES (?, ?, ?, ?, ?)",
					event.getName(), ((event.getGuild() != null) ? event.getGuild().getIdLong() : 0),
					event.getUser().getIdLong(),
					event.getTimeCreated().format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")),
					event.getCommandString());

			cmd.performSlashCommand(event);

			return true;
		}
		return false;
	}
}
