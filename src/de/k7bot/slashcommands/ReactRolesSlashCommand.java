package de.k7bot.slashcommands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.manage.LiteSQL;
import de.k7bot.manage.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class ReactRolesSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(true).complete();

		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();
		Member m = event.getMember();

		if (m.hasPermission(Permission.MANAGE_ROLES)) {

			Emote emote;
			OptionMapping channel = event.getOption("channel");
			OptionMapping messageid = event.getOption("messageid");
			OptionMapping emoteop = event.getOption("emoteid-oder-utfemote");
			OptionMapping roleop = event.getOption("role");

			TextChannel tc = channel.getAsTextChannel();
			Role role = roleop.getAsRole();
			Long MessageId = messageid.getAsLong();

			try {

				emote = tc.getGuild().getEmoteById(emoteop.getAsLong());
				
				tc.addReactionById(MessageId, emote).queue();
				
				lsql.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES("
						+ tc.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + MessageId + ", '" + emote.getId()
						+ "', " + role.getIdLong() + ")");

			} catch (NumberFormatException e) {

				tc.addReactionById(MessageId, emoteop.getAsString()).queue();;
				
				lsql.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES("
						+ tc.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + MessageId + ", '"
						+ emoteop.getAsString() + "', " + role.getIdLong() + ")");

			}

			hook.sendMessage("Reactrole was successfull set for Message: " + MessageId).queue();

		} else {
			PermissionError.onPermissionError(m, event.getTextChannel());
		}

	}

}
