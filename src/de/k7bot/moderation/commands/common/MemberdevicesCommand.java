package de.k7bot.moderation.commands.common;

import java.time.OffsetDateTime;
import java.util.List;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MemberdevicesCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		List<Member> memb = message.getMentions().getMembers();

		EmbedBuilder build = new EmbedBuilder();

		build.setFooter("requested by @" + m.getEffectiveName());
		build.setTimestamp(OffsetDateTime.now());
		StringBuilder strbuild = new StringBuilder();

		for (Member u : memb) {

			u.getActiveClients().forEach(c -> {

				strbuild.append(c.name());

			});

		}

		build.setDescription(strbuild.toString().trim());

		channel.sendMessageEmbeds(build.build()).queue();

	}

}
