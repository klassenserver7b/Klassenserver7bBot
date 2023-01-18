/**
 *
 */
package de.k7bot.util;

import java.io.IOException;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Felix
 *
 */
public abstract class HttpUtilities {

	private static final Logger log = LoggerFactory.getLogger(HttpUtilities.class);

	public static void closeHttpClient(CloseableHttpClient httpclient) {
		try {
			httpclient.close();
		} catch (IOException e1) {
			log.error(e1.getMessage(), e1);
		}
	}

}
