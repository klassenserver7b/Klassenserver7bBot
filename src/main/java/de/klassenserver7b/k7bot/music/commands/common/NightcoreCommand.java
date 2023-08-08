/**
 * 
 */
package de.klassenserver7b.k7bot.music.commands.common;

import java.util.Collections;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

/**
 * 
 * @author K7
 *
 */
public class NightcoreCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "nightcore", "nc" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(channel.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();

		player.setFilterFactory((track, format, output) -> {
			TimescalePcmAudioFilter timescale = new TimescalePcmAudioFilter(output, format.channelCount,
					format.sampleRate);
			timescale.setRate(1.25);
			return Collections.singletonList(timescale);
		});
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
