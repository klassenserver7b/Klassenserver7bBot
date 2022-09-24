package de.k7bot.moderation.commands;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class WarnCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		List<Member> ment = message.getMentions().getMembers();
		try {
			if (!ment.isEmpty()) {
				String[] args = message.getContentRaw().replaceAll("<@(\\d+)?>", "").split(" ");
				String grund = "";
				
				for(int i = 1; i<args.length; i++) {
					grund = grund + args[i];
				}

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
		TextChannel system = Klassenserver7bbot.INSTANCE.getsyschannell().getSysChannel(guild);

		try {

			if (system != null) {

				system.sendMessageEmbeds(builder.build()).queue();

			}

			if (system.getIdLong() != channel.getIdLong()) {

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);

			}

			u.getUser().openPrivateChannel().queue((ch) -> {
				ch.sendMessageEmbeds(builder.build()).queue();
			});

			String action = "warn";

			LiteSQL.onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES("
							+ channel.getGuild().getIdLong() + ", " + u.getIdLong() + ", " + requester.getIdLong()
							+ ", '" + u.getEffectiveName() + "', '" + requester.getEffectiveName() + "', '" + action
							+ "', '" + grund + "', '" + OffsetDateTime.now() + "')");
		} catch (HierarchyException e) {
			PermissionError.onPermissionError(requester, channel);
		}
	}

	@Override
	public String gethelp() {
		String help = "Verwarnt den angegebenen Nutzer und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. [prefix]warn @K7Bot [reason]";

		return help;
	}

	@Override
	public String getcategory() {
		String category = "Moderation";
		return category;
	}

}