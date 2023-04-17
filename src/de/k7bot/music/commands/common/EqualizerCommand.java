package de.k7bot.music.commands.common;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.utilities.BotAudioEffectsManager;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EqualizerCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return "COMING SOON - read Bot-News or GitHub Commit-Message";
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 2) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "eq [id]", m);
			return;
		}

		BotAudioEffectsManager eq = BotAudioEffectsManager.getAudioEffectsManager(
				Klassenserver7bbot.getInstance().getPlayerUtil().getController(m.getGuild().getIdLong()).getPlayer());
		eq.setEQMode(Integer.valueOf(args[1]));

	}

}
