/**
 * 
 */
package de.k7bot.commands.common;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * @author Klassenserver7b
 *
 */
public class DanielCommand implements ServerCommand {

	private final Logger log;

	/**
	 * 
	 */
	public DanielCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public String gethelp() {
		
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		
		return null;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		int x;
		int y;
		int zahl;

		try {
			String[] writtenMessage = message.getContentDisplay().split(" ");
			x = Integer.valueOf(writtenMessage[1]);
			y = Integer.valueOf(writtenMessage[2]);
			zahl = Integer.valueOf(writtenMessage[3]);
		} catch (Exception e) {
			channel.sendMessage(
					"Schreibe deinen customized Zahlenbereich in folgendem Format in diesen Kanal: \"min max\"\n")
					.queue();
			return;
		}

		Random randomNumber = new Random();
		int randomNum = randomNumber.nextInt((y - x) + 1) + x;
		log.info("Der Bot hat als random Zahl die " + randomNum + " gewählt.\n");
		if (zahl == randomNum) {
			channel.sendMessage("Deine Wahl war RICHTIG! :thumbsup:").queue();
			return;
		}
		channel.sendMessage("Deine Wahl war leider Falsch. :sob:").queue();

	}

}
