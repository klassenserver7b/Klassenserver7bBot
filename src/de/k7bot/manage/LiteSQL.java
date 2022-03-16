package de.k7bot.manage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteSQL {
	private static Connection conn;
	private static Statement stmt;
	public Logger dblog = LoggerFactory.getLogger("DB-Log");

	public void connect() {
		conn = null;

		try {
			File file = new File("datenbank.db");
			if (!file.exists()) {
				file.createNewFile();
			}

			String url = "jdbc:sqlite:" + file.getPath();
			conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
			dblog.info("Datenbankverbindung hergestellt");
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (conn != null) {
				conn.close();
				dblog.info("Datenbankverbindung getrennt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void onUpdate(String sql) {
		try {
			String query = sql;
			stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet onQuery(String sql){
			
			try {
				String query = sql;
				return stmt.executeQuery(query);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
	}

	public Logger getdblog() {
		return dblog;
	}
}