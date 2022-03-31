package de.k7bot.timed;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Color;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.manage.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class VPlan_main {

	public LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();
	Logger log = Klassenserver7bbot.INSTANCE.getMainLogger();

	public void sendvplanMessage(String cunext) {

		ConcurrentHashMap<List<JsonObject>, String> input = finalplancheck(cunext);
		Guild guild;
		TextChannel channel;

		if (!Klassenserver7bbot.INSTANCE.indev) {
			guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(779024287733776454l);

			if (cunext.equalsIgnoreCase("next")) {
				channel = guild.getTextChannelById(918904387739459645l);
			} else {
				channel = guild.getTextChannelById(931287317774221322l);
			}
		} else {
			guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(850697874147770368l);
			channel = guild.getTextChannelById(920777920681738390l);
		}

		if (input != null) {

			List<JsonObject> fien = input.keys().nextElement();

			String info = input.values().toString();

			info = info.replaceAll("\"", "").replaceAll("\\[", "").replaceAll("\\]", "").trim();

			if (log != null) {
				log.debug("sending Vplanmessage (cunext = " + cunext + ") with following hash: " + fien.hashCode()
						+ " and devmode = " + Klassenserver7bbot.INSTANCE.indev);
			}

			StringBuilder builder = new StringBuilder();
			EmbedBuilder embbuild = new EmbedBuilder();

			if (cunext.equalsIgnoreCase("next")) {
				embbuild.setTitle("Es gibt einen neuen Vertretungsplan für den nächsten Schultag! \n");
			} else {
				embbuild.setTitle("Es gibt einen neuen Vertretungsplan für Heute! \n");
			}
			if (fien.isEmpty()) {

				embbuild.setTitle("**KEINE ÄNDERUNGEN 😭**");

			} else {

				fien.forEach(entry -> {

					JsonArray changes = entry.get("changed").getAsJsonArray();
					String subjectchange = "";
					String teacherchange = "";
					String roomchange = "";

					if (changes.size() != 0) {

						if (changes.toString().contains("subject")) {
							subjectchange = "**";
						}
						if (changes.toString().contains("teacher")) {
							teacherchange = "**";
						}
						if (changes.toString().contains("room")) {
							roomchange = "**";
						}

					}

					builder.append("Stunde: " + entry.get("lesson").getAsString().replaceAll("\"", ""));

					if (!entry.get("subject").toString().equalsIgnoreCase("\"---\"")) {

						builder.append(" | Fach: " + subjectchange
								+ entry.get("subject").getAsString().replaceAll("\"", "") + subjectchange);

						builder.append(" | Lehrer: " + teacherchange
								+ entry.get("teacher").getAsString().replaceAll("\"", "") + teacherchange);

						builder.append(" | Raum: " + roomchange + entry.get("room").getAsString().replaceAll("\"", "")
								+ roomchange);

					} else {

						builder.append(" | **AUSFALL**");

					}

					if (!entry.get("info").toString().equalsIgnoreCase("\"\"")) {

						builder.append(" |  Info: " + entry.get("info").getAsString().replaceAll("\"", ""));

					}

					builder.append("\n");

				});

				embbuild.addField("Änderungen", builder.toString().trim(), false);

				if (!(info.equalsIgnoreCase(""))) {

					embbuild.addField("Sonstige Infos", info, false);

				}

			}

			embbuild.setColor(Color.decode("#038aff"));
			embbuild.setFooter("Stand vom " + OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

			channel.sendMessageEmbeds(embbuild.build()).queue();

			lsql.onUpdate("UPDATE vplan" + cunext + " SET classeintraege = " + fien.hashCode());

		}
	}

	public ConcurrentHashMap<List<JsonObject>, String> finalplancheck(String cunext) {

		Integer dbh = null;
		List<JsonObject> finalentries = new ArrayList<>();
		JsonObject plan = getPlan(cunext);

		if (plan != null) {
			String info = plan.get("info").toString();
			boolean synced = false;

			if (cunext.equalsIgnoreCase("next")) {

				synced = synchronizePlanDB(plan);

			}

			ConcurrentHashMap<List<JsonObject>, String> fien = new ConcurrentHashMap<>();

			List<JsonObject> getC = getyourC(plan);
			if (getC != null) {
				int h = getC.hashCode();

				ResultSet set = lsql.onQuery("SELECT classeintraege FROM vplan" + cunext);
				try {
					if (set.next()) {

						dbh = set.getInt("classeintraege");

					}
					if (dbh != null) {
						if (dbh != h || synced) {

							finalentries = getC;

							lsql.onUpdate("UPDATE vplan" + cunext + " SET zieldatum = '"
									+ plan.get("head").getAsJsonObject().get("title").getAsString().replaceAll(" ", "")
											.replaceAll("\\(B-Woche\\)", "").replaceAll("\\(A-Woche\\)", "")
											.replaceAll(",", "").replaceAll("Montag", "").replaceAll("Dienstag", "")
											.replaceAll("Mittwoch", "").replaceAll("Donnerstag", "")
											.replaceAll("Freitag", "").toLowerCase()
									+ "'");

						} else {
							return null;

						}
					} else {
						finalentries = getC;
						lsql.onUpdate("INSERT INTO vplan" + cunext + "(zieldatum, classeintraege) VALUES('"
								+ plan.get("head").getAsJsonObject().get("title").getAsString().replaceAll(" ", "")
										.replaceAll("\\(B-Woche\\)", "").replaceAll("\\(A-Woche\\)", "")
										.replaceAll(",", "").replaceAll("Montag", "").replaceAll("Dienstag", "")
										.replaceAll("Mittwoch", "").replaceAll("Donnerstag", "")
										.replaceAll("Freitag", "").toLowerCase()
								+ "', " + finalentries.hashCode() + ")");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

				fien.put(finalentries, info);
				return fien;

			} else {
				return null;

			}
		} else {
			return null;
		}
	}

	public boolean synchronizePlanDB(JsonObject plan) {
		if (plan != null) {
			String dbdate = "";

			String onlinedate = plan.get("head").getAsJsonObject().get("title").getAsString().replaceAll(" ", "")
					.replaceAll("\\(B-Woche\\)", "").replaceAll("\\(A-Woche\\)", "").replaceAll(",", "")
					.replaceAll("Montag", "").replaceAll("Dienstag", "").replaceAll("Mittwoch", "")
					.replaceAll("Donnerstag", "").replaceAll("Freitag", "").toLowerCase();
			OffsetDateTime time = OffsetDateTime.now();
			String realdate = "" + time.getDayOfMonth() + "."
					+ time.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN).toLowerCase() + time.getYear();

			try {
				ResultSet next = lsql.onQuery("SELECT zieldatum FROM vplannext");
				if (next.next()) {

					dbdate = next.getString("zieldatum");
				}

				if (!(dbdate.equalsIgnoreCase(onlinedate)) && dbdate.equalsIgnoreCase(realdate)) {

					lsql.getdblog().info("Plan-DB-Sync");

					ResultSet old = lsql.onQuery("SELECT * FROM vplannext");

					if (old.next()) {
						lsql.onUpdate("UPDATE vplancurrent SET zieldatum = '" + old.getString("zieldatum")
								+ "', classeintraege = '" + old.getInt("classeintraege") + "'");
						lsql.onUpdate("UPDATE vplannext SET zieldatum = '', classeintraege = ''");
					}
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return false;
		}

	}

	public List<JsonObject> getyourC(JsonObject obj) {
		List<JsonObject> classentries = new ArrayList<>();
		if (obj != null) {
			JsonArray arr = obj.get("body").getAsJsonArray();
			arr.forEach(element -> {
				String elem = element.getAsJsonObject().get("class").toString().replaceAll("\"", "");
				if (elem.equalsIgnoreCase("9b") || elem.equalsIgnoreCase("9b,9c") || elem.equalsIgnoreCase("9a-9c/ Spw")) {

					classentries.add(element.getAsJsonObject());

				}

			});
			return classentries;
		} else {
			return null;

		}

	}

	public JsonObject getPlan(String cunext) {

		final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("manos-dresden.de", 443),
				new UsernamePasswordCredentials("manos", Klassenserver7bbot.INSTANCE.getVplanpw()));
		try (final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.build()) {
			final HttpGet httpget = new HttpGet("https://manos-dresden.de/vplan/upload/" + cunext + "/students.json");
			final CloseableHttpResponse response = httpclient.execute(httpget);
			if (response.getStatusLine().getStatusCode() == 200) {
				JsonElement elem = JsonParser.parseString(EntityUtils.toString(response.getEntity()));
				return elem.getAsJsonObject();
			} else {
				return null;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

}