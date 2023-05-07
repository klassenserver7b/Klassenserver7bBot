
package de.k7bot.manage;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.common.ClientInfo;
import de.k7bot.commands.common.GithubRepoCommand;
import de.k7bot.commands.common.HelpCommand;
import de.k7bot.commands.common.PingCommand;
import de.k7bot.commands.common.PrefixCommand;
import de.k7bot.commands.common.RestartCommand;
import de.k7bot.commands.common.ShutdownCommand;
import de.k7bot.commands.common.SystemchannelCommand;
import de.k7bot.commands.common.TeacherCommand;
import de.k7bot.commands.common.TestCommand;
import de.k7bot.commands.common.VTestCommand;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.hypixel.commands.SCtoHC;
import de.k7bot.moderation.commands.common.BanCommand;
import de.k7bot.moderation.commands.common.KickCommand;
import de.k7bot.moderation.commands.common.MemberLogsCommand;
import de.k7bot.moderation.commands.common.ModLogsCommand;
import de.k7bot.moderation.commands.common.StopTimeoutCommand;
import de.k7bot.moderation.commands.common.TimeoutCommand;
import de.k7bot.moderation.commands.common.WarnCommand;
import de.k7bot.music.commands.common.AddQueueTrackCommand;
import de.k7bot.music.commands.common.ClearQueueCommand;
import de.k7bot.music.commands.common.CurrentTrackInfoCommand;
import de.k7bot.music.commands.common.EqualizerCommand;
import de.k7bot.music.commands.common.LoopCommand;
import de.k7bot.music.commands.common.LyricsCommand;
import de.k7bot.music.commands.common.NightcoreCommand;
import de.k7bot.music.commands.common.OverallChartsCommand;
import de.k7bot.music.commands.common.PauseCommand;
import de.k7bot.music.commands.common.PlayCommand;
import de.k7bot.music.commands.common.PlayNextCommand;
import de.k7bot.music.commands.common.QueuelistCommand;
import de.k7bot.music.commands.common.ResumeCommand;
import de.k7bot.music.commands.common.SeekCommand;
import de.k7bot.music.commands.common.ShuffleCommand;
import de.k7bot.music.commands.common.SkipBackCommand;
import de.k7bot.music.commands.common.SkipCommand;
import de.k7bot.music.commands.common.SkipForwardCommand;
import de.k7bot.music.commands.common.StopCommand;
import de.k7bot.music.commands.common.UebersteuerungAdmin;
import de.k7bot.music.commands.common.UnLoopCommand;
import de.k7bot.music.commands.common.VolumeCommand;
import de.k7bot.util.commands.common.AddReactionCommand;
import de.k7bot.util.commands.common.ClearCommand;
import de.k7bot.util.commands.common.MessagetoEmbedCommand;
import de.k7bot.util.commands.common.ReactRolesCommand;
import de.k7bot.util.commands.common.StatsCategoryCommand;
import de.k7bot.util.customapis.DisabledAPI;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 *
 * @author Klassenserver7b
 *
 */
public class CommandManager {
	private final ArrayList<ServerCommand> commands;
	private final HashMap<String, ServerCommand> mappedCommands;

	private final Logger commandlog;

	/**
	 * 
	 */
	public CommandManager() {
		this.commands = new ArrayList<>();
		this.mappedCommands = new HashMap<>();
		this.commandlog = LoggerFactory.getLogger("Commandlog");

		// Allgemein
		this.commands.add(new HelpCommand());
		this.commands.add(new PrefixCommand());
		this.commands.add(new PingCommand());
		this.commands.add(new SystemchannelCommand());
		this.commands.add(new RestartCommand());
		this.commands.add(new ShutdownCommand());

		// Util Commands
		this.commands.add(new ClearCommand());
		this.commands.add(new ReactRolesCommand());
		this.commands.add(new AddReactionCommand());
		this.commands.add(new MessagetoEmbedCommand());
		this.commands.add(new ClientInfo());
		this.commands.add(new StatsCategoryCommand());

		// Moderation Commands
		this.commands.add(new WarnCommand());
		this.commands.add(new KickCommand());
		this.commands.add(new BanCommand());
		this.commands.add(new ModLogsCommand());
		this.commands.add(new MemberLogsCommand());
		this.commands.add(new TimeoutCommand());
		this.commands.add(new StopTimeoutCommand());

		// Musik Commands
		this.commands.add(new PlayCommand());
		this.commands.add(new StopCommand());
		this.commands.add(new PauseCommand());
		this.commands.add(new ResumeCommand());
		this.commands.add(new PlayNextCommand());
		this.commands.add(new AddQueueTrackCommand());
		this.commands.add(new SkipCommand());
		this.commands.add(new VolumeCommand());
		this.commands.add(new LoopCommand());
		this.commands.add(new UnLoopCommand());
		this.commands.add(new ShuffleCommand());
		this.commands.add(new QueuelistCommand());
		this.commands.add(new ClearQueueCommand());
		this.commands.add(new CurrentTrackInfoCommand());
		this.commands.add(new SeekCommand());
		this.commands.add(new SkipForwardCommand());
		this.commands.add(new SkipBackCommand());
		this.commands.add(new LyricsCommand());
		this.commands.add(new OverallChartsCommand());
		this.commands.add(new EqualizerCommand());
		this.commands.add(new NightcoreCommand());

		// Private
		this.commands.add(new UebersteuerungAdmin());
		this.commands.add(new TeacherCommand());

		if (Klassenserver7bbot.getInstance().getPropertiesManager().isApiEnabled("hypixel")) {
			this.commands.add(new SCtoHC());
		} else {
			this.commands.add(new DisabledAPI(new String[] { "hypixel" }));
		}

		if (Klassenserver7bbot.getInstance().getPropertiesManager().isApiEnabled("github")) {
			this.commands.add(new GithubRepoCommand());
		} else {
			this.commands.add(new DisabledAPI(new String[] { "repo" }));
		}

		if (Klassenserver7bbot.getInstance().isDevMode()) {
			this.commands.add(new TestCommand());
			this.commands.add(new VTestCommand());
		}

		commands.forEach(command -> {
			for (String s : command.getCommandStrings()) {
				mappedCommands.put(s, command);
			}
		});
	}

	/**
	 *
	 * @param command
	 * @param m
	 * @param channel
	 * @param message
	 * @return
	 */
	public int perform(String command, Member m, TextChannel channel, Message message) {
		ServerCommand cmd;
		if ((cmd = this.mappedCommands.get(command.toLowerCase())) != null) {

			if (!cmd.isEnabled()) {
				return 0;
			}

			message.delete().queue();

			commandlog.info(
					"see next lines:\n\nMember: " + m.getEffectiveName() + " | \nGuild: " + channel.getGuild().getName()
							+ " | \nChannel: " + channel.getName() + " | \nMessage: " + message.getContentRaw() + "\n");

			cmd.performCommand(m, channel, message);

			return 1;
		}

		return -1;
	}

	public boolean disableCommand(String command) {

		return disableCommand(mappedCommands.get(command));

	}

	public boolean disableCommand(ServerCommand command) {
		if (command == null) {
			return false;
		}

		if (!command.isEnabled()) {
			return false;
		}

		command.disableCommand();
		return true;
	}

	public boolean disableCommand(Class<?> command) {

		boolean removed = false;
		ArrayList<ServerCommand> rem = new ArrayList<>();

		for (ServerCommand serverCommand : commands) {
			if (serverCommand.getClass().isInstance(command)) {
				rem.add(serverCommand);
				removed = true;
			}
		}
		for (ServerCommand c : rem) {
			c.disableCommand();
		}

		return removed;
	}

	public boolean enableCommand(String command) {
		return enableCommand(mappedCommands.get(command));
	}

	public boolean enableCommand(ServerCommand command) {

		if (command == null) {
			return false;
		}

		if (command.isEnabled()) {
			return false;
		}

		command.enableCommand();
		return true;
	}

	public boolean enableCommand(Class<?> command) {

		boolean added = false;
		ArrayList<ServerCommand> add = new ArrayList<>();

		for (ServerCommand serverCommand : commands) {
			if (serverCommand.getClass().isInstance(command)) {
				add.add(serverCommand);
				added = true;
			}
		}
		for (ServerCommand c : add) {
			c.enableCommand();
		}

		return added;
	}

	public String getNearestCommand(String str) {

		LevenshteinDistance levdis = LevenshteinDistance.getDefaultInstance();
		String comm = "";
		int l = Integer.MAX_VALUE;

		for (String s : mappedCommands.keySet()) {

			Integer distance = levdis.apply(s, str);

			if (distance < l) {

				l = distance;
				comm = s;

			}

		}

		return comm;

	}

	public ArrayList<ServerCommand> getCommands() {
		return commands;
	}
}
