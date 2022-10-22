package de.k7bot.music.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SkipBackCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return "Spult zur um die gewählte Anzahl an Sekunden zurück.\n - z.B. [prefix]back [time in seconds]";
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}

		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 2) {
			SyntaxError.oncmdSyntaxError(channel, "seek [position in seconds]", m);
			return;
		}

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(m.getGuild().getIdLong());
		int pos = Integer.valueOf(args[1]);
		controller.back(pos * 1000);

	}

}
