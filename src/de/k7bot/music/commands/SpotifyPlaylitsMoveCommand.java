package de.k7bot.music.commands;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.core5.http.ParseException;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.requests.data.playlists.AddItemsToPlaylistRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistsItemsRequest;

public class SpotifyPlaylitsMoveCommand implements ServerCommand{

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String getcategory() {
		return null;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		channel.sendTyping().queue();

		String[] args = message.getContentDisplay().split(" ");
		if (args.length < 4) {

			SyntaxError.oncmdSyntaxError(channel, "test [quell id] [ziel id] [offset]", m);
			return;

		}

		List<String> uris = new ArrayList<>();

		final SpotifyApi spotifyapi = new SpotifyApi.Builder().setClientId("0971d").setAccessToken(
				"BQAF2PJVq20qX7T17nYjo0b6oW35iEtMMvekuU0_dgsHVSWWuo3GktgxxWQ1tB_YPWXuGsQPrhQ6xaitfYpzGWxO5Jnk4ARnrttUpq5Ucf6uUt2-vRUZ1zgzdBi_VnCI3KoGvbnyfBTQpUyXOqcO3iOk1YkOChtt02zeaHdhdZaH2VboMDsl5NAzZrh32G4tCtKNRqkXA8i4UbZZoy9otG1juTeHeX5SlFLb7P9vZvCuE41uXX70-orUkcP_6pPmQwvBJM7mQ77b40nxPYijB0lmavMTMH49bJk49LJKfGeZuom0mPrGXxoeyv7Gtr_BVggy6ZE_Kv2Hhp4yXSXT8k-vEA\",\"accessTokenExpirationTimestampMs")
				.build();

		GetPlaylistsItemsRequest getplaylistitemsrequest = spotifyapi.getPlaylistsItems(args[1]).limit(50)
				.offset(Integer.valueOf(args[3])).build();

		PlaylistTrack[] songs = new PlaylistTrack[0];
		
		try {

			Paging<PlaylistTrack> tracks = getplaylistitemsrequest.execute();
			songs = tracks.getItems();

		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < songs.length - 1; i++) {

			uris.add(songs[i].getTrack().getUri());

		}

		uris.forEach(uri -> {
			System.out.println(uri);
		});

		AddItemsToPlaylistRequest submit = spotifyapi.addItemsToPlaylist(args[2], uris.toArray(new String[0])).build();

		try {
			submit.execute();
			System.out.println("ende");

			channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#4d05e8"))
					.setTimestamp(LocalDateTime.now()).setDescription("Copied " + songs.length + " songs").build())
					.queue();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}

		
	}
	
	

}
