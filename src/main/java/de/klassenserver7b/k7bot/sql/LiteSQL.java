package de.klassenserver7b.k7bot.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteSQL {
	private static Connection conn;
	public final static Logger dblog = LoggerFactory.getLogger("DB-Log");

	public static void connect() {
		conn = null;

		try {
			File file = new File("resources/datenbank.db");
			file.createNewFile();

			String url = "jdbc:sqlite:" + file.getPath();
			conn = DriverManager.getConnection(url);
			dblog.info("Datenbankverbindung hergestellt");
		} catch (SQLException | IOException e) {
			dblog.error(e.getMessage(), e);
		}
	}

	public static void disconnect() {
		try {
			if (conn != null) {
				conn.close();
				dblog.info("Datenbankverbindung getrennt");
			}
		} catch (SQLException e) {
			dblog.error(e.getMessage(), e);
		}
	}

	public static int onUpdate(String sqlpattern, Object... parameters) {

		if (parameters.length != countMatches(sqlpattern, "?")) {
			IllegalArgumentException e = new IllegalArgumentException(
					"Invalid SQLString! - parameter count does not match.", new Throwable().fillInStackTrace());
			dblog.error(e.getMessage(), e);
			return -5;
		}

		try (PreparedStatement p = conn.prepareStatement(sqlpattern)) {

			for (int i = 0; i < parameters.length; i++) {
				p.setObject(i + 1, parameters[i]);
			}

			return p.executeUpdate();

		} catch (SQLException e) {
			dblog.error(e.getMessage(), e);
			return -1;
		}
	}

	@SuppressWarnings("resource")
	public static ResultSet onQuery(String sqlpattern, Object... parameters) {

		if (parameters.length != countMatches(sqlpattern, "?")) {
			IllegalArgumentException e = new IllegalArgumentException(
					"Invalid SQLString! - parameter count does not match.", new Throwable().fillInStackTrace());
			dblog.error(e.getMessage(), e);
		}

		try {

			PreparedStatement p = conn.prepareStatement(sqlpattern);

			for (int i = 0; i < parameters.length; i++) {
				p.setObject(i + 1, parameters[i]);
			}

			return p.executeQuery();

		} catch (SQLException e) {
			dblog.error(e.getMessage(), e);
			return null;
		}

	}

	private static int countMatches(String base, String pattern) {

		int occurences = 0;

		if (0 == pattern.length()) {
			return occurences;
		}

		for (int index = base.indexOf(pattern, 0); index != -1; index = base.indexOf(pattern, index + 1)) {
			occurences++;
		}

		return occurences;

	}

	public static Logger getdblog() {
		return dblog;
	}
}