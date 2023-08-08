package de.klassenserver7b.k7bot.music.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.music.utilities.BotAudioEffectsManager;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class EqualizerCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		return "COMING SOON - read Bot-News or GitHub Commit-Message";
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "eq" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

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
