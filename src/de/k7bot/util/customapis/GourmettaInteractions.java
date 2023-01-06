/**
 * 
 */
package de.k7bot.util.customapis;

import java.awt.Color;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.subscriptions.types.SubscriptionTarget;
import de.k7bot.util.customapis.types.InternalAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * @author Klassenserver7b
 *
 */
public class GourmettaInteractions implements InternalAPI {

	String token;
	String userid;

	boolean apienabled;

	Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 */
	public GourmettaInteractions() {
		apienabled = false;
		token = null;
		userid = null;
	}

	/**
	 * Login for the Gourmetta Rest-API based on the credentials given
	 * 
	 * @return If the Login was successful
	 */
	public void login() {

		String username = Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("gourmettauserid");
		String password = Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("gourmettapassword");

		if (username == null || password == null || username.isBlank() || password.isBlank()) {
			return;
		}

		final CloseableHttpClient httpclient = HttpClients.createSystem();

		final HttpPost post = new HttpPost("https://bestellung-rest.gourmetta.de/login");

		EntityBuilder entitybuild = EntityBuilder.create();
		entitybuild.setContentType(ContentType.APPLICATION_JSON);

		JsonObject auth = new JsonObject();
		auth.addProperty("login", username);
		auth.addProperty("password", password);
		entitybuild.setText(auth.toString());

		try {

			post.setEntity(entitybuild.build());
			CloseableHttpResponse response = httpclient.execute(post);

			if (response.getCode() != 200) {
				log.warn("Invalid response from bestellung-rest.gourmetta.de");
				return;
			}

			String content = EntityUtils.toString(response.getEntity());

			response.close();
			httpclient.close();

			JsonElement elem = JsonParser.parseString(content);

			this.token = elem.getAsJsonObject().get("token").getAsString();

			String base64uid = token.split("\\.")[1];
			String uidjson = new String(Base64.getDecoder().decode(base64uid));
			this.userid = JsonParser.parseString(uidjson).getAsJsonObject().get("userUuid").getAsString();
			this.apienabled = true;

		} catch (IOException | ParseException e) {
			log.error(e.getMessage(), e);

			try {
				httpclient.close();
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}

		}
	}

	/**
	 * Disconnects from the Gourmetta API
	 */
	public void logout() {
		this.token = null;
		this.userid = null;
		this.apienabled = false;
	}

	/**
	 * Checks if there is a new Plan to provide and provides it
	 */
	public void checkforUpdates() {

		login();
		if (!isApiEnabled()) {
			return;
		}

		log.debug("Gourmetta Check");

		ResultSet set = LiteSQL.onQuery("Select * FROM gourmettaInteractions");

		OffsetDateTime offsetprovideDay = getNextDay();
		long provideday = Long.parseLong(offsetprovideDay.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

		try {

			if (set.next()) {

				long dbday = set.getLong("lastday");

				if (dbday != provideday) {

					MessageCreateData data = buildMessage(getStripedDayMeals(), offsetprovideDay);

					if (data != null) {
						providePlanMessage(data);
					}

					LiteSQL.onUpdate("UPDATE gourmettaInteractions SET lastday=?", provideday);
				}

			} else {
				MessageCreateData data = buildMessage(getStripedDayMeals(), offsetprovideDay);

				if (data == null) {
					return;
				}

				providePlanMessage(data);
				LiteSQL.onUpdate("INSERT INTO gourmettaInteractions(lastday) VALUES(?)", provideday);

			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return;
		}

		logout();

	}

	/**
	 * Parses the given meals and returns the as an usable Discord
	 * {@link MessageCreateData}
	 * 
	 * @param stripedDayMeals {@link JsonArray} obtained by
	 *                        {@link GourmettaInteractions#getStripedDayMeals()}
	 * @param provideDay      The day for which the plan is as an
	 *                        {@link OffsetDateTime}
	 * @return The {@link MessageCreateData} which can be used in
	 *         {@link GourmettaInteractions#providePlanMessage(MessageCreateData)}
	 */
	private MessageCreateData buildMessage(JsonArray stripedDayMeals, OffsetDateTime provideDay) {

		if (stripedDayMeals == null || stripedDayMeals.isEmpty()) {
			return null;
		}

		MessageCreateBuilder builder = new MessageCreateBuilder();
		EmbedBuilder embbuild = new EmbedBuilder();

		StringBuilder descriptionbuild = new StringBuilder();

		stripedDayMeals.forEach(json -> {

			JsonObject meal = json.getAsJsonObject();

			descriptionbuild.append("**");
			descriptionbuild.append(meal.get("number").getAsString());
			descriptionbuild.append("** (");
			descriptionbuild.append(meal.get("price").getAsString());
			descriptionbuild.append("€) - **");
			descriptionbuild.append(StringEscapeUtils.unescapeJson(meal.get("name").getAsString()));
			descriptionbuild.append("**\n");
			descriptionbuild.append("_");
			descriptionbuild.append(StringEscapeUtils.unescapeJson(meal.get("description").getAsString()));
			descriptionbuild.append("_");
			descriptionbuild.append("\n\n");

		});

		embbuild.setDescription(descriptionbuild.toString().trim());
		embbuild.setColor(Color.decode("#038aff"));
		embbuild.setTitle("Gourmetta-Plan für den " + provideDay.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		embbuild.setFooter("Provided by @K7Bot");
		embbuild.setTimestamp(OffsetDateTime.now());

		builder.setEmbeds(embbuild.build());
		return builder.build();
	}

	/**
	 * 
	 * @param data The pregenerated {@link MessageCreateData} which is used to build
	 *             the {@link Message} - usually obtained by
	 *             {@link GourmettaInteractions#buildMessage(JsonArray)}
	 */
	private void providePlanMessage(MessageCreateData data) {
		Klassenserver7bbot.getInstance().getSubscriptionManager()
				.provideSubscriptionNotification(SubscriptionTarget.GOURMETTA, data);
	}

	/**
	 * Reduces the meal-array to the necessary information
	 * 
	 * @return The stripped meals as a {@link JsonArray}
	 */
	private JsonArray getStripedDayMeals() {

		if (!isApiEnabled()) {
			return null;
		}

		JsonArray meals = getMealsforDayofWeek(DayOfWeek.of(getNextDay().getDayOfWeek().getValue()));

		if (meals == null || meals.isEmpty()) {
			return null;
		}

		JsonArray ret = new JsonArray();

		for (JsonElement json : meals) {

			JsonObject mealobj = json.getAsJsonObject().get("meal").getAsJsonObject();
			JsonObject retobj = new JsonObject();

			if (mealobj.get("categoryShortName").getAsString().matches("M\\d")) {

				JsonElement number = mealobj.get("categoryShortName");
				JsonElement name = mealobj.get("name");
				JsonElement description = mealobj.get("description");
				JsonElement price = mealobj.get("price");

				retobj.addProperty("number", (number.isJsonNull() ? "" : number.getAsString()));
				retobj.addProperty("name", (name.isJsonNull() ? "" : name.getAsString()));
				retobj.addProperty("description", (description.isJsonNull() ? "" : description.getAsString()));
				retobj.addProperty("price", (price.isJsonNull() ? "" : price.getAsString()));
				ret.add(retobj);
			}

		}

		return ret;

	}

	/**
	 * Used to get all meals for a {@link DayOfWeek}
	 * 
	 * @param day The {@link DayOfWeek} you want the meals for
	 * @return The meals forthe selected {@link DayOfWeek} as a {@link JsonArray}
	 */
	private JsonArray getMealsforDayofWeek(DayOfWeek day) {

		if (!isApiEnabled() || day == null) {
			return null;
		}

		JsonArray days = getWeekPlan();

		if (days == null) {
			return null;
		}

		if (day.getValue() >= days.size()) {
			return null;
		}

		JsonObject jsonday = days.get(day.getValue() - 1).getAsJsonObject();

		return jsonday.get("orderedMeals").getAsJsonArray();
	}

	/**
	 * Requests the Plan of the current week from the REST-API and returns it as a
	 * {@link JsonArray} <br>
	 * If the current day is Saturday or Sunday the used week is the next week!
	 * 
	 * @return The days with the meals as an {@link JsonArray}
	 */
	private JsonArray getWeekPlan() {

		if (!isApiEnabled()) {
			return null;
		}

		String url = generateOfferRequestURI();

		final CloseableHttpClient httpclient = HttpClients.createSystem();

		final HttpGet httpget = new HttpGet(url);

		httpget.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.token);
		httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

		try (final CloseableHttpResponse response = httpclient.execute(httpget)){

			if (response.getCode() != 200) {
				log.warn("Invalid response from bestellung-rest.gourmetta.de");
				return null;
			}

			JsonElement elem = JsonParser.parseString(EntityUtils.toString(response.getEntity()));

			response.close();
			httpclient.close();

			return elem.getAsJsonObject().get("orderDays").getAsJsonArray();

		} catch (IOException | JsonSyntaxException | ParseException e) {
			log.error(e.getMessage(), e);

			try {
				httpclient.close();
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			}

		}

		return null;

	}

	/**
	 * Builds the offer request
	 * 
	 * @return the url for the current week offer request
	 */
	private String generateOfferRequestURI() {

		if (!isApiEnabled()) {
			return null;
		}

		StringBuilder requestbuilder = new StringBuilder();
		requestbuilder.append("https://bestellung-rest.gourmetta.de/users/");
		requestbuilder.append(userid);
		requestbuilder.append("/order?from=");
		requestbuilder.append(getFirstDayofWeek().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		requestbuilder.append("&to=");
		requestbuilder.append(getLastDayofWeek().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

		return requestbuilder.toString();
	}

	/**
	 * Returns the Monday of the current week <br>
	 * If the current day is Saturday or Sunday the used week is the next week!
	 * 
	 * @return The Monday of the current week as an {@link OffsetDateTime}
	 */
	private OffsetDateTime getFirstDayofWeek() {

		OffsetDateTime current = OffsetDateTime.now();
		int day = current.getDayOfWeek().getValue();

		if (day <= 5) {
			return current.minusDays(day - 1);
		} else {
			return current.plusDays(8 - day);
		}

	}

	/**
	 * Returns the Friday of the current week <br>
	 * If the current day is Saturday or Sunday the used week is the next week!
	 * 
	 * @return The Friday of the current week as an {@link OffsetDateTime}
	 */
	private OffsetDateTime getLastDayofWeek() {

		OffsetDateTime current = OffsetDateTime.now();
		int day = current.getDayOfWeek().getValue();

		if (day == 5) {
			return current;
		}

		if (day < 5) {
			return current.plusDays(5 - day);
		} else {
			return current.plusDays(12 - day);
		}

	}

	/**
	 * 
	 * @return
	 */
	private OffsetDateTime getNextDay() {

		OffsetDateTime cutime = OffsetDateTime.now();

		if (cutime.getHour() <= 14) {
			return cutime;
		}

		if (cutime.getDayOfWeek().getValue() == 6 || cutime.getDayOfWeek().getValue() == 5) {
			return cutime;
		}

		int day = cutime.getDayOfWeek().getValue();

		if (day >= 5) {
			return cutime.plusDays(8 - day);
		} else {
			return cutime.plusDays(1);
		}

	}

	/**
	 * Checks if the Api is enabled and warns if not
	 * 
	 * @return Whether this Api is currently enabled or not
	 */
	private boolean isApiEnabled() {

		if (!apienabled) {
			log.warn("API not enabled! - Enable it by using 'login()'");
		}

		return apienabled;
	}

	@Override
	public void shutdown() {
		logout();

	}

}
