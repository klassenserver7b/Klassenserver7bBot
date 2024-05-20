package de.klassenserver7b.k7bot.music.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.music.utilities.AudioLoadOption;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class AudioLoadResult implements AudioLoadResultHandler {

    private final String uri;
    private final MusicController controller;
    private AudioLoadOption loadoption;

    public AudioLoadResult(MusicController controller, String uri, AudioLoadOption loadoption) {
        this.uri = uri;
        this.controller = controller;
        this.loadoption = loadoption;
    }

    @Override
    public void trackLoaded(AudioTrack track) {

        Queue queue = this.controller.getQueue();
        Klassenserver7bbot.getInstance().getMainLogger().info("Bot AudioLoadResult loaded a single track");
        addTrackToqueue(queue, track);

        EmbedBuilder builder = EmbedUtils.getBuilderOf(Color.decode("#4D05E8"), controller.getGuild().getIdLong())
                .setTitle("1 track added to queue");

        MusicUtil.sendEmbed(this.controller.getGuild().getIdLong(), builder);

    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {

        Queue queue = this.controller.getQueue();

        Klassenserver7bbot.getInstance().getMainLogger().info("Bot AudioLoadResult loaded a playlist");
        if (!playlist.getTracks().isEmpty()) {
            if (this.uri.startsWith("ytsearch: ")) {
                Klassenserver7bbot.getInstance().getMainLogger().debug("url starts with ytsearch:");

                // ytsearch liefert Liste an vorgeschlagenen Videos - nur das erste wird zur
                // Queue hinzugefügt
                AudioTrack track = playlist.getTracks().getFirst();
                addTrackToqueue(queue, track);
                return;
            }

            if (this.uri.startsWith("scsearch: ")) {
                Klassenserver7bbot.getInstance().getMainLogger().debug("url starts with scsearch:");

                // scsearch liefert Liste an vorgeschlagenen Videos - nur das erste wird zur
                // Queue hinzugefügt
                AudioTrack track = playlist.getTracks().getFirst();
                addTrackToqueue(queue, track);
                return;
            }

            int added = playlist.getTracks().size();

            addPlaylistToQueue(queue, playlist);

            EmbedBuilder builder = EmbedUtils.getBuilderOf(Color.decode("#4D05E8"), controller.getGuild().getIdLong())
                    .setTitle(added + " tracks added to queue");

            MusicUtil.sendEmbed(this.controller.getGuild().getIdLong(), builder);

        } else {
            noMatches();
        }
    }

    private void addPlaylistToQueue(Queue queue, AudioPlaylist playlist) {

        switch (loadoption) {
            case APPEND -> queue.addPlaylistToQueue(playlist);
            case NEXT -> queue.setNextPlaylist(playlist);
            case REPLACE -> queue.replace(playlist);
        }
    }

    private void addTrackToqueue(Queue queue, AudioTrack track) {

        switch (loadoption) {
            case APPEND -> queue.addTrackToQueue(track);
            case NEXT -> queue.setNextTrack(track);
            case REPLACE -> queue.replace(track);
        }

    }

    @Override
    public void noMatches() {
        Klassenserver7bbot.getInstance().getMainLogger()
                .info("Bot AudioLoadResult couldn't find a matching audio track (uri={})", uri);
        EmbedBuilder builder = EmbedUtils.getErrorEmbed("Couldn't find the Song you Searched for! :sob:",
                controller.getGuild().getIdLong());
        MusicUtil.sendEmbed(this.controller.getGuild().getIdLong(), builder);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        Klassenserver7bbot.getInstance().getMainLogger().info("Bot AudioLoadResult failed to load the requested item. - error: {}", exception.getLocalizedMessage());
        EmbedBuilder builder = EmbedUtils.getErrorEmbed(exception.getMessage(), controller.getGuild().getIdLong());
        MusicUtil.sendEmbed(this.controller.getGuild().getIdLong(), builder);
    }

    /**
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @return the controller
     */
    public MusicController getController() {
        return controller;
    }

    /**
     * @return the loadoption
     */
    public AudioLoadOption getLoadoption() {
        return loadoption;
    }

    /**
     * @param loadoption the loadoption to set
     */
    public void setLoadoption(AudioLoadOption loadoption) {
        this.loadoption = loadoption;
    }
}