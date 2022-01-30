
package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HypixelHelpCommand implements HypixelCommand {
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		ConcurrentHashMap<String, HypixelCommand> commands = (Klassenserver7bbot.INSTANCE.gethypMan()).commands;
		ConcurrentHashMap<String, String> help = (Klassenserver7bbot.INSTANCE.gethypMan()).help;

		StringBuilder strbuild = new StringBuilder();
		commands.forEachKey(0L, comm -> {
			if (help.get(comm) != null) {
				strbuild.append("`" + comm + "` - " + (String) help.get(comm) + "\n\n");
			}
		});
		EmbedBuilder messbuild = new EmbedBuilder();

		messbuild.setDescription("Hypixel-Befehle auf diesem Server starten mit `"
				+ Klassenserver7bbot.INSTANCE.prefixl.get(channel.getGuild().getIdLong()) + "Hypixel []`\n"
				+ "z.B. `-Hypixel help` \n\n" + "**Hilfe zu den Hypixel Commands:**\n\n" + strbuild.toString().trim());
		messbuild.setAuthor("Klassenserver 7b ", null,
				"https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Help-browser.svg/48px-Help-browser.svg.png");
		messbuild.setColor(3066993);
		messbuild.setFooter("by @Klassenserver 7b");
		messbuild.setTitle("Hilfe zum K7Bot - Hypixel");
		messbuild.setTimestamp(OffsetDateTime.now());
		((Message) channel.sendMessage("** look into your DM's **" + m.getAsMention()
				+ "\n (Only available if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!)")
				.complete()).delete().queueAfter(10L, TimeUnit.SECONDS);

		m.getUser().openPrivateChannel().queue(ch -> ch.sendMessageEmbeds(messbuild.build()).queue());
	}
}
