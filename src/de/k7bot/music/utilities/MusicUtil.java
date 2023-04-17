package de.k7bot.music.utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;

/**
 *
 * @author Klassenserver7b
 *
 */
public class MusicUtil {

	private static final Logger log = LoggerFactory.getLogger(MusicUtil.class);

	/**
	 *
	 * @param channel
	 */
	public static void updateChannel(TextChannel channel) {

		ResultSet set = LiteSQL.onQuery("SELECT * FROM musicutil WHERE guildId = ?;", channel.getGuild().getIdLong());

		try {
			if (set.next()) {
				LiteSQL.onUpdate("UPDATE musicutil SET channelId = ? WHERE guildId = ?;", channel.getIdLong(),
						channel.getGuild().getIdLong());
			} else {
				LiteSQL.onUpdate("INSERT INTO musicutil(guildId, channelId, volume) VALUES(?, ?, ?);",
						channel.getGuild().getIdLong(), channel.getIdLong(), 10);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param hook
	 */
	public static void updateChannel(InteractionHook hook) {

		TextChannel channel = (TextChannel) hook.getInteraction().getMessageChannel();

		updateChannel(channel);
	}

	/**
	 *
	 * @param guildid
	 * @param builder
	 */
	public static void sendEmbed(long guildid, EmbedBuilder builder) {
		ResultSet set = LiteSQL.onQuery("SELECT channelId FROM musicutil WHERE guildId = ?", guildid);

		try {
			if (set.next()) {
				long channelid = set.getLong("channelId");
				Guild guild;
				if ((guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid)) != null) {
					TextChannel channel;
					if ((channel = guild.getTextChannelById(channelid)) != null) {
						channel.sendMessageEmbeds(builder.build()).queue();
					}
				}

			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param guildid
	 * @param builder
	 */
	public static void sendEmbed(long guildid, EmbedBuilder builder, FileUpload upload) {
		ResultSet set = LiteSQL.onQuery("SELECT channelId FROM musicutil WHERE guildId = ?", guildid);

		try {
			if (set.next()) {
				long channelid = set.getLong("channelId");
				Guild guild;
				if ((guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid)) != null) {
					TextChannel channel;
					if ((channel = guild.getTextChannelById(channelid)) != null) {
						builder.setImage("attachment://thumbnail.png");
						channel.sendFiles(upload).setEmbeds(builder.build()).queue();
					}
				}

			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param m
	 * @return
	 */
	public static boolean membHasVcConnection(Member m) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			if (state.getChannel() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param m
	 * @return
	 */
	public static AudioChannel getMembVcConnection(Member m) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {
				return vc;
			}
		}
		return null;
	}

	/**
	 *
	 * @param ac
	 * @return
	 */
	public static int isConnectedtoChannel(AudioChannel ac) {
		AudioManager audioman = ac.getGuild().getAudioManager();

		if (audioman.getConnectedChannel() == null) {
			return 2;
		}

		if ((ac.getIdLong() == audioman.getConnectedChannel().getIdLong())) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 *
	 * @param sendHandler
	 * @param m
	 * @return
	 */
	public static boolean checkDefaultConditions(GenericMessageSendHandler sendHandler, Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			sendHandler.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		return checkChannelConnection(sendHandler, m);
	}

	/**
	 *
	 * @param sendHandler
	 * @param m
	 * @return
	 */
	public static boolean checkConditions(GenericMessageSendHandler sendHandler, Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			sendHandler.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		return checkChannelConnection(sendHandler, m);
	}

	protected static boolean checkChannelConnection(GenericMessageSendHandler sendHandler, Member m) {

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		switch (MusicUtil.isConnectedtoChannel(vc)) {
		case 0: {
			sendHandler.sendMessage("You are not connected to the music playing VoiceChannel" + m.getAsMention())
					.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		case 2: {

			sendHandler.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention())
					.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		}

		return true;

	}

	/**
	 *
	 * @param m
	 * @return
	 */
	public static boolean checkConditions(Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			return false;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		switch (MusicUtil.isConnectedtoChannel(vc)) {
		case 0: {
			return false;
		}
		case 2: {
			return false;
		}
		}

		return true;
	}

	/**
	 *
	 * @param c
	 * @param m
	 * @return
	 */
	public static boolean isPlayingSong(TextChannel c, Member m) {

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(c.getGuild().getIdLong());

		if (controller == null) {
			c.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		AudioPlayer player = controller.getPlayer();

		if (player.getPlayingTrack() == null) {
			c.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		return true;

	}
}