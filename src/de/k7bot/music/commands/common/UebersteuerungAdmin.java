package de.k7bot.music.commands.common;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.errorhandler.SyntaxError;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class UebersteuerungAdmin implements ServerCommand {
	
	@Override
	public String gethelp() {
		String help = null;
		return help;
	}

	@Override
	public String getcategory() {
		String category = null;
		return category;
	}
	
	public void performCommand(Member m, TextChannel channel, Message message) {
		if (m.hasPermission(Permission.ADMINISTRATOR)) {
			
			String[] args = message.getContentDisplay().split(" ");
			try {
				int volume = Integer.parseInt(args[1]);
				MusicUtil.updateChannel(channel);
				if (args.length > 1) {
					Guild guild = channel.getGuild();
					MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
							.getController(guild.getIdLong());
					AudioPlayer player = controller.getPlayer();
					player.setVolume(volume);
					EmbedBuilder builder = new EmbedBuilder();
					builder.setFooter("Requested by @" + m.getEffectiveName());
					builder.setTimestamp(OffsetDateTime.now());
					builder.setTitle("Volume was set to " + volume);
					channel.sendMessageEmbeds(builder.build())
							.complete().delete().queueAfter(10L, TimeUnit.SECONDS);

				} else {
					SyntaxError.oncmdSyntaxError(channel, "volume [int]", m);
				}
			} catch (NumberFormatException e) {
				SyntaxError.oncmdSyntaxError(channel, "volume [int]", m);
			}
		}
	}
	
}
