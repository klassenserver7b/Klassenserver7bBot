
package de.klassenserver7b.k7bot.hypixel;

import java.util.concurrent.ConcurrentHashMap;

import de.klassenserver7b.k7bot.commands.types.HypixelCommand;
import de.klassenserver7b.k7bot.hypixel.commands.HypixelHelpCommand;
import de.klassenserver7b.k7bot.hypixel.commands.HypixelRankCommand;
import de.klassenserver7b.k7bot.hypixel.commands.OnlineCommand;
import de.klassenserver7b.k7bot.hypixel.commands.PlayerCountCommand;
import de.klassenserver7b.k7bot.hypixel.commands.PlayerInfoCommand;
import de.klassenserver7b.k7bot.hypixel.commands.TestCommand;
import de.klassenserver7b.k7bot.hypixel.commands.WatchdogCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class HypixelCommandManager {
	public ConcurrentHashMap<String, HypixelCommand> commands;
	public ConcurrentHashMap<String, String> help;

	public HypixelCommandManager() {
		this.commands = new ConcurrentHashMap<>();
		this.help = new ConcurrentHashMap<>();

		this.commands.put("help", new HypixelHelpCommand());
		this.commands.put("playercount", new PlayerCountCommand());
		this.commands.put("pc", new PlayerCountCommand());
		this.commands.put("watchdog", new WatchdogCommand());
		this.commands.put("rank", new HypixelRankCommand());
		this.commands.put("test", new TestCommand());
		this.commands.put("playerinfo", new PlayerInfoCommand());
		this.commands.put("online", new OnlineCommand());

		this.help.put("help", "Zeigt diese Hilfe an.");
		this.help.put("playercount", "Zeigt die Aktuelle Spieleranzahl auf Hypixel.");
		this.help.put("pc", "Alias für playercount.");
		this.help.put("watchdog", "Zeigt die aktuellen Staff und Watchdog Statistiken.");
		this.help.put("rank",
				"Gibt den höchsten Rang des Spielers auf Hypixel zurück (VIP/VIP+/MVP/MVP+/MVP++).\n- z.B. "
						+ "[prefix]" + "hypixel rank [playername]");
		this.help.put("karma", "Zeigt die Menge an Karma, die ein Spieler besitzt.\n- z.B. " + "[prefix]"
				+ "hypixel karma [playername]");
		this.help.put("playerinfo",
				"Zeigt die allerlei Infos zum Spieler.\n- z.B. " + "[prefix]" + "hypixel playerinfo [playername]");
		this.help.put("online",
				"Zeigt ob ein Spieler aktuell online ist und welchen Spielmodus er gerade spielt.\n- z.B. " + "[prefix]"
						+ "hypixel online [playername]");
	}

	public boolean performHypixel(String command, Member m, TextChannel channel, Message message) {
		HypixelCommand cmd;
		if ((cmd = this.commands.get(command.toLowerCase())) != null) {

			cmd.performHypixelCommand(m, channel, message);
			return true;
		}
		return false;
	}
}