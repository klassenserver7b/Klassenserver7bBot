
package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.commands.common.*;
import de.k7bot.moderation.commands.common.*;
import de.k7bot.music.commands.common.*;
import de.k7bot.util.commands.common.*;
import de.k7bot.hypixel.commands.SCtoHC;
import de.k7bot.util.customapis.DisabledAPI;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * 
 * @author Felix
 *
 */
public class CommandManager {
	public LinkedHashMap<String, ServerCommand> commands;
	public Logger commandlog;

	/**
	 * 
	 * @param hypenable <br>
	 *                  Should the HypixelAPI be enabled
	 * @param gitenable <br>
	 *                  Should the GitHubAPI be enabled
	 */
	public CommandManager() {
		this.commands = new LinkedHashMap<>();
		this.commandlog = LoggerFactory.getLogger("Commandlog");

		// Allgemein
		this.commands.put("help", new HelpCommand());
		this.commands.put("prefix", new PrefixCommand());
		this.commands.put("ping", new PingCommand());
		this.commands.put("syschannel", new SystemchannelCommand());
		this.commands.put("restart", new RestartCommand());
		this.commands.put("shutdown", new ShutdownCommand());

		// Util Commands
		this.commands.put("clear", new ClearCommand());
		this.commands.put("reactrole", new ReactRolesCommand());
		this.commands.put("createrole", new RoleCreation());
		this.commands.put("react", new AddReactionCommand());
		this.commands.put("toembed", new MessagetoEmbedCommand());
		this.commands.put("memberinfo", new ClientInfo());
		this.commands.put("onlinedevices", new MemberdevicesCommand());
		this.commands.put("everyone", new EveryoneCommand());
		this.commands.put("statscategory", new StatsCategoryCommand());

		// Moderation Commands
		this.commands.put("warn", new WarnCommand());
		this.commands.put("kick", new KickCommand());
		this.commands.put("ban", new BanCommand());
		this.commands.put("modlogs", new ModLogsCommand());
		this.commands.put("memberlogs", new MemberLogsCommand());
		this.commands.put("timeout", new TimeoutCommand());
		this.commands.put("stoptimeout", new StopTimeoutCommand());

		// Musik Commands
		this.commands.put("play", new PlayCommand());
		this.commands.put("p", new PlayCommand());
		this.commands.put("stop", new StopCommand());
		this.commands.put("pause", new PauseCommand());
		this.commands.put("resume", new ResumeCommand());
		this.commands.put("playnext", new PlayNextCommand());
		this.commands.put("pn", new PlayNextCommand());
		this.commands.put("addtoqueue", new AddQueueTrackCommand());
		this.commands.put("skip", new SkipCommand());
		this.commands.put("volume", new VolumeCommand());
		this.commands.put("loop", new LoopCommand());
		this.commands.put("unloop", new UnLoopCommand());
		this.commands.put("shuffle", new ShuffleCommand());
		this.commands.put("random", new ShuffleCommand());
		this.commands.put("queuelist", new QueuelistCommand());
		this.commands.put("clearqueue", new ClearQueueCommand());
		this.commands.put("nowplaying", new TrackInfoCommand());
		this.commands.put("np", new TrackInfoCommand());
		this.commands.put("seek", new SeekCommand());
		this.commands.put("forward", new SkipForwardCommand());
		this.commands.put("back", new SkipBackCommand());
		this.commands.put("lyrics", new LyricsCommand());
		this.commands.put("charts", new OverallChartsCommand());

		// Private
		this.commands.put("uvolume", new UebersteuerungAdmin());
		this.commands.put("teacher", new TeacherCommand());
		this.commands.put("diload", new DanceInterpreterJsonGenerateCommand());
		this.commands.put("eq", new EqualizerCommand());

		if (Klassenserver7bbot.getInstance().getPropertiesManager().isApiEnabled("hypixel")) {
			this.commands.put("hypixel", new SCtoHC());
		} else {
			this.commands.put("hypixel", new DisabledAPI());
		}
		
		if (Klassenserver7bbot.getInstance().getPropertiesManager().isApiEnabled("github")) {
			this.commands.put("repo", new GithubRepoCommand());
		} else {
			this.commands.put("repo", new DisabledAPI());
		}

		if (Klassenserver7bbot.getInstance().isDevMode()) {
			this.commands.put("test", new TestCommand());
			this.commands.put("vtest", new VTestCommand());
		}
	}

	/**
	 * 
	 * @param command
	 * @param m
	 * @param channel
	 * @param message
	 * @return
	 */
	public boolean perform(String command, Member m, TextChannel channel, Message message) {
		ServerCommand cmd;
		if ((cmd = this.commands.get(command.toLowerCase())) != null) {
			message.delete().queue();
			commandlog.info(
					"see next lines:\n\nMember: " + m.getEffectiveName() + " | \nGuild: " + channel.getGuild().getName()
							+ " | \nChannel: " + channel.getName() + " | \nMessage: " + message.getContentRaw() + "\n");

			cmd.performCommand(m, channel, message);

			return true;
		}
		return false;
	}
}
