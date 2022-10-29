package de.k7bot.listener;

import java.time.OffsetDateTime;

import de.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Role_InviteListener extends ListenerAdapter {
	String gen = "";

	public void onRoleCreate(RoleCreateEvent event) {

		Guild guild = event.getGuild();
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Role rolle = event.getRole();

		rolle.getPermissions().forEach(perm -> this.gen = String.valueOf(this.gen) + perm.getName() + ", ");

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter(guild.getName());
		builder.setColor(58944);
		builder.setTitle("Role created: " + rolle.getName());
		builder.setDescription("**Role: **\n @" + rolle.getName() + "\n\n **Permissions: **\n" + this.gen);
		system.sendMessageEmbeds(builder.build()).queue();

		this.gen = "";
	}

	public void onRoleDelete(RoleDeleteEvent event) {

		Guild guild = event.getGuild();
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Role rolle = event.getRole();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter(guild.getName());
		builder.setColor(13565967);
		builder.setTitle("Role deleted: " + rolle.getName());
		builder.setDescription("**Role: **\n @" + rolle.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

	public void onGuildInviteCreate(GuildInviteCreateEvent event) {

		Guild guild = event.getGuild();
		GuildChannel channel = event.getChannel();
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Invite inv = event.getInvite();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter(guild.getName());
		builder.setColor(58944);
		builder.setTitle("Invite created for " + channel.getName());
		builder.setDescription("**Invite: **\n " + inv.getUrl() + "\n\n **Channel: **\n" + channel.getName()
				+ "\n\n **Inviter: **\n@" + inv.getInviter().getName() + "\n\n **Is temporary: **\n" + inv.isTemporary()
				+ "\n\n **Is expandet: **\n" + inv.isExpanded());
		system.sendMessageEmbeds(builder.build()).queue();
	}
}
