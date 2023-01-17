/**
 *
 */
package de.k7bot.music.commands.generic;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.AudioPlayerUtil;
import de.k7bot.music.MusicController;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.spotify.SpotifyAudioSourceManager;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * @author Felix
 *
 */
public abstract class GenericPlayCommand implements ServerCommand, SlashCommand {

	private final Logger log;
	private final AudioPlayerManager apm;

	/**
	 *
	 */
	public GenericPlayCommand() {
		this.log = LoggerFactory.getLogger(this.getClass());

		apm = new DefaultAudioPlayerManager();
		apm.registerSourceManager(new SpotifyAudioSourceManager());
		AudioSourceManagers.registerRemoteSources(apm);
		AudioSourceManagers.registerLocalSource(apm);
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	protected boolean tryLoad(String identifyer, AudioLoadResult ares, AudioPlayerManager apm) {
		try {
			apm.loadItem(identifyer, ares);
			return true;
		} catch (FriendlyException e) {
			return false;
		}
	}

	private String formatQuerry(String q) {

		String url = q;

		if (url.startsWith("lf: ")) {

			url = url.substring(4);

		} else if (!(url.startsWith("http") || url.startsWith("scsearch: ") || url.startsWith("ytsearch: "))) {
			url = "ytsearch: " + url;
		}

		return url;
	}

	protected void setVolume(AudioPlayer player, Long guildId) {

		ResultSet set = LiteSQL.onQuery("SELECT volume FROM musicutil WHERE guildId = ?;", guildId);

		try {
			if (set.next()) {
				int volume = set.getInt("volume");
				if (volume != 0) {
					player.setVolume(volume);
				} else {
					LiteSQL.onUpdate("UPDATE musicutil SET volume = ? WHERE guildId = ?;",
							AudioPlayerUtil.STANDARDVOLUME, guildId);
					player.setVolume(AudioPlayerUtil.STANDARDVOLUME);
				}
			} else {
				LiteSQL.onUpdate("INSERT INTO musicutil(volume, guildId) VALUES(?,?);", AudioPlayerUtil.STANDARDVOLUME,
						guildId);
				player.setVolume(AudioPlayerUtil.STANDARDVOLUME);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(false).complete();
		Member m = event.getMember();

		if (event.getOptions().isEmpty()) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(hook), gethelp(), m);
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());

		performInternalChecks(m, vc, controller, new GenericMessageSendHandler(hook));

		performItemLoad("", controller, vc.getName());

	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");

		if (args.length <= 1) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), gethelp(), m);
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());

		performInternalChecks(m, vc, controller, new GenericMessageSendHandler(channel));

		StringBuilder strBuilder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			strBuilder.append(args[i]);
			strBuilder.append(" ");
		}

		String url = strBuilder.toString().trim();

		performItemLoad(url, controller, vc.getName());

	}

	public boolean performInternalChecks(Member m, AudioChannel vc, MusicController controller,
			GenericMessageSendHandler sendHandler) {

		if (!MusicUtil.checkDefaultConditions(sendHandler, m)) {
			return false;
		}

		AudioManager manager = vc.getGuild().getAudioManager();

		if (!manager.isConnected() || controller.getPlayer().getPlayingTrack() == null) {

//				sendHandler.sendMessageEmbeds(new EmbedBuilder().setFooter("requested by @" + m.getEffectiveName())
//						.setTitle("Invalid Command Usage").setColor(Color.decode("#ff0000"))
//						.setDescription(
//								"The Bot isn't connected to a voicechannel / isn't playing a Song!\nPLEASE USE `"
//										+ Klassenserver7bbot.getInstance().getPrefixMgr()
//												.getPrefix(vc.getGuild().getIdLong())
//										+ "play` INSTEAD!")
//						.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);
//
//				return false;


			manager.openAudioConnection(vc);

		}

		return true;
	}

	protected void performItemLoad(String url, MusicController controller, String vcname) {
		url = formatQuerry(url);

		log.info("Bot startet searching a track: no current track -> new Track(channelName = " + vcname + ", url = "
				+ url + ")");

		try {
			apm.loadItem(url, generateAudioLoadResult(controller, url));
		} catch (FriendlyException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public SlashCommandData getCommandData() {
		return null;
	}

	@Override
	abstract public String gethelp();

	abstract protected AudioLoadResult generateAudioLoadResult(MusicController controller, String url);

	abstract protected GenericPlayCommand getChildClass();

}
