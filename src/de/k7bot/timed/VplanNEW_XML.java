/**
 * 
 */
package de.k7bot.timed;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.api.client.util.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.Cell;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.TableMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * @author felix
 *
 */
public class VplanNEW_XML {

	private final Logger log = Klassenserver7bbot.INSTANCE.getMainLogger();
	public LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

	/**
	 * 
	 * @param force
	 */
	public void sendVplanMessage(boolean force, String klasse, GuildChannel chan) {

		OffsetDateTime d = checkdate();
		d = OffsetDateTime.of(2022, 07, 04, 10, 10, 10, 10, ZoneOffset.ofHours(2));
		Document doc = read(d);
		Element classPlan = getyourClass(doc, klasse);
		boolean sendApproved = true;

		if (!force && (doc == null || !checkPlanChanges(doc, classPlan))) {
			sendApproved = false;
		}

		if (sendApproved) {

			TextChannel channel;

			if ((channel=(TextChannel) chan)==null) {

				channel = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(779024287733776454L)
						.getTextChannelById(918904387739459645L);

				if (Klassenserver7bbot.INSTANCE.indev) {
					channel = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(850697874147770368L)
							.getTextChannelById(920777920681738390L);
				}
			}

			String info = doc.getElementsByTagName("ZiZeile").item(0).getTextContent();
			log.debug("sending Vplanmessage with following hash: " + classPlan.hashCode() + " and devmode = "
					+ Klassenserver7bbot.INSTANCE.indev);

			EmbedBuilder embbuild = new EmbedBuilder();

			embbuild.setTitle("Es gibt einen neuen Stundenplan für "
					+ doc.getElementsByTagName("DatumPlan").item(0).getTextContent() + " ("+klasse+")");
			embbuild.setFooter("Stand vom " + doc.getElementsByTagName("zeitstempel").item(0).getTextContent());

			TableMessage tablemess = new TableMessage();
			tablemess.addHeadline("Stunde", "Fach", "Lehrer", "Raum", "Info");

			NodeList lessons = classPlan.getElementsByTagName("Std");

			for (int i = 0; i < lessons.getLength(); i++) {
				Element e = (Element) lessons.item(i);

				boolean subjectchange = e.getElementsByTagName("Fa").item(0).hasAttributes();
				boolean teacherchange = e.getElementsByTagName("Le").item(0).hasAttributes();
				boolean roomchange = e.getElementsByTagName("Ra").item(0).hasAttributes();

				tablemess.addCell(e.getElementsByTagName("St").item(0).getTextContent());

				if (!e.getElementsByTagName("Fa").item(0).getTextContent().equalsIgnoreCase("---")) {

					Cell subjectcell = Cell.of(e.getElementsByTagName("Fa").item(0).getTextContent(),
							(subjectchange ? Cell.STYLE_BOLD : Cell.STYLE_NONE));
					Cell teachercell = Cell.of(e.getElementsByTagName("Le").item(0).getTextContent(),
							(teacherchange ? Cell.STYLE_BOLD : Cell.STYLE_NONE));

					StringBuilder strbuild = new StringBuilder();
					String teacher = e.getElementsByTagName("Le").item(0).getTextContent();

					if (teacher != null && !teacher.equalsIgnoreCase("")) {
						JsonElement teachelem = Klassenserver7bbot.teacherslist.get(teacher);

						if (teachelem != null) {

							JsonObject teach = teachelem.getAsJsonObject();

							String gender = teach.get("gender").getAsString();
							if (gender.equalsIgnoreCase("female")) {
								strbuild.append("Frau ");
							} else if (gender.equalsIgnoreCase("male")) {
								strbuild.append("Herr ");
							}

							if (teach.get("is_doctor").getAsBoolean()) {

								strbuild.append("Dr. ");

							}

							strbuild.append(teach.get("full_name").getAsString().replaceAll("\"", ""));

						}
					}

					teachercell.setLinkTitle(strbuild.toString());
					teachercell.setLinkURL("https://manos-dresden.de/lehrer");

					Cell room = Cell.of(e.getElementsByTagName("Ra").item(0).getTextContent(),
							(roomchange ? Cell.STYLE_BOLD : Cell.STYLE_NONE));

					tablemess.addRow(subjectcell, teachercell, room);

				} else {

					tablemess.addRow(Cell.of("AUSFALL", Cell.STYLE_BOLD), "---", "---");

				}

				if (!e.getElementsByTagName("If").item(0).getTextContent().equalsIgnoreCase("")) {

					tablemess.addCell(e.getElementsByTagName("If").item(0).getTextContent());

				} else {
					tablemess.addCell("   ");
				}

			}

			tablemess.automaticLineBreaks(4);
			embbuild.setDescription("**Änderungen**\n" + tablemess.build());

			if (!(info.equalsIgnoreCase(""))) {

				embbuild.addField("Sonstige Infos", info, false);

			}

			embbuild.setColor(Color.decode("#038aff"));
			channel.sendMessageEmbeds(embbuild.build()).queue();

			lsql.onUpdate("UPDATE vplannext SET classeintraege = " + classPlan.hashCode());

		}

	}

	/**
	 * 
	 * @param plan
	 * @param classPlan
	 * @return
	 */
	private boolean checkPlanChanges(Document plan, Element classPlan) {

		Integer dbhash = null;

		if (plan != null) {
			boolean synced = synchronizePlanDB(plan);

			if (classPlan != null) {
				int planhash = classPlan.hashCode();

				ResultSet set = lsql.onQuery("SELECT classeintraege FROM vplannext");
				try {
					if (set.next()) {

						dbhash = set.getInt("classeintraege");

					}
					if (dbhash != null) {
						if (dbhash != planhash || synced) {

							lsql.onUpdate("UPDATE vplannext SET zieldatum = '"
									+ OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "'");

						} else {
							return false;

						}
					} else {

						lsql.onUpdate("INSERT INTO vplannext(zieldatum, classeintraege) VALUES('"
								+ OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "', "
								+ classPlan.hashCode() + ")");
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
				return true;

			}
		}

		return false;
	}

	/**
	 * 
	 * @param obj
	 * @param klasse
	 * @return
	 */
	private Element getyourClass(Document obj, String klasse) {

		if (obj == null) {
			return null;
		}
		NodeList nList = obj.getElementsByTagName("Kl");

		Element yourclass = null;
		for (int i = 0; i < nList.getLength(); i++) {

			Node n = nList.item(i);

			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;

				if (e.getElementsByTagName("Kurz").item(0).getTextContent().equalsIgnoreCase("10b")) {
					yourclass = e;
					break;
				}

			}

		}

		return yourclass;
	}

	/**
	 * 
	 * @param plan
	 * @return
	 */
	private boolean synchronizePlanDB(Document plan) {
		if (plan != null) {
			String dbdate = "";

			String onlinedate = plan.getElementsByTagName("datei").item(0).getTextContent();
			onlinedate = onlinedate.replaceAll("WPlanKl_", "").replaceAll(".xml", "");

			try {
				ResultSet next = lsql.onQuery("SELECT zieldatum FROM vplannext");
				if (next.next()) {

					dbdate = next.getString("zieldatum");
				}

				if (!dbdate.equalsIgnoreCase(onlinedate)) {

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
		}
		return false;

	}

	/**
	 * 
	 * @return
	 */
	private OffsetDateTime checkdate() {

		OffsetDateTime cutime = OffsetDateTime.now();
		int day = cutime.getDayOfWeek().getValue();

		if (day >= 5) {
			return cutime.plusDays(8 - day);
		} else {
			return cutime.plusDays(1);
		}

	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private Document read(OffsetDateTime date) {
		if (date == null) {
			log.error("DocumentReadError - date = null caused by\n"
					+ new NullPointerException().getStackTrace().toString());
			return null;
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
		try {

			String xmlstr = getVplanXML(date);

			if (xmlstr != null) {

				DocumentBuilder docbuild = factory.newDocumentBuilder();
				Document doc = docbuild.parse(new ByteArrayInputStream(xmlstr.getBytes(StandardCharsets.UTF_8)));

				doc.getDocumentElement();
				return doc;
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param date
	 * @return
	 */
	private String getVplanXML(OffsetDateTime date) {

		final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("www.stundenplan24.de", 443),
				new UsernamePasswordCredentials("schueler", Klassenserver7bbot.INSTANCE.getVplanPW()));

		try (final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.build()) {

			final HttpGet httpget = new HttpGet("https://www.stundenplan24.de/"
					+ Klassenserver7bbot.INSTANCE.getSchoolID() + "/wplan/wdatenk/WPlanKl_"
					+ date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xml");
			final CloseableHttpResponse response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);

			} else {

				if (response.getStatusLine().getStatusCode() == 201) {
					return null;
				}

				log.warn("Vplan-Servererror StatusCode: " + response.getStatusLine().getStatusCode() + " - "
						+ response.getStatusLine().getReasonPhrase());
				return null;

			}

		} catch (IOException e) {
			log.error("Vplan IO Exception - please check your connection");
			e.printStackTrace();
		}

		return null;
	}

}
