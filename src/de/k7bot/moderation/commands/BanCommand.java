package de.k7bot.moderation.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class BanCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		List<Member> ment = message.getMentions().getMembers();
		try {

			String[] args = message.getContentDisplay().replaceAll("<@(\\d+)?>", "").split(" ");
			String grund = "";

			for (int i = 1; i < args.length; i++) {
				grund += args[i] + " ";
			}

			grund = grund.trim();

			channel.sendTyping().queue();

			if (m.hasPermission(Permission.BAN_MEMBERS)) {
				if (ment.size() > 0) {
					for (Member u : ment) {
						onBan(m, u, channel, grund);
					}
				}
			} else {
				PermissionError.onPermissionError(m, channel);
			}
		} catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(channel, "ban [@user] [reason]", m);
		}
	}

	@Override
	public String gethelp() {
		return "Bannt den ausgewählten Nutzer vom Server und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder bannen' ausgeführt werden!\n - z.B. [prefix]ban [@USER] [reason]";
	}

	@Override
	public String getcategory() {
		return "Moderation";
	}

	public void onBan(Member requester, Member u, TextChannel channel, String grund) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		builder.setColor(16711680);
		builder.setTitle("@" + u.getEffectiveName() + " was banned");

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getAsMention() + "\n");
		strBuilder.append("**Case: **" + grund + "\n");
		strBuilder.append("**Requester: **" + requester.getAsMention() + "\n");

		builder.setDescription(strBuilder);

		TextChannel system = Klassenserver7bbot.INSTANCE.getsyschannell().getSysChannel(channel.getGuild());

		try {
			u.ban(7, TimeUnit.DAYS).reason(grund).queue();

			if (system != null) {

				system.sendMessageEmbeds(builder.build()).queue();

			}

			if (system.getIdLong() != channel.getIdLong()) {

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);

			}

			String action = "ban";
			LiteSQL.onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?);",
					channel.getGuild().getIdLong(), u.getIdLong(), requester.getIdLong(), u.getEffectiveName(),
					requester.getEffectiveName(), action, grund, OffsetDateTime.now());
		} catch (HierarchyException e) {
			PermissionError.onPermissionError(requester, channel);
		}
	}
}