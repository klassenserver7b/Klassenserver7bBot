package de.k7bot.music.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.BotEqualizer;
import de.k7bot.music.MusicUtil;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class EqualizerCommand implements ServerCommand{

	@Override
	public String gethelp() {
		return "COMING SOON - read Bot-News or GitHub Commit-Message";
	}

	@Override
	public String getcategory() {
		return "Musik";
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		if(!MusicUtil.checkConditions(channel, m)) {
			return;
		}
		
		String[] args = message.getContentDisplay().split(" ");

		if (args.length < 2) {
			SyntaxError.oncmdSyntaxError(channel, "eq [id]", m);
			return;
		}

		BotEqualizer eq = BotEqualizer.getEQ(Klassenserver7bbot.INSTANCE.playerManager.getController(m.getGuild().getIdLong()).getPlayer());
		eq.setEQMode(Integer.valueOf(args[1]));

	}

}
