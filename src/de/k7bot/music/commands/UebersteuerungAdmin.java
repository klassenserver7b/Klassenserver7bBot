package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.util.SyntaxError;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class UebersteuerungAdmin implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		if (m.hasPermission(Permission.ADMINISTRATOR)) {
			message.delete().queue();
			String[] args = message.getContentDisplay().split(" ");
			try {
				int volume = Integer.parseInt(args[1]);
				Klassenserver7bbot.INSTANCE.getMusicUtil().updateChannel(channel);
				if (args.length > 1) {
					Guild guild = channel.getGuild();
					MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
							.getController(guild.getIdLong());
					AudioPlayer player = controller.getPlayer();
					player.setVolume(volume);
					Klassenserver7bbot.INSTANCE.getDB()
							.onUpdate("UPDATE botutil SET volume = " + volume + " WHERE id = " + '\001');
					EmbedBuilder builder = new EmbedBuilder();
					builder.setFooter("Requested by @" + m.getEffectiveName());
					builder.setTimestamp(OffsetDateTime.now());
					builder.setTitle("Volume was set to " + volume);
					channel.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0])
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
