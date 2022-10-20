package de.k7bot.commands.common;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.errorhandler.PermissionError;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ClientInfo implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		channel.sendTyping().queue();
		List<Member> ment = message.getMentions().getMembers();

		if (m.hasPermission(Permission.KICK_MEMBERS, Permission.NICKNAME_MANAGE, Permission.NICKNAME_CHANGE)) {
			if (ment.size() > 0) {
				for (Member u : ment) {
					onInfo(m, u, channel);
				}
			}
		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}

	@Override
	public String gethelp() {
		String help = "Zeigt die Informationen zum angegebenen User.\n - z.B. [prefix]memberinfo [@USER]";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Moderation";
		return category;
	}

	public void onInfo(Member requester, Member u, TextChannel channel) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		builder.setColor(16711680);
		builder.setTitle("Info zu " + u.getAsMention());

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getAsMention() + "\n");
		strBuilder.append("**ClientId: **" + u.getId() + "\n");
		strBuilder.append("**TimeJoined: **" + u.getTimeJoined() + "\n");
		strBuilder.append("**TimeCreated: **" + u.getTimeCreated() + "\n");
		strBuilder.append("**Nickname: **" + u.getNickname() + "\n");
		strBuilder.append("**Status: **" + u.getOnlineStatus() + "\n");
		strBuilder.append("**Is Owner: **" + u.isOwner() + "\n");
		strBuilder.append("**Permissions: **" + u.getPermissions() + "\n\n");

		strBuilder.append("**Roles: **\n");

		StringBuilder roleBuild = new StringBuilder();
		for (Role role : u.getRoles()) {
			roleBuild.append(role.getAsMention() + " ");
		}

		strBuilder.append(roleBuild.toString().trim() + "\n");

		builder.setDescription(strBuilder);

		channel.sendMessageEmbeds(builder.build()).complete().delete()
				.queueAfter(20L, TimeUnit.SECONDS);
	}
}
