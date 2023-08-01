package de.klassenserver7b.k7bot.moderation.commands.common;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.PermissionError;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.HierarchyException;

public class KickCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		return "Kickt den ausgewählten Nutzer vom Server und übermitelt den angegebenen Grund.\n - kann nur von Personen mit der Berechtigung 'Mitglieder kicken' ausgeführt werden!\n - z.B. kick @K7Bot [reason]";
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "kick" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MODERATION;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		List<Member> ment = message.getMentions().getMembers();

		try {
			String[] args = message.getContentRaw().replaceAll("<@(\\d+)?>", "").split(" ");
			String grund = "";

			for (int i = 2; i < args.length; i++) {
				grund += args[i] + " ";
			}

			grund = grund.trim();

			channel.sendTyping().queue();

			if (m.hasPermission(Permission.KICK_MEMBERS)) {
				if (ment.size() > 0) {
					for (Member u : ment) {
						onkick(m, u, channel, grund);
					}
				}
			} else {
				PermissionError.onPermissionError(m, channel);
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "kick [@user] [reason]", m);
		}
	}

	public void onkick(Member requester, Member u, TextChannel channel, String grund) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
		builder.setColor(16711680);
		builder.setTitle("@" + u.getEffectiveName() + " was kicked");

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("**User: **" + u.getAsMention() + "\n");
		strBuilder.append("**Case: **" + grund + "\n");
		strBuilder.append("**Requester: **" + requester.getAsMention() + "\n");

		builder.setDescription(strBuilder);

		Guild guild = channel.getGuild();
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);

		try {
			u.kick().reason(grund).queue();

			if (system != null) {

				system.sendMessageEmbeds(builder.build()).queue();

			}

			if (system != null && system.getIdLong() != channel.getIdLong()) {

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);

			}

			String action = "kick";
			LiteSQL.onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?);",
					channel.getGuild().getIdLong(), u.getIdLong(), requester.getIdLong(), u.getEffectiveName(),
					requester.getEffectiveName(), action, grund, OffsetDateTime.now());
		}
		catch (HierarchyException e) {
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