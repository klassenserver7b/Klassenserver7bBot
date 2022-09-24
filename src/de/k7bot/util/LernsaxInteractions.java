package de.k7bot.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.sql.LiteSQL;
import de.konsl.webweaverapi.WebWeaverClient;
import de.konsl.webweaverapi.model.auth.Credentials;
import de.konsl.webweaverapi.model.messages.Message;
import de.konsl.webweaverapi.model.messages.MessageType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * 
 * @author Felix
 *
 */
public class LernsaxInteractions {
	WebWeaverClient client;
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * 
	 * @param lsaxemail
	 * @param token
	 * @param applicationID
	 */
	public void connect(String lsaxemail, String token, String applicationID) {

		this.client = new WebWeaverClient();
		Credentials cred = new Credentials(lsaxemail, token, applicationID);
		client.login(cred);

	}

	/**
	 * 
	 */
	public void disconnect() {
		client.logout();
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Message> checkForLernplanMessages() throws SQLException {

		List<Message> messages;

		ResultSet set = LiteSQL.onQuery("Select LernplanId from lernsaxinteractions");

		String currentMessageID = null;

		if (set.next()) {
			currentMessageID = set.getString("LernplanId");
		}

		if (currentMessageID == null) {
			messages = client.getMessagesScope().getMessages();
			LiteSQL.onUpdate("INSERT INTO lernsaxinteractions(LernplanId) VALUES('"
					+ messages.get(messages.size() - 1).getId() + "');");
		} else {
			messages = client.getMessagesScope().getMessages(Integer.parseInt(currentMessageID));
			if (messages.size() > 0)
				messages = messages.stream().skip(1).toList();
			LiteSQL.onUpdate(
					"UPDATE lernsaxinteractions SET LernplanId='" + messages.get(messages.size() - 1).getId() + "';");
		}

		if (messages.size() <= 0) {
			return messages;
		}

		messages = messages.stream().filter(msg -> msg.getType() == MessageType.NEW_LEARNING_PLAN).toList();

		return messages;
	}

	/**
	 * 
	 * @param lernplanmessages
	 */
	public void sendLernsaxEmbeds(List<Message> lernplanmessages) {

		if (lernplanmessages.size() > 0) {

			log.info("Sending " + lernplanmessages.size() + " new Lernplan-Embeds");
			ArrayList<MessageEmbed> embeds = new ArrayList<MessageEmbed>();

			lernplanmessages.forEach(msg -> {

				String linkURLQuery = null;
				try {
					linkURLQuery = URLEncoder.encode(
							"learning_plan|" + msg.getFromGroup().getLogin() + "|" + msg.getData() + "|/", "UTF-8");
				} catch (UnsupportedEncodingException ignored) {
				}

				embeds.add(new EmbedBuilder()
						.setTitle("Ein neuer Lernplan wurde bereitgestellt!",
								"https://" + client.getRemoteHost() + "/l.php?" + linkURLQuery)
						.addField("Name", msg.getData(), false)
						.addField("Klasse / Gruppe", msg.getFromGroup().getName(), false)
						.addField("Autor", msg.getFromUser().getName(), false).build());

			});

//			for (MessageEmbed e : embeds) {
//
//			}

		}

	}

}
