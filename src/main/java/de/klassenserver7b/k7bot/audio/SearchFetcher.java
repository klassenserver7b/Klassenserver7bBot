/* (C)2026 */
package de.klassenserver7b.k7bot.audio;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.klassenserver7b.k7bot.util.EmbedUtils;
import dev.arbjerg.lavalink.client.LavalinkNode;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class SearchFetcher {
	private static final Logger log = LoggerFactory.getLogger(SearchFetcher.class);

	public static @Nullable String searchTrack(LavalinkNode node, long guildId, Consumer<MessageEmbed> sender,
			String query, String searchTypes) {

		if (!LavalinkFetchUtils.validateNode(node, guildId, sender)) {
			return null;
		}

		String uri = LavalinkFetchUtils.getBaseHttpUri(node) + "/v4/loadsearch";
		uri += "?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
		uri += "&types=" + URLEncoder.encode(searchTypes, StandardCharsets.UTF_8);

		log.info("Searching track: {}", uri);

		return LavalinkFetchUtils.executeRequest(node, uri, (response) -> handleResponse(response, guildId, sender),
				"search");
	}

	private static @Nullable String handleResponse(ClassicHttpResponse response, long guildId,
			Consumer<MessageEmbed> sender) {
		int statusCode = response.getCode();
		String responseBody;
		try {
			responseBody = EntityUtils.toString(response.getEntity());
		} catch (ParseException | IOException ex) {
			return null;
		}

		if (statusCode == HttpStatus.SC_OK) {
			try {
				JsonObject obj = JsonParser.parseString(responseBody).getAsJsonObject();
				JsonArray tracks = obj.getAsJsonArray("tracks");

				if (!tracks.isEmpty()) {
					return tracks.get(0).getAsJsonObject().get("info").getAsJsonObject().get("uri").getAsString();
				}

			} catch (Exception e) {
				log.warn(e.getMessage(), e);
				sender.accept(EmbedUtils.getErrorEmbed("Error executing search.", guildId).build());
			}
		} else if (statusCode == HttpStatus.SC_NO_CONTENT) {
			String msg = "No results found for this query";
			sender.accept(EmbedUtils.getErrorEmbed(msg, guildId).build());
		} else {
			String msg = "Error on executing search query. status: " + statusCode;
			sender.accept(EmbedUtils.getErrorEmbed(msg, guildId).build());
		}
		return null;
	}
}
