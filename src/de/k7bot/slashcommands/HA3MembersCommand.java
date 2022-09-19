package de.k7bot.slashcommands;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.SQL.LiteSQL;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.util.TableMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class HA3MembersCommand implements SlashCommand{
	
	private final Logger log = LoggerFactory.getLogger("HA3MemberList");

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {
		
		if(!isHA3Member(event.getUser())) {
			event.replyEmbeds(new EmbedBuilder().setColor(Color.decode("#ff0000")).setFooter("Handeled by @K7Bot").setDescription("Sorry, you are not allowed to use this command!").build()).queue();
			return;
		}
		
		InteractionHook hook = event.deferReply(true).complete();
		
		ResultSet set = LiteSQL.onQuery("SELECT * FROM ha3users");
		
		TableMessage mess = new TableMessage();
		mess.addHeadline("Name","DC Name","InGame Name");
		
		try {
			while(set.next()) {
				mess.addRow(set.getString("realname"), set.getString("dcname"), set.getString("ingamename"));
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		hook.sendMessageEmbeds(new EmbedBuilder().setDescription(mess.build()).setColor(Color.decode("#038aff")).setFooter("Generated by @K7Bot").setTimestamp(OffsetDateTime.now()).build()).queue();
		
	}
	
	private boolean isHA3Member(User u) {
		
		ResultSet set = LiteSQL.onQuery("SELECT dcId FROM ha3users");
		
		try {
			while(set.next()) {
				if(set.getLong("dcId") == u.getIdLong()) {
					return true;
				}
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
		
		return false;
	}

}