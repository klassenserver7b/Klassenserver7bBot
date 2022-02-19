package de.k7bot.commands;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.manage.PermissionError;
import de.k7bot.manage.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class WarnCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		List<Member> ment = message.getMentionedMembers();
		try {
			if (!ment.isEmpty()) {
				String grund = message.getContentDisplay().substring((ment.get(0).getEffectiveName().length() + 8));

				channel.sendTyping().queue();

				if (m.hasPermission(Permission.KICK_MEMBERS)) {
					if (ment.size() > 0) {
						for (Member u : ment) {
							onWarn(m, u, channel, message, grund);
						}
					}
				} else {
					PermissionError.onPermissionError(m, channel);
				}
			} else {
				SyntaxError.oncmdSyntaxError(channel, "warn [@user] [reason]", m);
			}
		} catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(channel, "warn [@user] [reason]", m);
		}
	}

	public void onWarn(Member requester, Member u, TextChannel channel, Message message, String grund) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		builder.setColor(0xff0000);
		builder.setTitle("Warning logged for @" + u.getEffectiveName());

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getAsMention() + "\n");
		strBuilder.append("**Case: **" + grund + "\n");
		strBuilder.append("**Requester: **" + requester.getAsMention() + "\n");
		strBuilder.append("**Server: **" + channel.getGuild().getName() + "\n");

		builder.setDescription(strBuilder);

		Guild guild = channel.getGuild();
		TextChannel system = guild.getSystemChannel();

		try {

			if (system.getIdLong() != channel.getIdLong()) {
				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);
				system.sendMessageEmbeds(builder.build()).queue();
			}else {
				system.sendMessageEmbeds(builder.build()).queue();
			}

			u.getUser().openPrivateChannel().queue((ch) -> {
				ch.sendMessageEmbeds(builder.build()).queue();
			});

			String action = "warn";

			Klassenserver7bbot.INSTANCE.getDB().onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES("
							+ channel.getGuild().getIdLong() + ", " + u.getIdLong() + ", " + requester.getIdLong()
							+ ", '" + u.getEffectiveName() + "', '" + requester.getEffectiveName() + "', '" + action
							+ "', '" + grund + "', '" + OffsetDateTime.now() + "')");
		} catch (HierarchyException e) {
			PermissionError.onPermissionError(requester, channel);
		}
	}

}