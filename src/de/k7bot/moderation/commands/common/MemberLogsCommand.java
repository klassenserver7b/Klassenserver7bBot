
package de.k7bot.moderation.commands.common;

import de.k7bot.sql.LiteSQL;
import de.k7bot.util.errorhandler.PermissionError;
import de.k7bot.util.errorhandler.SyntaxError;
import de.k7bot.commands.types.ServerCommand;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MemberLogsCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.KICK_MEMBERS)) {
			List<Member> memb = message.getMentions().getMembers();
			if (!memb.isEmpty()) {
				List<Role> roles = memb.get(0).getRoles();
				ArrayList<Long> roleid = new ArrayList<>();
				for (Role r : roles) {
					roleid.add(r.getIdLong());
				}
				long guildid = channel.getGuild().getIdLong();
				long membid = memb.get(0).getIdLong();
				ResultSet set = LiteSQL.onQuery(
						"SELECT requesterName, action, reason, date FROM modlogs  WHERE guildId = AND memberId = ?",
						guildid, membid);

				try {
					ArrayList<String> requName = new ArrayList<>();
					ArrayList<String> action = new ArrayList<>();
					ArrayList<String> reason = new ArrayList<>();
					ArrayList<String> date = new ArrayList<>();

					for (int i = 1; i < 51 && set.next(); i++) {
						requName.add(set.getString("requesterName"));
						action.add(set.getString("action"));
						reason.add(set.getString("reason"));
						date.add(set.getString("date"));
					}

					if (!requName.isEmpty()) {
						for (int j = 0; j < requName.size(); j++) {
							EmbedBuilder embed = new EmbedBuilder();
							embed.setTitle("Memberlogs for @" + memb.get(0).getEffectiveName());
							embed.setColor(13565967);
							embed.setTimestamp(OffsetDateTime.now());
							embed.setThumbnail(memb.get(0).getUser().getEffectiveAvatarUrl());
							embed.setFooter("requested by @" + m.getEffectiveName());
							embed.setDescription("user: @" + memb.get(0).getEffectiveName() + "\n" + "action: "
									+ action.get(j) + "\n" + "moderator: " + requName.get(j) + "\n" + "reason: "
									+ reason.get(j) + "\n" + "date: " + date.get(j));
							channel.sendMessageEmbeds(embed.build()).queue();
						}
					} else {

						channel.sendMessage("This user hasn't a log!").complete().delete().queueAfter(20L,
								TimeUnit.SECONDS);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				SyntaxError.oncmdSyntaxError(channel, "memberlogs [@user]", m);
			}
		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}

	@Override
	public String gethelp() {
		return "Zeigt die Logs zu einem Mitglied.\n - kann nur von Mitgliedern mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]modlogs @member";
	}

	@Override
	public String getcategory() {
		return "Moderation";
	}
}
