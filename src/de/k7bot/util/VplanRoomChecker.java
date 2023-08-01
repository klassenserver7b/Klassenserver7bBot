/**
 * 
 */
package de.k7bot.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.sql.LiteSQL;

/**
 * 
 */
public class VplanRoomChecker {

	private static final Logger log;
	private static final List<String> defaultRooms;

	/**
	 * 
	 */
	static {
		log = LoggerFactory.getLogger(VplanRoomChecker.class);
		defaultRooms = List.of("005", "007", "013", "014", "019", "020", "203", "205", "214", "215", "220", "221");
	}

	public static boolean isRoomFree(long lesson, String room) {
		
		room = removeLeadingZero(room);

		try (ResultSet set = LiteSQL.onQuery("SELECT room FROM vplandata WHERE lesson=? ", lesson)) {

			boolean found = false;

			while (set.next()) {
				if (set.getString(1).equalsIgnoreCase(room)) {
					found = true;
					break;
				}
			}

			if (found) {
				return false;

			}

			return true;

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			return false;
		}

	}

	public static List<String> checkDefaults(long lesson) {
		List<String> ret = new ArrayList<>();

		for (String s : defaultRooms) {
			if (isRoomFree(lesson, s)) {
				ret.add(s);
			}
		}

		return ret;

	}

	public static String removeLeadingZero(String s) {
		if (s.startsWith("0")) {
			return s.substring(1);
		}
		return s;
	}

}
