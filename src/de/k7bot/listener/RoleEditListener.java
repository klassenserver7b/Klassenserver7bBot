package de.k7bot.listener;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import de.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleEditListener extends ListenerAdapter {
	String remove = "";

	String added = "";

	String gen = "";

	public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent event) {
		Klassenserver7bbot.INSTANCE.getsyschannell().checkSysChannelList();

		Guild guild = event.getGuild();
		TextChannel system = Klassenserver7bbot.INSTANCE.getsyschannell().getSysChannel(guild);
		Role rolle = event.getRole();
		rolle.getPermissions().forEach(perm -> this.gen = String.valueOf(this.gen) + perm.getName() + ", ");
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter(guild.getName());
		builder.setColor(58944);
		builder.setTitle("Role(**Name**) edited: " + rolle.getName());
		builder.setDescription("Old Rolename: **\n @" + event.getOldName() + "\n\n**New Rolename: **\n @"
				+ event.getNewName() + "\n\n **Permissions: **\n" + this.gen);
		system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
		this.gen = "";
	}

	public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event) {
		Klassenserver7bbot.INSTANCE.getsyschannell().checkSysChannelList();

		List<String> rem = new ArrayList<>();
		List<String> add = new ArrayList<>();
		event.getOldPermissions().forEach(perm -> {
			if (!event.getNewPermissions().contains(perm))
				add.add(perm.getName());
		});
		event.getNewPermissions().forEach(perm -> {
			this.gen = String.valueOf(this.gen) + perm.getName() + ", ";
			if (!event.getOldPermissions().contains(perm))
				rem.add(perm.getName());
		});
		rem.forEach(str -> this.remove = String.valueOf(this.remove) + str + ", ");
		add.forEach(str -> this.added = String.valueOf(this.added) + str + ", ");
		Guild guild = event.getGuild();
		TextChannel system = Klassenserver7bbot.INSTANCE.getsyschannell().getSysChannel(guild);
		Role rolle = event.getRole();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter(guild.getName());
		builder.setColor(58944);
		builder.setTitle("Role(**Permissions**) edited: " + rolle.getName());
		builder.setDescription("**Role: **\n @" + rolle.getName() + "\n\n **Removed Permissions: **\n" + this.remove
				+ "\n\n **Added Permissions: **\n" + this.added + "\n\n **Permissions: **\n" + this.gen);
		system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
		this.gen = "";
	}
}