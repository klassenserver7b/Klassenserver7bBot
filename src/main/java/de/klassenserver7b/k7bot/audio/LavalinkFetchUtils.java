/* (C)2026 */
package de.klassenserver7b.k7bot.audio;

import java.util.function.Consumer;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.io.CloseMode;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.manage.LavaLinkManager;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import dev.arbjerg.lavalink.client.LavalinkNode;
import net.dv8tion.jda.api.entities.MessageEmbed;

public abstract class LavalinkFetchUtils {

	private static final Logger log = LoggerFactory.getLogger(LavalinkFetchUtils.class);

	public static String getBaseHttpUri(LavalinkNode node) {
		String baseUri = node.getBaseUri();
		if (baseUri.startsWith("wss://"))
			return baseUri.replaceFirst("wss://", "https://");
		else if (baseUri.startsWith("ws://"))
			// noinspection HttpUrlsUsage
			return baseUri.replaceFirst("ws://", "http://");
		else
			return baseUri;
	}

	public static boolean validateNode(LavalinkNode node, long guildId, Consumer<MessageEmbed> sender) {
		if (node == null) {
			sender.accept(EmbedUtils.getErrorEmbed("No active Lavalink node found", guildId).build());
			return false;
		}
		return true;
	}

	@Nullable
	public static String validateSession(LavalinkNode node, long guildId, Consumer<MessageEmbed> sender) {
		String sessionId = LavaLinkManager.SESSION_IDS.get(node.getName());
		if (sessionId == null) {
			sender.accept(EmbedUtils.getErrorEmbed("Lavalink session not ready.", guildId).build());
			return null;
		}
		return sessionId;
	}

	@Nullable
	public static <T> T executeRequest(LavalinkNode node, String uri, HttpClientResponseHandler<T> responseHandler,
			String requestDescription) {

		try (CloseableHttpClient httpClient = HttpClients.createSystem()) {

			BasicClassicHttpRequest request = new BasicClassicHttpRequest("GET", uri);
			request.setHeader("Authorization", node.getPassword());

			return httpClient.execute(request, responseHandler);
		} catch (Exception e) {
			log.error("{}} request failed {}", requestDescription, e.getMessage(), e);
			return null;
		}
	}

	public static void executeRequestAsync(LavalinkNode node, String uri, long guildId, Consumer<MessageEmbed> sender,
			Consumer<SimpleHttpResponse> responseHandler, String requestDescription) {

		try {
			CloseableHttpAsyncClient httpClient = HttpAsyncClients.createSystem();
			httpClient.start();

			SimpleHttpRequest request = SimpleHttpRequest.create("GET", uri);
			request.setHeader("Authorization", node.getPassword());

			httpClient.execute(request, new FutureCallback<>() {
				@Override
				public void completed(SimpleHttpResponse response) {
					responseHandler.accept(response);
					httpClient.close(CloseMode.GRACEFUL);
				}

				@Override
				public void failed(Exception ex) {
					log.error("Exception fetching {}", requestDescription, ex);
					sender.accept(EmbedUtils
							.getErrorEmbed("Error fetching " + requestDescription + ": " + ex.getMessage(), guildId)
							.build());
					httpClient.close(CloseMode.GRACEFUL);
				}

				@Override
				public void cancelled() {
					log.warn("{} request cancelled", requestDescription);
					httpClient.close(CloseMode.GRACEFUL);
				}
			});
		} catch (Exception e) {
			log.error("{}} request failed {}", requestDescription, e.getMessage(), e);
		}

	}

}
