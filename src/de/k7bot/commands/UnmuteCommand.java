package de.k7bot.commands;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.manage.PermissionError;
import de.k7bot.manage.SyntaxError;
import java.time.OffsetDateTime;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class UnmuteCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		List<Member> ment = message.getMentionedMembers();
		try {
			message.delete().queue();

			channel.sendTyping().queue();

			if (m.hasPermission(new Permission[] { Permission.KICK_MEMBERS })) {
				if (ment.size() > 0) {
					for (Member u : ment) {
						onUnMute(m, u, channel, message);
					}
				}
			} else {
				PermissionError.onPermissionError(m, channel);
			}
		} catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(channel, "unmute [@user]", m);
		}
	}

	public void onUnMute(Member requester, Member u, TextChannel channel, Message message) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		builder.setColor(16711680);
		builder.setTitle("@" + u.getEffectiveName() + " was unmuted");

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getAsMention() + "\n");
		strBuilder.append("**Requester: **" + requester.getAsMention() + "\n");

		builder.setDescription(strBuilder);

		Guild guild = channel.getGuild();
		TextChannel system = guild.getSystemChannel();

		try {
			Guild g = channel.getGuild();
			g.removeRoleFromMember(u.getIdLong(), g.getRoleById(702828274837094400L));

			if (system.getIdLong() != channel.getIdLong()) {
				channel.sendMessageEmbeds(builder.build()).queue();
			}
		} catch (HierarchyException e) {
			PermissionError.onPermissionError(requester, channel);
		}
	}
}