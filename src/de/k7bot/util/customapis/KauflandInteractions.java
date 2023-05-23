/**
 * 
 */
package de.k7bot.util.customapis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.util.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.k7bot.util.customapis.types.InternalAPI;

/**
 * @author Klassenserver7b
 *
 */
public class KauflandInteractions implements InternalAPI {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public List<JsonObject> getOffers() {

		ArrayList<JsonObject> offers = new ArrayList<>();

		String json = loadJson();

		if (json == null) {
			return null;
		}

		JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

		for (JsonElement elem : arr) {

			for (JsonElement cat : elem.getAsJsonObject().get("categories").getAsJsonArray()) {

				for (JsonElement offer : cat.getAsJsonObject().get("offers").getAsJsonArray()) {

					offers.add(offer.getAsJsonObject());

				}

			}

		}

		return offers;

	}

	private String loadJson() {

		final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("app.kaufland.net", 443),
				new UsernamePasswordCredentials("KIS-KLAPP", "Dreckszeug_3529-Achtspnner"));

		try (final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(null).build()) {

			final HttpGet httpget = new HttpGet("https://app.kaufland.net/data/api/v5/offers/DE3940");
			final CloseableHttpResponse response = httpclient.execute(httpget);

			if (response.getStatusLine().getStatusCode() == 200) {
				return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);

			} else {

				if (response.getStatusLine().getStatusCode() == 201) {
					return null;
				}

				log.debug("KauflandAPI-Servererror StatusCode: " + response.getStatusLine().getStatusCode() + " - "
						+ response.getStatusLine().getReasonPhrase());
				return null;

			}

		} catch (

		IOException e) {
			log.error("KauflandAPI IO Exception - please check your connection");
			log.error(e.getMessage(), e);
		}
		return null;

	}

	@Override
	public void checkforUpdates() {
		List<JsonObject> offs = getOffers();

		if (offs != null) {
			offs.clear();
		}
	}

	@Override
	public void shutdown() {
		log.debug("Kaufland shutdown");
	}

}