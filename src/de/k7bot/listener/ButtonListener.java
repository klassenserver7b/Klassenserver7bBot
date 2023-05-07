package de.k7bot.listener;

import java.awt.Color;
import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class ButtonListener extends ListenerAdapter {
	public final Logger log = LoggerFactory.getLogger("HA3Buttons");

	@Override
	public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {

		String compid = event.getComponentId();

		switch (compid) {
		case "ApproveHA3Request": {
			ApproveHA3Request(event);
			break;
		}
		case "DenyHA3Request": {
			DenyHA3Request(event);
			break;
		}
		case "inWhitelistInserted": {
			inWhitelistInserted(event);
			break;
		}
		}

	}

	private void ApproveHA3Request(ButtonInteractionEvent event) {

		MessageEmbed emb = event.getMessage().getEmbeds().get(0);
		event.getButton();
		Long dcid = Long.parseLong(emb.getFooter().getText());

		LiteSQL.onUpdate("UPDATE ha3users SET approved=1 WHERE dcId = ?", dcid);
		User u = Klassenserver7bbot.getInstance().getShardManager().getUserById(dcid);

		PrivateChannel pvtch = u.openPrivateChannel().complete();

		MessageEditBuilder messbuild = MessageEditBuilder.fromMessage(event.getMessage());
		messbuild.setActionRow(Button.success("inWhitelistInserted", "I whitelisted the person!"));

		try (MessageEditData messdata = messbuild.build()) {
			event.editMessage(messdata).queue();
		}

		log.info("Approved HA3 Request for UserId: " + dcid);

		if (pvtch != null) {

			MessageEmbed acceptmessage = new EmbedBuilder().setColor(Color.decode("#00ff00"))
					.setFooter("Handeled by K7Bot")
					.setDescription(
							"Congratulations, you were accepted for 'HA3'. You will recieve a message when your whitelist is active.")
					.setTimestamp(OffsetDateTime.now()).build();
			pvtch.sendMessageEmbeds(acceptmessage).queue();

		}
	}

	private void DenyHA3Request(ButtonInteractionEvent event) {
		MessageEmbed emb = event.getMessage().getEmbeds().get(0);
		Long dcid = Long.parseLong(emb.getFooter().getText());

		LiteSQL.onUpdate("UPDATE ha3users SET approved = 0 WHERE dcId = ?", dcid);
		User u = Klassenserver7bbot.getInstance().getShardManager().getUserById(dcid);

		PrivateChannel pvtch = u.openPrivateChannel().complete();

		event.getMessage().delete().queue();
		log.info("Denied HA3 Request for UserId: " + dcid);

		if (pvtch != null) {

			MessageEmbed denymessage = new EmbedBuilder().setColor(Color.red).setFooter("Handeled by K7Bot")
					.setDescription("Sorry, but you 'HA3' whitelistrequest has been denied")
					.setTimestamp(OffsetDateTime.now()).build();
			pvtch.sendMessageEmbeds(denymessage).queue();

		}

	}

	private void inWhitelistInserted(ButtonInteractionEvent event) {
		MessageEmbed emb = event.getMessage().getEmbeds().get(0);
		Long dcid = Long.parseLong(emb.getFooter().getText());

		LiteSQL.onUpdate("UPDATE ha3users SET approved = 2 WHERE dcId = ?", dcid);
		User u = Klassenserver7bbot.getInstance().getShardManager().getUserById(dcid);

		event.getMessage().delete().queue();

		log.info("Whitelisted UserId: " + dcid + " on the HA3 Server");

		PrivateChannel pvtch = u.openPrivateChannel().complete();

		if (pvtch != null) {

			MessageEmbed acceptmessage = new EmbedBuilder().setColor(Color.decode("#00ff00"))
					.setFooter("Handeled by K7Bot")
					.setDescription("Congratulations, You are now whitelisted on the 'HA3' Server")
					.setTimestamp(OffsetDateTime.now()).build();
			pvtch.sendMessageEmbeds(acceptmessage).queue();

		}
	}

}
