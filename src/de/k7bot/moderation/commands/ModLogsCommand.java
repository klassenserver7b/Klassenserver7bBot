package de.k7bot.moderation.commands;

import de.k7bot.Klassenserver7bbot;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;

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
import net.dv8tion.jda.api.entities.TextChannel;

public class ModLogsCommand implements ServerCommand {
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
				long reqid = memb.get(0).getIdLong();
				ResultSet set = Klassenserver7bbot.INSTANCE.getDB()
						.onQuery("SELECT memberName, action, reason, date FROM modlogs  WHERE guildId = " + guildid
								+ " AND requesterId = " + reqid);

				try {
					ArrayList<String> membName = new ArrayList<>();
					ArrayList<String> action = new ArrayList<>();
					ArrayList<String> reason = new ArrayList<>();
					ArrayList<String> date = new ArrayList<>();

					for (int i = 1; i < 51 && set.next(); i++) {
						membName.add(set.getString("memberName"));
						action.add(set.getString("action"));
						reason.add(set.getString("reason"));
						date.add(set.getString("date"));
					}

					if (!membName.isEmpty()) {
						for (int j = 0; j < membName.size(); j++) {
							EmbedBuilder embed = new EmbedBuilder();
							embed.setTitle("Modlogs for @" + memb.get(0).getEffectiveName());
							embed.setColor(13565967);
							embed.setTimestamp(OffsetDateTime.now());
							embed.setThumbnail(memb.get(0).getUser().getEffectiveAvatarUrl());
							embed.setFooter("requested by @" + m.getEffectiveName());
							embed.setDescription("moderator: @" + memb.get(0).getEffectiveName() + "\n" + "action: "
									+ action.get(j) + "\n" + "user: " + membName.get(j) + "\n" + "reason: "
									+ reason.get(j) + "\n" + "date: " + date.get(j));
							channel.sendMessageEmbeds(embed.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0])
									.queue();
						}
					} else {

						channel.sendMessage("This moderator hasn't a log!").complete().delete().queueAfter(20L,
								TimeUnit.SECONDS);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {

				SyntaxError.oncmdSyntaxError(channel, "modlogs [@moderator]", m);
			}
		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}

	@Override
	public String gethelp() {
		return "Zeigt die Logs zu einem Moderator.\n - kann nur von Mitgliedern mit der Berechtigung 'Mitglieder kicken' ausgef?hrt werden!\n - z.B. [prefix]modlogs @moderator";
	}

	@Override
	public String getcategory() {
		return "Moderation";
	}
}
