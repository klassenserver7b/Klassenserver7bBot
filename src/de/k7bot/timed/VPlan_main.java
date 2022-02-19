package de.k7bot.timed;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class VPlan_main {

	public static List<JsonObject> finalentries = new ArrayList<>();
	public LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();
	Logger log = Klassenserver7bbot.INSTANCE.getMainLogger();

	public void sendvplanMessage(String cunext) {

		List<JsonObject> fien = finalplancheck(cunext);
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

		if (fien != null) {
			
			if(log !=null) {
			log.debug("send Vplanmessage (cunext = "+cunext+") with following hash: "+fien.hashCode()+" and devmode = "+Klassenserver7bbot.INSTANCE.indev);
			}
			
			StringBuilder builder = new StringBuilder();

			if (cunext.equalsIgnoreCase("next")) {
				builder.append("Es gibt einen neuen Vertretungsplan für den nächsten Schultag! \n");
			} else {
				builder.append("Es gibt einen neuen Vertretungsplan für Heute! \n");
			}
			if (fien.isEmpty()) {

				channel.sendMessage("**KEINE ÄNDERUNGEN 😭**").queue();

			} else {
				fien.forEach(entry -> {

					builder.append("Stunde: " + entry.get("lesson"));
					if (!entry.get("subject").toString().equalsIgnoreCase("\"---\"")) {

						builder.append(" Fach: " + entry.get("subject"));
						builder.append(" Lehrer: " + entry.get("teacher"));
						builder.append(" Raum: " + entry.get("room"));

						if (!(entry.get("changed").getAsJsonArray().size() == 0)) {
							builder.append(" Veränderung: " + entry.get("changed"));
						}

					} else {

						builder.append(" **AUSFALL**");

					}

					if (!entry.get("info").toString().equalsIgnoreCase("\"\"")) {
						builder.append(" Info: " + entry.get("info"));
					}
					builder.append("\n");
				});
				channel.sendMessage(builder.toString().trim()).queue();
			}

			lsql.onUpdate("UPDATE vplan" + cunext + " SET classeintraege = " + fien.hashCode());

		}
	}

	public List<JsonObject> finalplancheck(String cunext) {
		Integer dbh = null;
		JsonObject plan = getPlan(cunext);	
		boolean synced = false;
		
		if(cunext.equalsIgnoreCase("next")) {
			
			synced = synchronizePlanDB(plan);
			
		}

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
									.replaceAll("\\(B-Woche\\)", "").replaceAll("\\(A-Woche\\)", "").replaceAll(",", "")
									.replaceAll("Montag", "").replaceAll("Dienstag", "").replaceAll("Mittwoch", "")
									.replaceAll("Donnerstag", "").replaceAll("Freitag", "").toLowerCase()
							+ "', " + finalentries.hashCode() + ")");
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return finalentries;
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

			ResultSet next = lsql.onQuery("SELECT zieldatum FROM vplannext");
			try {
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
		List<JsonObject> classentries = new ArrayList<JsonObject>();
		if (obj != null) {
			JsonArray arr = obj.get("body").getAsJsonArray();
			arr.forEach(element -> {
				String elem = element.getAsJsonObject().get("class").toString();
				if (elem.equalsIgnoreCase("\"9b\"") || elem.equalsIgnoreCase("\"9b,9c\"")|| elem.equalsIgnoreCase("Manos")) {

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
				new UsernamePasswordCredentials("manos", "Man18Vplan"));
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