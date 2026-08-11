/* (C)2026 */
package de.klassenserver7b.k7bot.audio;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import de.klassenserver7b.k7bot.K7Bot;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class GuildAudioManager {

	private final TrackScheduler trackScheduler = new TrackScheduler(this);
	private final long guildId;
	private MessageChannel channel;

	public GuildAudioManager(long guildId) {
		this.guildId = guildId;
		this.channel = null;
	}

	public long getGuildId() {
		return guildId;
	}

	public void stop() {
		this.trackScheduler.queue.clear();

		this.getPlayer().ifPresent((player) -> player.setPaused(false).setTrack(null).subscribe(_ -> {
		}, e -> System.err.println("Lavalink operation failed: " + e.getMessage())));
	}

	public void setChannel(MessageChannel channel) {
		this.channel = channel;
	}

	public @Nullable MessageChannel getChannel() {
		return channel;
	}

	public Link getLink() {
		return K7Bot.getInstance().getLavalinkClient().getOrCreateLink(this.guildId);
	}

	public Optional<LavalinkPlayer> getPlayer() {
		return Optional.ofNullable(this.getLink().getCachedPlayer());
	}

	public LavalinkPlayer getOrCreatePlayer() {
		return this.getLink().getPlayer().block();
	}

	public TrackScheduler getTrackScheduler() {
		return trackScheduler;
	}
}
