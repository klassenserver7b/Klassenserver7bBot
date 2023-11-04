package de.klassenserver7b.k7bot.listener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleListener extends ListenerAdapter {

	@Override
	public void onRoleCreate(RoleCreateEvent event) {

		String gen = "";

		Guild guild = event.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Role role = event.getRole();

		for (Permission perm : role.getPermissions()) {
			gen = gen + perm.getName() + ", ";
		}

		EmbedBuilder builder = EmbedUtils.getSuccessEmbed(
				"**Role: **\n @" + role.getName() + "\n\n **Permissions: **\n" + gen, event.getGuild().getIdLong());

		builder.setTitle("Role created: " + role.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public void onRoleDelete(RoleDeleteEvent event) {

		Guild guild = event.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Role role = event.getRole();

		EmbedBuilder builder = EmbedUtils.getErrorEmbed("**Role: **\n @" + role.getName(),
				event.getGuild().getIdLong());

		builder.setTitle("Role deleted: " + role.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent event) {

		String gen = "";

		Guild guild = event.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Role role = event.getRole();

		for (Permission perm : role.getPermissions()) {
			gen = String.valueOf(gen) + perm.getName() + ", ";
		}

		EmbedBuilder builder = EmbedUtils
				.getBuilderOf(
						Color.decode("#038aff"), "Old Rolename: **\n @" + event.getOldName()
								+ "\n\n**New Rolename: **\n @" + event.getNewName() + "\n\n **Permissions: **\n" + gen,
						event.getGuild().getIdLong());

		builder.setTitle("Role(**Name**) edited: " + role.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event) {

		String remove = "";
		String added = "";
		String gen = "";

		List<String> rem = new ArrayList<>();
		List<String> add = new ArrayList<>();

		event.getOldPermissions().forEach(perm -> {
			if (!event.getNewPermissions().contains(perm))
				rem.add(perm.getName());
		});

		for (Permission perm : event.getNewPermissions()) {
			gen += perm.getName() + ", ";
			if (!event.getOldPermissions().contains(perm))
				add.add(perm.getName());
		}

		for (String s : rem) {
			remove = remove + s + ", ";
		}

		for (String s : add) {
			added = added + s + ", ";
		}

		Guild guild = event.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		Role role = event.getRole();

		EmbedBuilder builder = EmbedUtils.getBuilderOf(Color.decode("#038aff"),
				"**Role: **\n @" + role.getName() + "\n\n**Executive user: ** " + "\n\nRemoved Permissions: **\n"
						+ remove + "\n\n **Added Permissions: **\n" + added + "\n\n **Permissions: **\n" + gen,
				event.getGuild().getIdLong());

		builder.setTitle("Role(**Permissions**) edited: " + role.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}
}
