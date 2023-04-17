package de.k7bot.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TestCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

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
