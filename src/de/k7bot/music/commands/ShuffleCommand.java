package de.k7bot.music.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class ShuffleCommand implements ServerCommand {
	
	@Override
	public String gethelp() {
		String help = "Spielt die aktuelle Playlist in zufälliger Reihenfolge.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
	
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (!MusicUtil.checkConditions(channel, m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.INSTANCE.getPlayerUtil().getController(vc.getGuild().getIdLong());
		controller.getQueue().shuffle();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setDescription("playlist shuffled");
		builder.setColor(10827773);
		channel.sendMessageEmbeds(builder.build()).complete().addReaction(Emoji.fromUnicode("U+1F500")).queue();
	}

}