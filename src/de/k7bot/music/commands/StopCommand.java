package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopCommand implements ServerCommand {
	
	@Override
	public String gethelp() {
		String help = "Stopt den aktuellen Track und der Bot verlässt den VoiceChannel.";
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

		MusicController controller = Klassenserver7bbot.INSTANCE.playerManager.getController(vc.getGuild().getIdLong());
		AudioManager manager = vc.getGuild().getAudioManager();
		AudioPlayer player = controller.getPlayer();

		MusicUtil.updateChannel(channel);
		player.stopTrack();
		manager.closeAudioConnection();

	}

}
