
package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.ClientInfo;
import de.k7bot.commands.PingCommand;
import de.k7bot.commands.PrefixCommand;
import de.k7bot.commands.RestartCommand;
import de.k7bot.commands.ShutdownCommand;
import de.k7bot.commands.SystemchannelCommand;
import de.k7bot.commands.TestCommand;
import de.k7bot.commands.TeacherCommand;
import de.k7bot.commands.helpCommand;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.hypixel.commands.SCtoHC;
import de.k7bot.moderation.commands.BanCommand;
import de.k7bot.moderation.commands.MemberLogsCommand;
import de.k7bot.moderation.commands.MemberdevicesCommand;
import de.k7bot.moderation.commands.ModLogsCommand;
import de.k7bot.moderation.commands.StopTimeoutCommand;
import de.k7bot.moderation.commands.TimeoutCommand;
import de.k7bot.moderation.commands.WarnCommand;
import de.k7bot.moderation.commands.kickCommand;
import de.k7bot.music.commands.AddQueueTrackCommand;
import de.k7bot.music.commands.ClearQueueCommand;
import de.k7bot.music.commands.LoopCommand;
import de.k7bot.music.commands.LyricsCommand;
import de.k7bot.music.commands.Lyricsoldcommand;
import de.k7bot.music.commands.NoncoreCommand;
import de.k7bot.music.commands.OverallChartsCommand;
import de.k7bot.music.commands.PauseCommand;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.music.commands.QueuelistCommand;
import de.k7bot.music.commands.ResumeCommand;
import de.k7bot.music.commands.ShuffleCommand;
import de.k7bot.music.commands.SkipCommand;
import de.k7bot.music.commands.StopCommand;
import de.k7bot.music.commands.TrackInfoCommand;
import de.k7bot.music.commands.UebersteuerungAdmin;
import de.k7bot.music.commands.UnLoopCommand;
import de.k7bot.music.commands.VolumeCommand;
import de.k7bot.util.DisabledAPI;
import de.k7bot.util.commands.ClearCommand;
import de.k7bot.util.commands.EveryoneCommand;
import de.k7bot.util.commands.MessagetoEmbedCommand;
import de.k7bot.util.commands.ReactRolesCommand;
import de.k7bot.util.commands.RoleCreation;
import de.k7bot.util.commands.StatsCategoryCommand;
import de.k7bot.util.commands.addReactionCommand;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandManager {
	public ConcurrentHashMap<String, ServerCommand> commands;
	public Logger commandlog;

	/**
	 * 
	 * @param hypenable <br>
	 *                  Should the HypixelAPI be enabled
	 * @param gitenable <br>
	 *                  Should the GitHubAPI be enabled
	 */
	public CommandManager(Boolean hypenable, Boolean gitenable) {
		this.commands = new ConcurrentHashMap<>();
		this.commandlog = LoggerFactory.getLogger("Commandlog");

		this.commands.put("help", new helpCommand());
		this.commands.put("clear", new ClearCommand());
		this.commands.put("memberinfo", new ClientInfo());
		this.commands.put("prefix", new PrefixCommand());
		this.commands.put("kick", new kickCommand());
		this.commands.put("ban", new BanCommand());
		this.commands.put("warn", new WarnCommand());
		this.commands.put("toembed", new MessagetoEmbedCommand());
		this.commands.put("react", new addReactionCommand());
		this.commands.put("reactrole", new ReactRolesCommand());
		this.commands.put("ping", new PingCommand());
		this.commands.put("createrole", new RoleCreation());
		this.commands.put("modlogs", new ModLogsCommand());
		this.commands.put("memberlogs", new MemberLogsCommand());
		this.commands.put("timeout", new TimeoutCommand());
		this.commands.put("stoptimeout", new StopTimeoutCommand());
		this.commands.put("play", new PlayCommand());
		this.commands.put("p", new PlayCommand());
		this.commands.put("stop", new StopCommand());
		this.commands.put("pause", new PauseCommand());
		this.commands.put("resume", new ResumeCommand());
		this.commands.put("volume", new VolumeCommand());
		this.commands.put("skip", new SkipCommand());
		this.commands.put("shuffle", new ShuffleCommand());
		this.commands.put("random", new ShuffleCommand());
		this.commands.put("nowplaying", new TrackInfoCommand());
		this.commands.put("np", new TrackInfoCommand());
		this.commands.put("clearqueue", new ClearQueueCommand());
		this.commands.put("statscategory", new StatsCategoryCommand());
		this.commands.put("shutdown", new ShutdownCommand());
		this.commands.put("everyone", new EveryoneCommand());
		this.commands.put("uvolume", new UebersteuerungAdmin());
		this.commands.put("restart", new RestartCommand());
		this.commands.put("lyrics", new LyricsCommand());
		this.commands.put("lyricsold", new Lyricsoldcommand());
		this.commands.put("queuelist", new QueuelistCommand());
		this.commands.put("teacher", new TeacherCommand());
		this.commands.put("loop", new LoopCommand());
		this.commands.put("unloop", new UnLoopCommand());
		this.commands.put("syschannel", new SystemchannelCommand());
		this.commands.put("onlinedevices", new MemberdevicesCommand());
		this.commands.put("charts", new OverallChartsCommand());
		this.commands.put("addtoqueue", new AddQueueTrackCommand());
		if (hypenable) {
			this.commands.put("hypixel", new SCtoHC());
		} else {
			this.commands.put("hypixel", new DisabledAPI());
		}

		if (Klassenserver7bbot.INSTANCE.indev) {
			this.commands.put("noncore", new NoncoreCommand());
			this.commands.put("test", new TestCommand());
		}
	}

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
