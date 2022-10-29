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
import com.google.gson.JsonParser;

import de.k7bot.util.customapis.types.InternalAPI;

/**
 * @author Felix
 *
 */
public class KauflandInteractions implements InternalAPI{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public List<JsonElement> getOffers() {

		ArrayList<JsonElement> offers = new ArrayList<>();

		String json;

		if ((json = loadJson()) != null) {

			JsonArray arr = JsonParser.parseString(json).getAsJsonArray();

			arr.forEach(elem -> {

				elem.getAsJsonObject().get("categories").getAsJsonArray().forEach(cat -> {

					cat.getAsJsonObject().get("offers").getAsJsonArray().forEach(offer -> {

						offers.add(offer.getAsJsonObject());

					});

				});

			});

		}

		return offers;

	}

	private String loadJson() {

		final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope("app.kaufland.net", 443),
				new UsernamePasswordCredentials("KIS-KLAPP", "Dreckszeug_3529-Achtspnner"));

		try (final CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(null).build()) {

			final HttpGet httpget = new HttpGet("https://www.stundenplan24.de/");
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
			log.error(e.getMessage(),e);
		}
		return null;

	}


	@Override
	public void checkforUpdates() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
