
package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.slashcommands.ClearSlashCommand;
import de.k7bot.slashcommands.HelpSlashCommand;
import de.k7bot.slashcommands.Shutdownslashcommand;

import java.util.concurrent.ConcurrentHashMap;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

public class SlashCommandManager {
	public ConcurrentHashMap<String, SlashCommand> commands;

	public SlashCommandManager() {
		this.commands = new ConcurrentHashMap<>();

		this.commands.put("help", new HelpSlashCommand());
		this.commands.put("clear", new ClearSlashCommand());
		this.commands.put("shutdown", new Shutdownslashcommand());

		Klassenserver7bbot.INSTANCE.shardMan.getShards().forEach(shard -> {
			CommandListUpdateAction commup = shard.updateCommands();
			commup.addCommands(Commands.slash("help", "Gibt dir die Hilfe-Liste aus."));
			commup.addCommands(Commands.slash("shutdown", "fährt den Bot herunter"));
			commup.addCommands(Commands.slash("clear", "Löscht die Ausgewählte Anzahl an Nachrichten.")
					.addOptions(new OptionData(OptionType.INTEGER, "amount",
							"Wie viele Nachrichten sollen gelöscht werden? (Standart = 1 Nachricht)")));
			commup.queue();
		});
	}

	public boolean perform(SlashCommandInteraction event) {
		SlashCommand cmd;
		if ((cmd = this.commands.get(event.getName().toLowerCase())) != null) {

			cmd.performSlashCommand(event);

			System.out.println("Member: " + event.getMember().getEffectiveName() + " | \nChannel: "
					+ event.getChannel().getName() + " | \n Command: " + event.getName() + "\n");
			return true;
		}
		return false;
	}
}
