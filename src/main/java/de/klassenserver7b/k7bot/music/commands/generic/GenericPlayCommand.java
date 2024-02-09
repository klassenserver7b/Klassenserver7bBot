/**
 *
 */
package de.klassenserver7b.k7bot.music.commands.generic;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.music.asms.ExtendedLocalAudioSourceManager;
import de.klassenserver7b.k7bot.music.asms.SpotifyAudioSourceManager;
import de.klassenserver7b.k7bot.music.lavaplayer.AudioLoadResult;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import de.klassenserver7b.k7bot.music.utilities.AudioLoadOption;
import de.klassenserver7b.k7bot.music.utilities.AudioPlayerUtil;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.SupportedPlayQueries;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author K7
 *
 */
public abstract class GenericPlayCommand implements ServerCommand, TopLevelSlashCommand {

	private final Logger log;
	private final AudioPlayerManager apm;
	private final String SUPPORTED_AUDIO_FORMATS = "(mp4|mp3|wav|ogg|m4a)";

	/**
	 *
	 */
	public GenericPlayCommand() {
		this.log = LoggerFactory.getLogger(this.getClass());

		apm = new DefaultAudioPlayerManager();
		apm.registerSourceManager(new SpotifyAudioSourceManager());
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

		if (!performInternalChecks(m, vc, new GenericMessageSendHandler(hook))) {
			return;
		}

		MusicUtil.updateChannel(hook);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());

		playQueriedItem(SupportedPlayQueries.fromId(event.getOption("target").getAsInt()), vc,
				event.getOption("url").getAsString(), controller);

		hook.sendMessage("Successfully Loaded").queue();

	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");

		if (args.length <= 1 && message.getAttachments().size() <= 0) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), gethelp(), m);
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		if (!performInternalChecks(m, vc, new GenericMessageSendHandler(channel))) {
			return;
		}
		MusicUtil.updateChannel(channel);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());

		StringBuilder strBuilder = new StringBuilder();

		if (message.getAttachments().size() > 0) {
			int status = loadAttachments(message.getAttachments(), controller, gethelp());

			if (status == 0) {
				return;
			}

			channel.sendMessage(
					"Invalid file attached to this message! - allowed are ." + SUPPORTED_AUDIO_FORMATS + " files")
					.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			return;
		}

		for (int i = 1; i < args.length; i++) {
			strBuilder.append(args[i]);
			strBuilder.append(" ");
		}

		String url = strBuilder.toString().trim();

		loadURL(url, controller, vc.getName());

	}

	/**
	 * 
	 * @param querytype
	 * @param channel
	 * @param query
	 * @param controller
	 */
	private void playQueriedItem(SupportedPlayQueries querytype, AudioChannel channel, String query,
			MusicController controller) {

		String suffix = querytype.getSearchSuffix();
		String url = suffix + " " + query;
		url = url.trim();

		loadURL(url, controller, channel.getName());

	}

	protected int loadURL(String url, MusicController controller, String vcname) {
		url = formatQuerry(url);

		log.info("Bot startet searching a track: no current track -> new Track(channelName = " + vcname + ", url = "
				+ url + ")");

		try {
			apm.loadItem(url, generateAudioLoadResult(controller, url));
			return 0;
		} catch (FriendlyException e) {
			log.error(e.getMessage(), e);
			return 1;
		}
	}

	protected int loadAttachments(List<Attachment> attachments, MusicController controller, String vcname) {

		boolean err = false;
		for (int i = 0; i < attachments.size(); i++) {

			try (Attachment song = attachments.get(i)) {
				if (!song.getFileExtension().matches(SUPPORTED_AUDIO_FORMATS)) {
					err = true;
					continue;
				}

				AudioLoadResult alr = generateAudioLoadResult(controller, song.getProxyUrl());

				if (i != 0) {
					alr.setLoadoption(AudioLoadOption.APPEND);
				}

				apm.loadItem(song.getProxyUrl(), alr);
			}
		}

		if (err) {
			return 1;

		}
		return 0;

	}

	protected boolean tryLoad(String identifyer, AudioLoadResult ares, AudioPlayerManager apm) {
		try {
			apm.loadItem(identifyer, ares);
			return true;
		} catch (FriendlyException e) {
			return false;
		}
	}

	protected boolean performInternalChecks(Member m, AudioChannel vc, GenericMessageSendHandler sendHandler) {

		if (!MusicUtil.checkDefaultConditions(sendHandler, m)) {
			return false;
		}

		AudioManager manager = vc.getGuild().getAudioManager();

		if (!manager.isConnected()) {
			manager.openAudioConnection(vc);
		}

		return true;
	}

	protected void setVolume(AudioPlayer player, Long guildId) {

		try (ResultSet set = LiteSQL.onQuery("SELECT volume FROM musicutil WHERE guildId = ?;", guildId)) {

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
				LiteSQL.onUpdate("INSERT OR REPLACE INTO musicutil(guildId) VALUES(?);", guildId);
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
	public String[] getCommandStrings() {
		return null;
	}

	@NotNull
	@Override
	public SlashCommandData getCommandData() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void disableCommand() {
		// Nothing to do here
	}

	@Override
	public void enableCommand() {
		// Nothing to do here
	}

	@Override
	abstract public String gethelp();

	abstract protected AudioLoadResult generateAudioLoadResult(MusicController controller, String url);

	abstract protected GenericPlayCommand getChildClass();

}
