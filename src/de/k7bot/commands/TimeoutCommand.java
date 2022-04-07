package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class TimeoutCommand implements ServerCommand {

	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		List<Member> ment = message.getMentionedMembers();
		String[] args = message.getContentStripped().split(" ");

		try {
			
			channel.sendTyping().queue();

			if (m.hasPermission(Permission.MESSAGE_MANAGE)) {
				if (ment.size() > 0) {
					for (Member u : ment) {
						onTimeout(m, u, channel, args[1], args[2]);
					}
				}
			} else {
				PermissionError.onPermissionError(m, channel);
			}
		} catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(channel, "timeout [time (in minutes)] [reason] @user", m);
		}
	}

	public void onTimeout(Member requester, Member u, TextChannel channel, String time, String grund) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		builder.setColor(16711680);

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getAsMention() + "\n");
		strBuilder.append("**Case: **" + grund + "\n");
		strBuilder.append("**Requester: **" + requester.getAsMention() + "\n");


		Guild guild = channel.getGuild();
		TextChannel system = guild.getSystemChannel();

		try {
			u.timeoutFor(Long.parseLong(time), TimeUnit.MINUTES).queue();
			builder.setTitle("@" + u.getEffectiveName() + " has been timeouted for "+Long.parseLong(time)+" minutes");
			builder.setDescription(strBuilder);
			
			if (system.getIdLong() != channel.getIdLong()) {
				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);
				system.sendMessageEmbeds(builder.build()).queue();
			}else {
				system.sendMessageEmbeds(builder.build()).queue();
			}

			String action = "timeout";
			Klassenserver7bbot.INSTANCE.getDB().onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES("
							+ channel.getGuild().getIdLong() + ", " + u.getIdLong() + ", " + requester.getIdLong()
							+ ", '" + u.getEffectiveName() + "', '" + requester.getEffectiveName() + "', '" + action
							+ "', '" + grund + "', '" + OffsetDateTime.now() + "')");
		} catch (HierarchyException e) {

			PermissionError.onPermissionError(requester, channel);

		} catch (NumberFormatException e) {

			SyntaxError.oncmdSyntaxError(channel, "timeout [time (in minutes)] [reason] @user", requester);

		}
	}
}