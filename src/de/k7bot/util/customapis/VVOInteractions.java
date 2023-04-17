package de.k7bot.util.customapis;

import java.awt.Color;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.subscriptions.types.SubscriptionTarget;
import de.k7bot.util.InternalStatusCodes;
import de.k7bot.util.customapis.types.LoopedEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class VVOInteractions implements LoopedEvent {

	private final Logger log;

	public VVOInteractions() {
		log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public int checkforUpdates() {

		JsonElement plan = downloadPlan(LocalDateTime.now());
		if (plan == null || plan.isJsonNull()) {
			return InternalStatusCodes.FAILURE;
		}

		List<String> lines = getAllFieldsFromData(plan.getAsJsonObject());

		if (lines == null || lines.isEmpty()) {
			return InternalStatusCodes.FAILURE;
		}

		MessageCreateData data = getMessage(getEmbed(lines));

		if (data == null) {
			return InternalStatusCodes.FAILURE;
		}

		Klassenserver7bbot.getInstance().getSubscriptionManager()
				.provideSubscriptionNotification(SubscriptionTarget.DVB, data);

		return InternalStatusCodes.SUCCESS;

	}

	private MessageCreateData getMessage(MessageEmbed embed) {
		MessageCreateBuilder dataBuild = new MessageCreateBuilder();
		dataBuild.setEmbeds(embed);
		return dataBuild.build();
	}

	private MessageEmbed getEmbed(List<String> fields) {
		EmbedBuilder embedBuilder = new EmbedBuilder();
		embedBuilder.setTitle("Die nächsten Abfahrten an der Mosenstraße");
		embedBuilder.setFooter("Created by @K7Bot");
		embedBuilder.setColor(Color.decode("#2fff00"));
		embedBuilder.setTimestamp(OffsetDateTime.now());

		StringBuilder builder = new StringBuilder();

		for (String f : fields) {
			builder.append(f);
			builder.append("\n\n");
		}

		embedBuilder.setDescription(builder.toString().trim());
		return embedBuilder.build();
	}

	private String getLineStringFromData(JsonElement departure) {
		JsonObject departobj = departure.getAsJsonObject();
		String lineName = departobj.get("LineName").getAsString();
		String direction = departobj.get("Direction").getAsString();

		Entry<String, Long> times = getDepartureTime(departobj);

		if (times == null) {
			return null;
		}

		String departureTime = times.getKey();
		Long delay = times.getValue();

		String delaystr = "";

		if (delay > 0) {
			delaystr = "(+" + delay + ")";
		} else if (delay < 0) {
			delaystr = "(-" + delay + ")";
		}

		String lineString = lineName + " " + direction + " | " + departureTime + " " + delaystr;

		return lineString;
	}

	private List<String> getAllFieldsFromData(JsonObject object) {
		if (object == null || object.isJsonNull()) {
			return null;
		}
		JsonArray departures = object.get("Departures").getAsJsonArray();
		ArrayList<String> ret = new ArrayList<>();
		for (JsonElement depart : departures) {
			String f = getLineStringFromData(depart);
			if (f == null) {
				ret.add(f);
			}
		}
		return ret;
	}

	private Entry<String, Long> getDepartureTime(JsonObject depart) {

		if (depart == null || depart.isJsonNull()) {
			return null;
		}

		Pattern timePattern = Pattern.compile("\\d+");

		JsonElement realTimeElem = depart.get("RealTime");
		JsonElement sheduledTimeElem = depart.get("ScheduledTime");

		if (realTimeElem == null || realTimeElem.isJsonNull()) {
			return null;
		}

		Matcher departureMatcher = timePattern.matcher(realTimeElem.getAsString());
		Matcher delayMatcher = timePattern.matcher(
				((sheduledTimeElem == null || sheduledTimeElem.isJsonNull()) ? "" : sheduledTimeElem.getAsString()));

		String departure = "";
		Long delay = 0L;

		if (!departureMatcher.find()) {
			return null;
		}

		LocalDateTime theoreticdeparTime = LocalDateTime.ofEpochSecond(Long.valueOf(departureMatcher.group()) / 1000, 0,
				ZoneOffset.ofHours(1));
		Duration timediff = Duration.between(LocalDateTime.now(), theoreticdeparTime);
		long departureTime = timediff.getSeconds() / 60;
		departure = theoreticdeparTime.toLocalTime().toString();

		if (delayMatcher.find()) {
			LocalDateTime delaytime = LocalDateTime.ofEpochSecond(Long.valueOf(delayMatcher.group()) / 1000, 0,
					ZoneOffset.ofHours(1));
			Duration delaydur = Duration.between(LocalDateTime.now(), delaytime);
			long delaycheck = delaydur.getSeconds() / 60;
			if (delaycheck != departureTime) {
				delay = departureTime - delaycheck;

			}
		}

		return Map.entry(departure, delay);
	}

	public JsonElement downloadPlan(LocalDateTime time) {
		HttpPost request = new HttpPost("https://webapi.vvo-online.de/dm");
		JsonObject requestData = new JsonObject();
		requestData.addProperty("format", "json");
		requestData.addProperty("stopid", 33000063);
		requestData.addProperty("time", time.toString());
		requestData.addProperty("isarrival", false);
		requestData.addProperty("limit", 12);
		requestData.addProperty("shorttermchanges", true);
		requestData.addProperty("mentzonly", false);
		EntityBuilder entityBuilder = EntityBuilder.create();
		entityBuilder.setContentType(ContentType.APPLICATION_JSON);
		entityBuilder.setText(requestData.toString());
		request.setEntity(entityBuilder.build());
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			String content = httpClient.execute(request, new BasicHttpClientResponseHandler());
			return new JsonParser().parse(content);

		} catch (IOException | JsonParseException e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public boolean restart() {
		// Nothing to do here
		return true;
	}

	@Override
	public void shutdown() {
		// Nothing to do here
	}

	@Override
	public boolean isAvailable() {

		HttpGet request = new HttpGet("https://webapi.vvo-online.de/");

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			httpClient.execute(request, new BasicHttpClientResponseHandler());

		} catch (IOException e) {
			return false;
		}

		return true;
	}

	@Override
	public String getIdentifier() {
		return "vvo";
	}
}