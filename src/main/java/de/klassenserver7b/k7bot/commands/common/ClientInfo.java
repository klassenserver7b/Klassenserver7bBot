package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.errorhandler.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author K7
 *
 */
public class ClientInfo implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "Zeigt die Informationen zum angegebenen User.\n - z.B. [prefix]memberinfo [@USER]";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "memberinfo" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MODERATION;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

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

	public void onInfo(Member requester, Member u, GuildMessageChannel channel) {

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

		EmbedBuilder builder = EmbedUtils.getBuilderOf(Color.red, strBuilder,
				channel.getGuild().getIdLong());
		builder.setTitle("Info zu " + u.getEffectiveName());
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());

		channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);
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
