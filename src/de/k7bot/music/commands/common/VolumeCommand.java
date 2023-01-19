
package de.k7bot.music.commands.common;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class VolumeCommand implements ServerCommand {

	@Override
	public String gethelp() {
		String help = "Legt das Volume für den Bot auf diesem Server fest.\n - z.B. [prefix]volume [Zahl von 1 bis 100]";
		return help;
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
		try {
			MusicUtil.updateChannel(channel);
			if (args.length > 1) {
				int volume = Integer.parseInt(args[1]);
				if (volume > 0) {
					if (volume <= 100) {
						Guild guild = channel.getGuild();
						MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
								.getController(guild.getIdLong());
						AudioPlayer player = controller.getPlayer();
						player.setVolume(volume);
						LiteSQL.onUpdate("UPDATE musicutil SET volume = ? WHERE guildId = ?;", volume,
								channel.getGuild().getIdLong());
						EmbedBuilder builder = new EmbedBuilder();
						builder.setFooter("Requested by @" + m.getEffectiveName());
						builder.setTimestamp(OffsetDateTime.now());
						builder.setTitle("Volume was set to " + volume);
						channel.sendMessageEmbeds(builder.build()).complete().delete().queueAfter(10L,
								TimeUnit.SECONDS);
					} else {

						channel.sendMessage(
								"Brudi was willst denn du? Wenn du taub werden willst oder übersteuerte Musik hören willst dann mach es aber lass mich in Ruhe :rage:")
								.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
					}
				} else {
					channel.sendMessage(
							"Brudi was willst den du? Wenn du nix hören willst dann lefte doch den channel oder gebe `"
									+ Klassenserver7bbot.getInstance().getPrefixMgr()
											.getPrefix(channel.getGuild().getIdLong())
									+ "stop ` ein :rage:")
							.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
				}
			} else {
				SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "volume [int]", m);
			}
		} catch (NumberFormatException e) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "volume [int]", m);
		}
	}
}
