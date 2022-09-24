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

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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
		this.commands.put("whitelistadd", new WhitelistSlashCommand());
		this.commands.put("ha3members", new HA3MembersCommand());

		Klassenserver7bbot.INSTANCE.shardMan.getShards().forEach(shard -> {
			CommandListUpdateAction commup = shard.updateCommands();

			commup.addCommands(Commands.slash("help", "Gibt dir die Hilfe-Liste aus."));

			commup.addCommands(Commands.slash("shutdown", "führt den Bot herunter"));

			commup.addCommands(Commands.slash("ping", "Zeigt den aktuellen Ping des Bots"));

			commup.addCommands(
					Commands.slash("clear", "Lüscht die Ausgewühlte Anzahl an Nachrichten.").addOptions(new OptionData(
							OptionType.INTEGER, "amount", "Wie viele Nachrichten sollen gelüscht werden?", true)));

			commup.addCommands(Commands.slash("toembed", "Erstellt einen Embed mit den gegebenen Parametern")
					.addOption(OptionType.STRING, "title", "Welchen Titel soll der Embed haben?", true)
					.addOption(OptionType.STRING, "description", "Welchen Inhalt soll der Embed haben?", true)
					.addOption(OptionType.STRING, "color", "Die Farbe des Embeds als hexadezimale Zahl"));

			commup.addCommands(Commands
					.slash("reactrole", "Erstellt eine Reactionrole mit den übermittelten Parametern")
					.addOption(OptionType.CHANNEL, "channel", "Der Channel in dem die Message ist", true)
					.addOption(OptionType.STRING, "messageid",
							"Die MessageId der Message an die die Reaction angefügt werden soll", true)
					.addOption(OptionType.STRING, "emoteid-oder-utfemote",
							"Die EmoteId des Emotes bzw. das UTF8 Emoji auf das die Rolle registriert werden soll",
							true)
					.addOption(OptionType.ROLE, "role",
							"Die Rolle die zugewiesen werden soll -  stelle sicher: Rechte und Rolle des Bots > Rechte der Rolle",
							true));

			commup.addCommands(Commands.slash("charts", "Liefert die Bot-Music Charts für die gewühlten Parameter")
					.addOption(OptionType.BOOLEAN, "guild",
							"true wenn nur die charts für die aktuelle guild angefordert werden sollen")
					.addOption(OptionType.INTEGER, "time",
							"REQUIRES TIMEUNIT! - Wie viele TimeUnits soll der Bot zur Chartbestimmung berücksichtigen")
					.addOption(OptionType.STRING, "timeunit", "Erlaubte TimeUnits: \"DAYS\", \"MONTHS\", \"YEARS\"",
							false, true));
			commup.addCommands(Commands.slash("whitelistadd", "Fragt die Hinzufügung zur Whitelist an.")
					.addOption(OptionType.STRING, "ingamename", "Dein Spielername in Minecraft", true)
					.addOption(OptionType.STRING, "realname",
							"Der Name mit dem du im Talk etc. angesprochen werden willst.", true));
			commup.addCommands(Commands.slash("ha3members", "Zeigt die Mitglieder von HA3 mit Namen an"));
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
