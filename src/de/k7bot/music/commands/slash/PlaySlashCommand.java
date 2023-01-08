/**
 * 
 */
package de.k7bot.music.commands.slash;

import java.awt.Color;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.AudioPlayerUtil;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.music.TrackScheduler;
import de.k7bot.music.utilities.AudioLoadOption;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.PredefinedMusicPlaylists;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.SupportedPlayQueries;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * @author Klassenserver7b
 *
 */
public class PlaySlashCommand implements SlashCommand {

	/**
	 * 
	 */
	private final Logger log;

	/**
	 * 
	 */
	public PlaySlashCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * 
	 */
	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		Member m = event.getMember();

		if (!MusicUtil.membHasVcConnection(m)) {
			event.reply("You are not connected to the music playing VoiceChannel" + m.getAsMention()).queue();
			return;
		}

		InteractionHook hook = event.deferReply().complete();

		MusicUtil.updateChannel(hook);

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());
		AudioManager manager = vc.getGuild().getAudioManager();
		AudioPlayerManager apm = Klassenserver7bbot.getInstance().getAudioPlayerManager();
		AudioPlayer player = controller.getPlayer();
		Queue queue = controller.getQueue();

		if (player.getPlayingTrack() == null) {

			queue.clearQueue();
			manager.openAudioConnection(vc);

		} else {

			queue.clearQueue();
		}

		TrackScheduler.next = true;
		player.stopTrack();
		TrackScheduler.next = false;

		log.info("PlayslashCommand currentsong = " + (player.getPlayingTrack() != null));

		setVolume(player, event.getGuild().getIdLong());

		switch (event.getFullCommandName()) {

		case "play predefined" -> {

			hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#00ff00"))
					.setDescription("Started playing "
							+ PredefinedMusicPlaylists.fromId(event.getOption("playlist").getAsInt()).toString())
					.build()).complete().delete().queueAfter(10L, TimeUnit.SECONDS);

			playPredefinedPlaylist(PredefinedMusicPlaylists.fromId(event.getOption("playlist").getAsInt()), hook, vc,
					queue, apm, controller);

		}
		case "play queried" -> {

			hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#00ff00"))
					.setDescription("Started playing " + event.getOption("url").getAsString()).build()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);

			playQueriedItem(SupportedPlayQueries.fromId(event.getOption("target").getAsInt()), vc,
					event.getOption("url").getAsString(), hook, apm, controller);

		}

		}

	}

	/**
	 * 
	 * @param playlist
	 * @param channel
	 * @param queue
	 * @param apm
	 * @param controller
	 */
	private void playPredefinedPlaylist(PredefinedMusicPlaylists playlist, InteractionHook hook, AudioChannel channel,
			Queue queue, AudioPlayerManager apm, MusicController controller) {

		try {
			log.info("Bot startet searching a track: -> new Track(channelName = " + channel.getName() + ", playlist = "
					+ playlist.toString() + ")");
			apm.loadItem(playlist.getUrl(),
					new AudioLoadResult(controller, playlist.getUrl(), AudioLoadOption.REPLACE));
		} catch (FriendlyException e) {
			log.error(e.getMessage(), e);
		}

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
			InteractionHook hook, AudioPlayerManager apm, MusicController controller) {

		log.info("Bot startet searching a track -> new Track(channelName = " + channel.getName() + ", type = "
				+ querytype.toString() + ", url = " + query + ")");

		String suffix = querytype.getSearchSuffix();
		String url = suffix + " " + query;
		url = url.trim();

		try {
			apm.loadItem(url, new AudioLoadResult(controller, url, AudioLoadOption.REPLACE));
		} catch (FriendlyException e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * 
	 * @param player
	 * @param guildId
	 */
	private void setVolume(AudioPlayer player, Long guildId) {

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

	/**
	 * 
	 */
	@Override
	public SlashCommandData getCommandData() {

		/**
		 * First SubCommand
		 */

		ArrayList<Choice> targets = new ArrayList<>();
		for (SupportedPlayQueries q : SupportedPlayQueries.values()) {
			targets.add(new Choice(q.toString(), q.getId()));
		}
		OptionData target = new OptionData(OptionType.INTEGER, "target", "from where the song should be loaded")
				.addChoices(targets).setRequired(true);

		OptionData url = new OptionData(OptionType.STRING, "url", "The url/search query for the selected target")
				.setRequired(true);

		SubcommandData queried = new SubcommandData("queried",
				"for all play request with a url / query -> not predefined").addOptions(target, url);

		/**
		 * Second SubCommand
		 */

		ArrayList<Choice> playlists = new ArrayList<>();
		for (PredefinedMusicPlaylists q : PredefinedMusicPlaylists.values()) {
			playlists.add(new Choice(q.toString(), q.getId()));
		}
		OptionData playlist = new OptionData(OptionType.INTEGER, "playlist", "a predefined playlist")
				.addChoices(playlists).setRequired(true);

		SubcommandData predefined = new SubcommandData("predefined", "for our predefined playlists")
				.addOptions(playlist);

		return Commands.slash("play", "Plays the submitted Track / Livestream / Playlist")
				.addSubcommands(queried, predefined)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_CONNECT))
				.setDescriptionLocalization(DiscordLocale.GERMAN,
						"Spielt den/die ausgewählte/-n Track / Livestream / Playlist")
				.setGuildOnly(true);
	}

}
