package de.klassenserver7b.k7bot.commands.slash;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.TableMessage;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class HA3MembersCommand implements TopLevelSlashCommand {

	private final Logger log = LoggerFactory.getLogger("HA3MemberList");

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		if (!isHA3Member(event.getUser())) {
			event.replyEmbeds(EmbedUtils
					.getErrorEmbed("Sorry, you are not allowed to use this command!", event.getGuild().getIdLong())
					.build()).queue();
			return;
		}

		InteractionHook hook = event.deferReply(true).complete();

		try (ResultSet set = LiteSQL.onQuery("SELECT * FROM ha3users;")) {

			TableMessage mess = new TableMessage();
			mess.addHeadline("Name", "DC Name", "InGame Name");

			while (set.next()) {
				mess.addRow(set.getString("realname"), set.getString("dcname"), set.getString("ingamename"));
			}

			hook.sendMessageEmbeds(EmbedUtils
					.getBuilderOf(Color.decode("#038aff"), mess.build(), event.getGuild().getIdLong()).build()).queue();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

	}

	private boolean isHA3Member(User u) {

		try (ResultSet set = LiteSQL.onQuery("SELECT dcId FROM ha3users;")) {

			while (set.next()) {
				if (set.getLong("dcId") == u.getIdLong()) {
					return true;
				}
			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

		return false;
	}

	@Override
	public @NotNull SlashCommandData getCommandData() {
		return Commands.slash("ha3members", "Zeigt die Mitglieder von HA3 mit Namen an");
	}

}
