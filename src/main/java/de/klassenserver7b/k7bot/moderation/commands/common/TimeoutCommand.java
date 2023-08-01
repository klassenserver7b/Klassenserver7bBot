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

public class TimeoutCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "timeoutet den angegeben Nutzer für den Ausgewählten Grund.\n - kann nur von Mitgliedern mit der Berechtigung 'Nachrichten verwalten' ausgeführt werden!\n - z.B. [prefix]timeout [zeit (in minuten)] [reason] @member";

		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "timeout" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MODERATION;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		List<Member> ment = message.getMentions().getMembers();
		String[] args = message.getContentRaw().replaceAll("<@(\\d+)?>", "").split(" ");
		String grund = "";

		for (int i = 2; i < args.length; i++) {
			grund += args[i];
		}

		grund = grund.trim();

		try {

			channel.sendTyping().queue();

			if (m.hasPermission(Permission.MESSAGE_MANAGE)) {
				if (ment.size() > 0) {
					for (Member u : ment) {
						onTimeout(m, u, channel, args[1], grund);
					}
				}
			} else {
				PermissionError.onPermissionError(m, channel);
			}
		}
		catch (StringIndexOutOfBoundsException e) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel),
					"timeout [time (in minutes)] [reason] @user", m);
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
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);

		try {
			u.timeoutFor(Long.parseLong(time), TimeUnit.MINUTES).queue();
			builder.setTitle(
					"@" + u.getEffectiveName() + " has been timeouted for " + Long.parseLong(time) + " minutes");
			builder.setDescription(strBuilder);

			if (system != null) {

				system.sendMessageEmbeds(builder.build()).queue();

			}

			if (system == null || system.getIdLong() != channel.getIdLong()) {

				channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(20L, TimeUnit.SECONDS);

			}

			if (requester.getIdLong() == Klassenserver7bbot.getInstance().getOwnerId()) {
				builder.setTitle("You have been timeouted for " + Long.parseLong(time) + " minutes");
				builder.setFooter("");
				builder.setDescription("**Case: **" + grund + "\n");

				u.getUser().openPrivateChannel().queue((ch) -> {
					ch.sendMessageEmbeds(builder.build()).queue();
				});
			} else {
				builder.setTitle("You have been timeouted for " + Long.parseLong(time) + " minutes");
				builder.setDescription("**Case: **" + grund + "\n**Requester: **" + requester.getAsMention() + "\n");
				u.getUser().openPrivateChannel().queue((ch) -> {
					ch.sendMessageEmbeds(builder.build()).queue();
				});
			}

			String action = "timeout";
			LiteSQL.onUpdate(
					"INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES(?, ?, ?, ?, ?, ?, ?, ?);",
					channel.getGuild().getIdLong(), u.getIdLong(), requester.getIdLong(), u.getEffectiveName(),
					requester.getEffectiveName(), action, grund, OffsetDateTime.now());
		}
		catch (HierarchyException e) {

			PermissionError.onPermissionError(requester, channel);

		}
		catch (NumberFormatException e) {

			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel),
					"timeout [time (in minutes)] [reason] @user", requester);

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