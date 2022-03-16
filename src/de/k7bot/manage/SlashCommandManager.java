
package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.slashcommands.ClearSlashCommand;
import de.k7bot.slashcommands.HelpSlashCommand;
import de.k7bot.slashcommands.PingSlashCommand;
import de.k7bot.slashcommands.ReactRolesSlashCommand;
import de.k7bot.slashcommands.Shutdownslashcommand;
import de.k7bot.slashcommands.ToEmbedSlashCommand;

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
	public Logger commandlog;

	public SlashCommandManager() {
		this.commands = new ConcurrentHashMap<>();

		this.commands.put("help", new HelpSlashCommand());
		this.commands.put("clear", new ClearSlashCommand());
		this.commands.put("shutdown", new Shutdownslashcommand());
		this.commands.put("ping", new PingSlashCommand());
		this.commands.put("toembed", new ToEmbedSlashCommand());
		this.commands.put("reactrole", new ReactRolesSlashCommand());
		this.commandlog = LoggerFactory.getLogger("Commandlog");

		Klassenserver7bbot.INSTANCE.shardMan.getShards().forEach(shard -> {
			CommandListUpdateAction commup = shard.updateCommands();

			commup.addCommands(Commands.slash("help", "Gibt dir die Hilfe-Liste aus."));

			commup.addCommands(Commands.slash("shutdown", "fährt den Bot herunter"));

			commup.addCommands(Commands.slash("ping", "Zeigt den aktuellen Ping des Bots"));

			commup.addCommands(Commands.slash("clear", "Löscht die Ausgewählte Anzahl an Nachrichten.")
					.addOptions(new OptionData(OptionType.INTEGER, "amount",
							"Wie viele Nachrichten sollen gelöscht werden? (Standart = 1 Nachricht)")));

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
							"Die Rolle die zugewiesen werden soll – stelle sicher: Rechte und Rolle des Bots > Rechte der Rolle",
							true));

			commup.complete();
		});
	}

	public boolean perform(SlashCommandInteraction event) {
		SlashCommand cmd;
		if ((cmd = this.commands.get(event.getName().toLowerCase())) != null) {

			cmd.performSlashCommand(event);

			commandlog.info("SlashCommand - see next lines:\n\nMember: " + event.getMember().getEffectiveName()
					+ " | \nGuild: " + event.getGuild().getName() + " | \nChannel: " + event.getChannel().getName()
					+ " | \nMessage: " + event.getName() + "\n");

			return true;
		}
		return false;
	}
}
