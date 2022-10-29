package de.k7bot.util.commands.slash;

import de.k7bot.sql.LiteSQL;
import de.k7bot.util.errorhandler.PermissionError;

import org.jetbrains.annotations.NotNull;

import de.k7bot.commands.types.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ReactRolesSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(true).complete();
		Member m = event.getMember();

		if (m.hasPermission(Permission.MANAGE_ROLES)) {

			CustomEmoji emote;
			OptionMapping channel = event.getOption("channel");
			OptionMapping messageid = event.getOption("messageid");
			OptionMapping emoteop = event.getOption("emoteid-oder-utfemote");
			OptionMapping roleop = event.getOption("role");

			TextChannel tc = channel.getAsChannel().asTextChannel();
			Role role = roleop.getAsRole();
			long MessageId = messageid.getAsLong();

			try {

				emote = tc.getGuild().getEmojiById(emoteop.getAsLong());

				tc.addReactionById(MessageId, emote).queue();

				LiteSQL.onUpdate(
						"INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES(?, ?, ?, ?, ?);",
						tc.getGuild().getIdLong(), tc.getIdLong(), MessageId, emote.getId(), role.getIdLong());

			} catch (NumberFormatException e) {

				tc.addReactionById(MessageId, Emoji.fromUnicode(emoteop.getAsString())).queue();

				LiteSQL.onUpdate(
						"INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES(?, ?, ?, ?, ?);",
						tc.getGuild().getIdLong(), tc.getIdLong(), MessageId, emoteop.getAsString(), role.getIdLong());

			}

			hook.sendMessage("Reactrole was successfull set for Message: " + MessageId).queue();

		} else {
			PermissionError.onPermissionError(m, event.getChannel().asTextChannel());
		}

	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("reactrole", "Erstellt eine Reactionrole mit den übermittelten Parametern")
				.addOption(OptionType.CHANNEL, "channel", "Der Channel in dem die Message ist", true)
				.addOption(OptionType.STRING, "messageid",
						"Die MessageId der Message an die die Reaction angefügt werden soll", true)
				.addOption(OptionType.STRING, "emoteid-oder-utfemote",
						"Die EmoteId des Emotes bzw. das UTF8 Emoji auf das die Rolle registriert werden soll", true)
				.addOption(OptionType.ROLE, "role",
						"Die Rolle die zugewiesen werden soll -  stelle sicher: Rechte und Rolle des Bots > Rechte der Rolle",
						true);
	}

}
