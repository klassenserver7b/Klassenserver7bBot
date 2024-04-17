package de.klassenserver7b.k7bot.moderation.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.PermissionError;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WarnCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String getHelp() {
		String help = "Verwarnt den angegebenen Nutzer und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]warn @K7Bot [reason]";

		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "warn" };
	}

	@Override
	public HelpCategories getCategory() {
		return HelpCategories.MODERATION;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		List<Member> ment = message.getMentions().getMembers();
		try {
			if (!ment.isEmpty()) {
				String[] args = message.getContentRaw().replaceAll("<@(\\d+)?>", "").split(" ");
				String grund = "";

				for (int i = 1; i < args.length; i++) {
					grund = grund + args[i];
				}

				channel.sendTyping().queue();

				if (m.hasPermission(Permission.KICK_MEMBERS)) {
					if (ment.size() > 0) {
						for (Member u : ment) {
							onWarn(m, u, channel, grund);
						}
					}
				} else {
					PermissionError.onPermissionError(m, channel);
				}
			} else {
				SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "warn [@user] [reason]", m);
			}
		} catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "warn [@user] [reason]", m);
		}
	}

	public void onWarn(Member requester, Member u, GuildMessageChannel channel, String grund) {

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getUser().getAsMention() + "\n");
		strBuilder.append("**Case: **" + grund + "\n");
		strBuilder.append("**Requester: **" + requester.getEffectiveName() + "\n");
		strBuilder.append("**Server: **" + channel.getGuild().getName() + "\n");

		EmbedBuilder builder = EmbedUtils.getErrorEmbed(strBuilder, channel.getGuild().getIdLong());

		builder.setTitle("Warning logged for @" + u.getEffectiveName());
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());

		Guild guild = channel.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);

		try {

			if (system != null) {

				system.sendMessageEmbeds(builder.build()).queue();

			}

			if (system == null || system.getIdLong() != channel.getIdLong()) {

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);

			}

			u.getUser().openPrivateChannel().queue((ch) -> {
				ch.sendMessageEmbeds(builder.build()).queue();
			});

			String action = "warn";

			LiteSQL.onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?);",
					channel.getGuild().getIdLong(), u.getIdLong(), requester.getIdLong(), u.getEffectiveName(),
					requester.getEffectiveName(), action, grund, OffsetDateTime.now());
		} catch (HierarchyException e) {
			PermissionError.onPermissionError(requester, channel);
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