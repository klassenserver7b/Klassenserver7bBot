
package de.k7bot.util.commands.common;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.MessageClearUtil;
import de.k7bot.util.errorhandler.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ClearCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		return "Löscht die angegebene Anzahl an Nachrichten.\n - z.B. [prefix]clear 50";
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "clear" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.TOOLS;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(channel, Permission.MESSAGE_MANAGE)) {

			String[] args = message.getContentStripped().split(" ");

			if (args.length == 2) {

				int amount = Integer.parseInt(args[1]);

				MessageClearUtil.onclear(amount, channel);

				TextChannel system = Klassenserver7bbot.getInstance().getsyschannell()
						.getSysChannel(channel.getGuild());

				EmbedBuilder builder = new EmbedBuilder();
				builder.setColor(16345358);
				builder.setFooter("requested by @" + m.getEffectiveName());
				builder.setTimestamp(OffsetDateTime.now());
				builder.setDescription(amount + " messages deleted!\n\n" + "**Channel: **\n" + "#" + channel.getName());

				if (system != null) {

					system.sendMessageEmbeds(builder.build()).queue();

				}

				if (system != null && system.getIdLong() != channel.getIdLong()) {

					channel.sendMessage(amount + " messages deleted.").complete().delete().queueAfter(3L,
							TimeUnit.SECONDS);

				}
			}

		}

		else {

			PermissionError.onPermissionError(m, channel);

		}

	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}
}
