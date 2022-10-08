
package de.k7bot.sql;

public class SQLManager {
	
	public static void onCreate() {

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS reactroles(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, channelId INTEGER, messageId INTEGER, emote VARCHAR, roleId INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS modlogs(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, memberId INTEGER, requesterId INTEGER, memberName STRING, requesterName STRING, action STRING, reason STRING, date STRING);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS createdprivatevcs(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, channelId INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS musicutil(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, channelId INTEGER, volume INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS statschannels(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, categoryId INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS botutil(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, syschannelId INTEGER, prefix STRING);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS hypixelnewschannels(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, guildId INTEGER, channelId INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS hypnewstime(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, datum STRING);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS vplancurrent(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, zieldatum STRING, classeintraege INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS vplannext(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, zieldatum STRING, classeintraege INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS musiclogs(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, songname STRING, songauthor STRING, guildId INTEGER, timestamp INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS commandlog(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, command STRING, guildId INTEGER, timestamp INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS ha3users(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ingamename STRING, realname STRING, dcname STRING, dcId INTEGER, approved INTEGER);");

		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS lernsaxinteractions(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, lernplanId STRING);");
		
		LiteSQL.onUpdate(
				"CREATE TABLE IF NOT EXISTS subscriptions(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, type INTEGER, target INTEGER, targetDcId INTEGER, subscriptionId INTEGER);");
	}
}