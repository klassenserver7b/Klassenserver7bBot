package de.k7bot.manage;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.buttons.types.K7Button;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonManager {
	public ConcurrentHashMap<String, K7Button> buttons;
	private final  Logger buttonlog = LoggerFactory.getLogger("ButtonLog");
	
	public ButtonManager() {
		
		this.buttons = new ConcurrentHashMap<>();
		
	}
	
	public boolean performButton(ButtonInteractionEvent e) {
		K7Button cmd;
		if ((cmd = this.buttons.get(e.getComponentId())) != null) {
			buttonlog.info(
					"ButtonEvent - see next lines:\n\nButton: "+e.getComponentId()+" | \nMember: " + e.getUser().getName() + " | \nGuild: " + e.getGuild().getName()
							+ " | \nChannel: " + e.getChannel().getName() + " | \nMessage: " + e.getMessage().getContentDisplay() + "\n");

			cmd.performButtonEvent(e);

			return true;
		}
		return false;
	}
}