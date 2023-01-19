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
import de.k7bot.commands.types.TopLevelSlashCommand;
import de.k7bot.music.asms.ExtendedLocalAudioSourceManager;
import de.k7bot.music.lavaplayer.AudioLoadResult;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.utilities.AudioPlayerUtil;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.SupportedPlayQueries;
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
public abstract class GenericPlayCommand implements ServerCommand, TopLevelSlashCommand {

	private final Logger log;
	private final AudioPlayerManager apm;

	/**
	 *
	 */
	public GenericPlayCommand() {
		this.log = LoggerFactory.getLogger(this.getClass());

		apm = new DefaultAudioPlayerManager();
		apm.registerSourceManager(new ExtendedLocalAudioSourceManager());
		AudioSourceManagers.registerRemoteSources(apm);
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(true).complete();
		Member m = event.getMember();

		if (event.getOptions().isEmpty()) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(hook), gethelp(), m);
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());

		performInternalChecks(m, vc, controller, new GenericMessageSendHandler(hook));

		playQueriedItem(SupportedPlayQueries.fromId(event.getOption("target").getAsInt()), vc,
				event.getOption("url").getAsString(), controller);

		hook.sendMessage("Successfully Loaded").queue();

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

	/**
	 *
	 * @param querytype
	 * @param channel
	 * @param query
	 * @param hook
	 * @param apm
	 * @param controller
	 */
	private void playQueriedItem(SupportedPlayQueries querytype, AudioChannel channel, String query,
			MusicController controller) {

		String suffix = querytype.getSearchSuffix();
		String url = suffix + " " + query;
		url = url.trim();

		performItemLoad(url, controller, channel.getName());

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

	protected boolean tryLoad(String identifyer, AudioLoadResult ares, AudioPlayerManager apm) {
		try {
			apm.loadItem(identifyer, ares);
			return true;
		} catch (FriendlyException e) {
			return false;
		}
	}

	protected boolean performInternalChecks(Member m, AudioChannel vc, MusicController controller,
			GenericMessageSendHandler sendHandler) {

		if (!MusicUtil.checkDefaultConditions(sendHandler, m)) {
			return false;
		}

		AudioManager manager = vc.getGuild().getAudioManager();

		if (!manager.isConnected() || controller.getPlayer().getPlayingTrack() == null) {

			manager.openAudioConnection(vc);

		}

		return true;
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

	private String formatQuerry(String q) {

		String url = q;

		if (url.startsWith("lf: ")) {

			url = url.substring(4);

		} else if (!(url.startsWith("http") || url.startsWith("scsearch: ") || url.startsWith("ytsearch: "))) {
			url = "ytsearch: " + url;
		}

		return url;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
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
