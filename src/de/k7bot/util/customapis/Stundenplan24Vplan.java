/**
 *
 */
package de.k7bot.util.customapis;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.subscriptions.types.SubscriptionTarget;
import de.k7bot.util.Cell;
import de.k7bot.util.HttpUtilities;
import de.k7bot.util.TableMessage;
import de.k7bot.util.customapis.types.InternalAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * @author felix
 * @since 1.14.0
 */
public class Stundenplan24Vplan implements InternalAPI {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private List<String> klassen;

	public Stundenplan24Vplan() {
		klassen = new ArrayList<>();
	}

	public Stundenplan24Vplan(String... klassen) {
		this.klassen = new ArrayList<>();

		for (String klasse : klassen) {
			this.klassen.add(klasse);
		}
	}

	public void registerKlassen(String... klassen) {
		for (String klasse : klassen) {
			this.klassen.add(klasse);
		}
	}

	/**
	 *
	 * @param force
	 * @param klasse
	 * @param channel
	 *
	 * @since 1.15.0
	 */
	public void sendVplanToChannel(boolean force, String klasse, TextChannel channel) {

		MessageCreateData d = getVplanMessage(force, klasse);

		if (d != null) {
			channel.sendMessage(d).queue();
		}

	}

	/**
	 *
	 */
	@Override
	public void checkforUpdates() {

		for (String klasse : klassen) {

			VplanNotify(klasse);

		}

	}

	/**
	 *
	 * @param klasse
	 *
	 * @since 1.15.0
	 */
	public void VplanNotify(String klasse) {

		MessageCreateData d = getVplanMessage(false, klasse);

		if (d != null) {
			Klassenserver7bbot.getInstance().getSubscriptionManager()
					.provideSubscriptionNotification(SubscriptionTarget.VPLAN, d);
		}
	}

	/**
	 * 
	 * @param force
	 * @param klasse
	 * @since 1.14.0
	 * @return
	 */
	private MessageCreateData getVplanMessage(boolean force, String klasse) {

		OffsetDateTime d = checkdate();
		Document doc = read(d);
		Element classPlan = getyourClass(doc, klasse);
		boolean sendApproved = true;

		if (!force && (doc == null || !checkPlanChanges(doc, classPlan))) {
			sendApproved = false;
		}

		if (sendApproved) {

			String info = "";

			if (doc == null) {
				return null;
			}

			if (doc.getElementsByTagName("ZiZeile").getLength() != 0) {
				info = doc.getElementsByTagName("ZiZeile").item(0).getTextContent();
			}

			log.debug("sending Vplanmessage with following hash: " + classPlan.hashCode() + " and devmode = "
					+ Klassenserver7bbot.getInstance().isDevMode());

			EmbedBuilder embbuild = new EmbedBuilder();

			embbuild.setTitle("Es gibt einen neuen Stundenplan für "
					+ doc.getElementsByTagName("DatumPlan").item(0).getTextContent() + " (" + klasse + ")");

			TableMessage tablemess = new TableMessage();
			tablemess.addHeadline("Stunde", "Fach", "Lehrer", "Raum", "Info");

			NodeList lessons = classPlan.getElementsByTagName("Std");

			int limit = 6;
			int ges = lessons.getLength();

			if (lessons.getLength() < limit) {
				limit = lessons.getLength();
			}

			for (int i = 0; i < limit; i++) {
				Element e = (Element) lessons.item(i);
				appendLesson(e, tablemess);

			}
			TableMessage additionalmess = new TableMessage();
			additionalmess.setColums(5);

			for (int i = limit; i < ges; i++) {
				Element e = (Element) lessons.item(i);
				appendLesson(e, additionalmess);

			}

			tablemess.automaticLineBreaks(4);
			embbuild.setDescription("**Änderungen**\n" + tablemess.build());

			boolean isextraembed = false;

			if (additionalmess.hasData()) {
				additionalmess.automaticLineBreaks(4);

				if (additionalmess.build().length() <= 1020) {
					embbuild.addField("", additionalmess.build(), false);
				} else {
					isextraembed = true;
				}
			}

			if (!(info.equalsIgnoreCase(""))) {

				embbuild.addField("Sonstige Infos", info, false);

			}

			embbuild.setColor(Color.decode("#038aff"));

			LiteSQL.onUpdate("UPDATE vplannext SET classeintraege = ?;", classPlan.getTextContent().hashCode());

			MessageCreateBuilder builder = new MessageCreateBuilder();

			if (isextraembed) {

				embbuild.clearFields();
				embbuild.setFooter(null);
				builder.addEmbeds(embbuild.build());

				EmbedBuilder addbuild = new EmbedBuilder();

				addbuild.setDescription(additionalmess.build());
				addbuild.setColor(Color.decode("#038aff"));
				addbuild.setFooter("Stand vom " + doc.getElementsByTagName("zeitstempel").item(0).getTextContent());

				if (!(info.equalsIgnoreCase(""))) {

					addbuild.addField("Sonstige Infos", info, false);

				}

				builder.addEmbeds(addbuild.build());

			} else {
				builder.setEmbeds(embbuild.build());
			}

			return builder.build();

		}

		return null;

	}

	/**
	 *
	 * @param e
	 * @param tablemess
	 * @return
	 */
	private TableMessage appendLesson(Element e, TableMessage tablemess) {
		TableMessage ret;

		boolean subjectchange = e.getElementsByTagName("Fa").item(0).hasAttributes();
		boolean teacherchange = e.getElementsByTagName("Le").item(0).hasAttributes();
		boolean roomchange = e.getElementsByTagName("Ra").item(0).hasAttributes();
		String lesson = e.getElementsByTagName("St").item(0).getTextContent();

		if (!e.getElementsByTagName("Fa").item(0).getTextContent().equalsIgnoreCase("---")) {

			Cell subjectcell = Cell.of(e.getElementsByTagName("Fa").item(0).getTextContent(),
					(subjectchange ? Cell.STYLE_BOLD : Cell.STYLE_NONE));
			Cell teachercell = Cell.of(e.getElementsByTagName("Le").item(0).getTextContent(),
					(teacherchange ? Cell.STYLE_BOLD : Cell.STYLE_NONE));

			StringBuilder strbuild = new StringBuilder();
			String teacher = e.getElementsByTagName("Le").item(0).getTextContent();

			if (teacher != null && !teacher.equalsIgnoreCase("")) {
				JsonObject teachobj = Klassenserver7bbot.getInstance().getTeacherList();
				JsonElement teachelem;

				if (teachobj != null && (teachelem = teachobj.get(teacher)) != null) {

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

			tablemess.addRow(lesson, subjectcell, teachercell, room);

		} else {

			tablemess.addRow(lesson, Cell.of("AUSFALL", Cell.STYLE_BOLD), "---", "---");

		}

		if (!e.getElementsByTagName("If").item(0).getTextContent().equalsIgnoreCase("")) {

			tablemess.addCell(e.getElementsByTagName("If").item(0).getTextContent());

		} else {
			tablemess.addCell("   ");
		}

		ret = tablemess;

		return ret;
	}

	/**
	 *
	 * @param plan
	 * @param classPlan
	 * @return
	 * @since 1.14.0
	 */
	private boolean checkPlanChanges(Document plan, Element classPlan) {

		log.debug("PLAN DB CHECK");

		Integer dbhash = null;

		if (plan != null) {
			boolean synced = synchronizePlanDB(plan);

			if (classPlan != null) {
				int planhash = classPlan.getTextContent().hashCode();

				ResultSet set = LiteSQL.onQuery("SELECT classeintraege FROM vplannext;");
				try {
					if (set.next()) {

						dbhash = set.getInt("classeintraege");

					}

					String onlinedate = plan.getElementsByTagName("datei").item(0).getTextContent();
					onlinedate = onlinedate.replaceAll("WPlanKl_", "").replaceAll(".xml", "");

					if (dbhash != null) {

						if (dbhash != planhash || synced) {

							LiteSQL.onUpdate("UPDATE vplannext SET zieldatum = ?;", onlinedate);
							return true;

						} else {
							return false;

						}
					} else {

						LiteSQL.onUpdate("INSERT INTO vplannext(zieldatum, classeintraege) VALUES(?, ?);", onlinedate,
								planhash);
					}

				} catch (SQLException e) {
					log.error(e.getMessage(), e);
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
	 * @since 1.14.0
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
	 * @since 1.14.0
	 */
	private boolean synchronizePlanDB(Document plan) {
		if (plan != null) {
			String dbdate = "";

			String onlinedate = plan.getElementsByTagName("datei").item(0).getTextContent();
			onlinedate = onlinedate.replaceAll("WPlanKl_", "").replaceAll(".xml", "");

			try {
				ResultSet next = LiteSQL.onQuery("SELECT zieldatum FROM vplannext;");

				if (next.next()) {
					dbdate = next.getString("zieldatum");
				}

				if (!dbdate.equalsIgnoreCase(onlinedate)) {

					LiteSQL.getdblog().info("Plan-DB-Sync");

					ResultSet old = LiteSQL.onQuery("SELECT * FROM vplannext;");

					if (old.next()) {
						LiteSQL.onUpdate("UPDATE vplancurrent SET zieldatum = ?, classeintraege = ?;",
								old.getString("zieldatum"), old.getInt("classeintraege"));
						LiteSQL.onUpdate("UPDATE vplannext SET zieldatum = '', classeintraege = '';");
					}
					return true;

				} else {
					return false;
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}
		}
		return false;

	}

	/**
	 *
	 * @return
	 * @since 1.14.0
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
	 * @since 1.14.0
	 */
	private Document read(OffsetDateTime date) {
		if (date == null) {
			log.error("DocumentReadError - date = null caused by\n", new NullPointerException());
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
			log.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 *
	 * @param date
	 * @return
	 * @since 1.14.0
	 */
	private String getVplanXML(OffsetDateTime date) {

		final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("www.stundenplan24.de", 443),
				new UsernamePasswordCredentials("schueler",
						Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("vplanpw").toCharArray()));

		try (final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider)
				.build()) {

			final HttpGet httpget = new HttpGet("https://www.stundenplan24.de/"
					+ Klassenserver7bbot.getInstance().getPropertiesManager().getProperty("schoolID")
					+ "/wplan/wdatenk/WPlanKl_" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xml");

			final String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());
			HttpUtilities.closeHttpClient(httpclient);
			return response;

		} catch (HttpHostConnectException | HttpResponseException e1) {
			log.warn("Vplan Connection failed!" + e1.getMessage());
		} catch (IOException e) {
			log.error("Vplan IO Exception - please check your connection and settings");
			log.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 *
	 */
	@Override
	public void shutdown() {
		klassen.clear();

	}

	@Override
	public void restart() {
		log.debug("restart requested");
	}

}
