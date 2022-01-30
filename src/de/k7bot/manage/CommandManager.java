
package de.k7bot.manage;

import de.k7bot.commands.BanCommand;
import de.k7bot.commands.ClearCommand;
import de.k7bot.commands.ClientInfo;
import de.k7bot.commands.EveryoneCommand;
import de.k7bot.commands.MemberLogsCommand;
import de.k7bot.commands.MessagetoEmbedCommand;
import de.k7bot.commands.ModLogsCommand;
import de.k7bot.commands.MuteCommand;
import de.k7bot.commands.PingCommand;
import de.k7bot.commands.PrefixCommand;
import de.k7bot.commands.ReactRolesCommand;
import de.k7bot.commands.RestartCommand;
import de.k7bot.commands.RoleCreation;
import de.k7bot.commands.ShutdownCommand;
import de.k7bot.commands.StatsChannelCommand;
import de.k7bot.commands.UnmuteCommand;
import de.k7bot.commands.WarnCommand;
import de.k7bot.commands.addReactionCommand;
import de.k7bot.commands.helpCommand;
import de.k7bot.commands.kickCommand;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.hypixel.commands.SCtoHC;
import de.k7bot.music.commands.ClearQueueCommand;
import de.k7bot.music.commands.LyricsCommand;
import de.k7bot.music.commands.Lyricsoldcommand;
import de.k7bot.music.commands.NoncoreCommand;
import de.k7bot.music.commands.PauseCommand;
import de.k7bot.music.commands.PlayCommand;
import de.k7bot.music.commands.ResumeCommand;
import de.k7bot.music.commands.ShuffleCommand;
import de.k7bot.music.commands.SkipCommand;
import de.k7bot.music.commands.StopCommand;
import de.k7bot.music.commands.TrackInfoCommand;
import de.k7bot.music.commands.UebersteuerungAdmin;
import de.k7bot.music.commands.VolumeCommand;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class CommandManager {
	public ConcurrentHashMap<String, ServerCommand> commands;
	public ConcurrentHashMap<String, String> help;
	public ConcurrentHashMap<String, String> category;
	public Logger commandlog;

	public CommandManager() {
		this.commands = new ConcurrentHashMap<>();
		this.help = new ConcurrentHashMap<>();
		this.category = new ConcurrentHashMap<>();
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
		this.commands.put("mute", new MuteCommand());
		this.commands.put("unmute", new UnmuteCommand());
		this.commands.put("play", new PlayCommand());
		this.commands.put("p", new PlayCommand());
		this.commands.put("stop", new StopCommand());
		this.commands.put("pause", new PauseCommand());
		this.commands.put("resume", new ResumeCommand());
		this.commands.put("volume", new VolumeCommand());
		this.commands.put("skip", new SkipCommand());
		this.commands.put("shuffle", new ShuffleCommand());
		this.commands.put("nowplaying", new TrackInfoCommand());
		this.commands.put("np", new TrackInfoCommand());
		this.commands.put("clearqueue", new ClearQueueCommand());
		this.commands.put("statscategory", new StatsChannelCommand());
		this.commands.put("hypixel", new SCtoHC());
		this.commands.put("shutdown", new ShutdownCommand());
		this.commands.put("everyone", new EveryoneCommand());
		this.commands.put("noncore", new NoncoreCommand());
		this.commands.put("uvolume", new UebersteuerungAdmin());
		this.commands.put("restart", new RestartCommand());
		this.commands.put("lyrics", new LyricsCommand());
		this.commands.put("lyricsold", new Lyricsoldcommand());

		this.help.put("help", "Zeigt diese Hilfe.");
		this.help.put("clear", "Löscht die angegebene Anzahl an Nachrichten.\n - z.B. [prefix]clear 50");
		this.help.put("memberinfo", "Zeigt die Informationen zum angegebenen User.\n - z.B. [prefix]memberinfo @K7Bot");
		this.help.put("prefix", "Ändert das Prefix des Bots auf diesem Server.\n - z.B. [prefix][new prefix] '-'");
		this.help.put("kick",
				"Kickt den ausgewählten Nutzer vom Server und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. kick @K7Bot [reason]");
		this.help.put("ban",
				"Bannt den ausgewählten Nutzer vom Server und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder bannen' ausgeführt werden!\n - z.B. [prefix]ban @K7Bot [reason]");
		this.help.put("warn",
				"Verwarnt den angegebenen Nutzer und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]warn @K7Bot [reason]");
		this.help.put("toembed", "Sendet die eingegebene Nachricht als Embed.\n - z.B. [prefix]toembed [nachricht]");
		this.help.put("react",
				"Reagiert als Bot auf die ausgewählte Nachricht.\n - z.B. [prefix]react #textchannel [messageid] :emote: :emote: :emote: usw.");
		this.help.put("reactrole",
				"Erstellt eine Reactionrole für die ausgewählte Nachricht mit dem ausgewählten Emote.\n - z.B. [prefix]reactrole #channel [messageId] :emote: @role");
		this.help.put("ping", "Gibt den aktuellen pig des Bots zurück.");
		this.help.put("createrole",
				"Erstellt eine Rolle mit dem gewählten Namen und ggf. der gewählten Farbe.\n - kann nur von Mitgliedern mit der Berechtigung 'Manage-Roles' ausgeführt werden!\n - z.B. [prefix]createrole [test] <#ffffff>");
		this.help.put("modlogs",
				"Zeigt die Logs zu einem Moderator.\n - kann nur von Mitgliedern mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]modlogs @moderator");
		this.help.put("memberlogs",
				"Zeigt die Logs zu einem Mitglied.\n - kann nur von Mitgliedern mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]modlogs @member");
		this.help.put("mute",
				"Mutet den angegeben Nutzer für den Ausgewählten Grund.\n - kann nur von Mitgliedern mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]mute @member [reason]");
		this.help.put("unmute",
				"Entmuted den angegebenen Nutzer.\n - kann nur von Mitgliedern mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]unmute @member");
		this.help.put("play",
				"Spielt den/die ausgewählte/-n Track / Livestream / Playlist.\n - kann nur ausgeführt werden wenn sich der Nutzer in einem Voice Channel befindet!\n - z.B. [prefix]play [url / YouTube Suchbegriff]");
		this.help.put("p", "Alias für play.");
		this.help.put("stop", "Stopt den aktuellen Track und der Bot verlässt den VoiceChannel.");
		this.help.put("pause", "Pausiert den aktuellen Track.");
		this.help.put("resume", "Setzt den aktuellen Track fort.");
		this.help.put("volume",
				"Legt das Volume für den Bot auf diesem Server fest.\n - z.B. [prefix]volume [Zahl von 1 bis 100]");
		this.help.put("skip", "Überspringt den aktuellen Song.");
		this.help.put("shuffle", "Spielt die aktuelle Playlist random.");
		this.help.put("nowplaying", "Zeigt die Info zum aktuellen Track.");
		this.help.put("np", "Alias für nowplaying.");
		this.help.put("clearqueue", "Löscht die aktuelle Queuelist.");
		this.help.put("statscategory",
				"Legt eine Kategorie mit dem Bot-Status (Online/Offline) an.\n - kann nur von Mitgliedern mit der Berechtigung 'Administrator' ausgeührt werden!");
		this.help.put("shutdown",
				"Fährt den Bot herunter.\n - kann nur von Mitgliedern mit der Berechtigung 'Administrator' ausgeührt werden!");
		this.help.put("hypixel", "Siehe [prefix]hypixel help");
		this.help.put("everyone", "Sendet die aktuelle Nachricht als @everyone.\n - z.B. [prefix]everyone [Nachricht]");
		this.help.put("lyrics", "Sendet die lyrics des aktuell gespielten songs in den aktuellen channel.");
		this.help.put("lyricsold", "unterstützt nur Genius als Lyrics-Provider (findet weniger als der main-command), besitzt aber ein Embed-icon und bessere Textgliederung");

		this.category.put("help", "Allgemein");
		this.category.put("clear", "Tools");
		this.category.put("memberinfo", "Moderation");
		this.category.put("prefix", "Tools");
		this.category.put("kick", "Moderation");
		this.category.put("ban", "Moderation");
		this.category.put("warn", "Moderation");
		this.category.put("toembed", "Tools");
		this.category.put("react", "Tools");
		this.category.put("reactrole", "Tools");
		this.category.put("ping", "Allgemein");
		this.category.put("createrole", "Tools");
		this.category.put("modlogs", "Moderation");
		this.category.put("memberlogs", "Moderation");
		this.category.put("mute", "Moderation");
		this.category.put("unmute", "Moderation");
		this.category.put("play", "Musik");
		this.category.put("p", "Musik");
		this.category.put("stop", "Musik");
		this.category.put("pause", "Musik");
		this.category.put("resume", "Musik");
		this.category.put("volume", "Musik");
		this.category.put("skip", "Musik");
		this.category.put("shuffle", "Musik");
		this.category.put("nowplaying", "Musik");
		this.category.put("np", "Musik");
		this.category.put("clearqueue", "Musik");
		this.category.put("statscategory", "Tools");
		this.category.put("hypixel", "Games");
		this.category.put("shutdown", "Allgemein");
		this.category.put("everyone", "Allgemein");
		this.category.put("lyrics", "Musik");
		this.category.put("lyricsold", "Musik");
	}

	public boolean perform(String command, Member m, TextChannel channel, Message message) {
		ServerCommand cmd;
		if ((cmd = this.commands.get(command.toLowerCase())) != null) {

			cmd.performCommand(m, channel, message);

			commandlog.info(
					"see next lines:\n\nMember: " + m.getEffectiveName() + " | \nGuild: " + channel.getGuild().getName()
							+ " | \nChannel: " + channel.getName() + " | \nMessage: " + message.getContentRaw() + "\n");
			return true;
		}
		return false;
	}
}
