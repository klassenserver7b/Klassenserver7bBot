
package de.k7bot.manage;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.LiteSQL;

public class SQLManager {
	public static void onCreate() {

		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS reactroles(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, channelId INTEGER, messageId INTEGER, emote VARCHAR, roleId INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS modlogs(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, memberId INTEGER, requesterId INTEGER, memberName STRING, requesterName STRING, action STRING, reason STRING, date STRING)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS createdprivatevcs(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, channelId INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS musicchannel(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, channelId INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS statschannels(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, categoryId INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS botutil(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, syschannelId INTEGER, prefix STRING, volume INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS hypixelnewschannels(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, channelId INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS hypnewstime(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, datum STRING)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS vplancurrent(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, zieldatum STRING, classeintraege INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS vplannext(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, zieldatum STRING, classeintraege INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS musiclogs(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, songname STRING, songauthor STRING, guildId INTEGER, timestamp INTEGER)");

		lsql.onUpdate(
				"CREATE TABLE IF NOT EXISTS commandlog(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, command STRING, timestamp INTEGER)");
	}
}