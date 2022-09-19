
package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.SQL.LiteSQL;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import de.k7bot.util.SyntaxError;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class VolumeCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");
		try {
			MusicUtil.updateChannel(channel);
			if (args.length > 1) {
				int volume = Integer.parseInt(args[1]);
				if (volume > 0) {
					if (volume <= 100) {
						Guild guild = channel.getGuild();
						MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
								.getController(guild.getIdLong());
						AudioPlayer player = controller.getPlayer();
						player.setVolume(volume);
						LiteSQL.onUpdate("UPDATE botutil SET volume = " + volume
								+ " WHERE guildId = " + channel.getGuild().getIdLong());
						EmbedBuilder builder = new EmbedBuilder();
						builder.setFooter("Requested by @" + m.getEffectiveName());
						builder.setTimestamp(OffsetDateTime.now());
						builder.setTitle("Volume was set to " + volume);
						channel.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0])
								.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
					} else {

						channel.sendMessage(
								"Brudi was willst denn du? Wenn du taub werden willst oder übersteuerte Musik hören willst dann mach es aber lass mich in Ruhe :rage:")
								.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
					}
				} else {
					channel.sendMessage(
							"Brudi was willst den du? Wenn du nix hören willst dann lefte doch den channel oder gebe `"
									+ Klassenserver7bbot.INSTANCE.prefixl.get(channel.getGuild().getIdLong())
									+ "stop ` ein :rage:")
							.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
				}
			} else {
				SyntaxError.oncmdSyntaxError(channel, "volume [int]", m);
			}
		} catch (NumberFormatException e) {
			SyntaxError.oncmdSyntaxError(channel, "volume [int]", m);
		}
	}

	@Override
	public String gethelp() {
		String help = "Legt das Volume für den Bot auf diesem Server fest.\n - z.B. [prefix]volume [Zahl von 1 bis 100]";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
}
