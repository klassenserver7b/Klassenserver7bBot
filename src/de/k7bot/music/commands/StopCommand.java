package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class StopCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {

				MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
						.getController(vc.getGuild().getIdLong());
				AudioManager manager = vc.getGuild().getAudioManager();
				AudioPlayer player = controller.getPlayer();

				Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);
				player.stopTrack();
				manager.closeAudioConnection();
			}
		}
	}

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
}
