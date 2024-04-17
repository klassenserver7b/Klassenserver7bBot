package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class TestCommand implements ServerCommand { 

 	private boolean isEnabled;

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "test" };
	}

	@Override
	public HelpCategories getCategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member caller, GuildMessageChannel channel, Message message) {
		//Test command is only used when I have something to test......
	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}
}

/**
 * MODAL TEST
 * 
 * TextInput posprompt = TextInput.create("posprompt", "positive prompt",
 * TextInputStyle.PARAGRAPH) .setMaxLength(75).setPlaceholder("insert your
 * positive prompt here").build(); TextInput negprompt =
 * TextInput.create("negprompt", "negative prompt", TextInputStyle.PARAGRAPH)
 * .setMaxLength(75).setPlaceholder("insert your negative prompt here").build();
 * TextInput steps = TextInput.create("steps", "sampling steps",
 * TextInputStyle.SHORT).setRequiredRange(0, 3) .setPlaceholder("Integer between
 * 1 and 150 - default 20").build();
 * 
 * Modal m = Modal.create("ai_prompt", "Stable-Diffusion prompt")
 * .addComponents(ActionRow.of(posprompt), ActionRow.of(negprompt),
 * ActionRow.of(steps)).build(); event.replyModal(m).queue();
 * 
 **/
