package de.k7bot.music.utilities;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.MusicController;
import de.k7bot.sql.LiteSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * 
 * @author Felix
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
	 * @param channel
	 */
	public static void updateChannel(InteractionHook hook) {

		TextChannel channel = (TextChannel) hook.getInteraction().getMessageChannel();

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

	public static boolean membHasVcConnection(Member m) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			if (state.getChannel() != null) {
				return true;
			}
		}
		return false;
	}

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

	public static boolean checkConditions(TextChannel c, Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			c.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
			return false;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		switch (MusicUtil.isConnectedtoChannel(vc)) {
		case 0: {
			c.sendMessage("You are not connected to the music playing VoiceChannel" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		case 2: {
			c.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		}

		return true;
	}

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

	public static boolean checkConditions(InteractionHook channel, Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
			return false;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		switch (MusicUtil.isConnectedtoChannel(vc)) {
		case 0: {
			channel.sendMessage("You are not connected to the music playing VoiceChannel" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		case 2: {
			channel.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		}

		return true;
	}

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
}