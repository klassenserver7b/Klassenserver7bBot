package de.k7bot.music.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vdurmont.emoji.EmojiParser;

import de.k7bot.util.customapis.DiscogsAPI;

public class SongDataUtils {

	private final DiscogsAPI api;
	private final Logger log;

	public SongDataUtils() {
		api = new DiscogsAPI();
		log = LoggerFactory.getLogger(this.getClass().getCanonicalName());
	}

	public SongDataUtils(DiscogsAPI api) {
		this.api = api;
		log = LoggerFactory.getLogger(this.getClass().getCanonicalName());
	}

	public String stripSongTitle(String title) {

		String[] split = title.trim().toLowerCase().split(" - ");

		String strippedtitle;

		if (split.length <= 1) {
			strippedtitle = split[0];
		} else {
			strippedtitle = split[0] + " - " + split[1];
		}

		strippedtitle = EmojiParser.removeAllEmojis(strippedtitle);

		strippedtitle = strippedtitle.replaceAll("|", "");

//		strippedtitle = strippedtitle.replaceAll("\\(offizielles musikvideo\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles musikvideo\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official musicvideo\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official musicvideo\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official music video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official music video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles audio\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles audio\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official audio\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official audio\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official lyric video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official lyric video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles lyric video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles lyric video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(lyric video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[lyric video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(lyrics video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[lyrics video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(short version\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[short version\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(videoclip\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[videoclip\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(lyrics\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[lyrics\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles musikvideo\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles musikvideo\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official musicvideo\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official musicvideo\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official music video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official music video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official lyric video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official lyric video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles lyric video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles lyric video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(lyric video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[lyric video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(lyrics video\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[lyrics video\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(short version\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[short version\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(videoclip\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[videoclip\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(lyrics\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[lyrics\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(offizielles audio\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[offizielles audio\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(official audio\\).*", "");
//		strippedtitle = strippedtitle.replaceAll("\\[official audio\\].*", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(.*edit\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[.*edit\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(.*video\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[.*video\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(.*musikvideo\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[.*musikvideo\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(.*musicvideo\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[.*musicvideo\\]", "");
//
//		strippedtitle = strippedtitle.replaceAll("\\(.*version\\)", "");
//		strippedtitle = strippedtitle.replaceAll("\\[.*version\\]", "");

		strippedtitle = strippedtitle.replaceAll(" \\(.*\\)", "");
		strippedtitle = strippedtitle.replaceAll(" \\[.*\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(.*\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[.*\\]", "");

		return strippedtitle.trim();
	}

	public String stripSongAuthor(String author) {

		String strippedauthor = author.trim().toLowerCase();
		strippedauthor = strippedauthor.replaceAll(" - Thema", "");
		strippedauthor = strippedauthor.replaceAll(" - ", "");
		strippedauthor = strippedauthor.replaceAll("-", "");

		return strippedauthor;
	}

	public SongJson parseYtTitle(String yttitle, String channel) {

		String stripedtitle = stripSongTitle(yttitle);
		String author = stripSongAuthor(channel);

		log.info("YT TitleParseRequest - title=" + stripedtitle + "&channel=" + author);

		if (!api.isApiEnabled()) {
			return parseOffline(stripedtitle, author);
		} else {
			return parseViaDiscogs(stripedtitle, author);
		}

	}

	private SongJson parseViaDiscogs(String title, String channel) {

		log.info("Parsing Online");
		String newtitle = title;

		if (!titleContainsAuthor(title)) {
			newtitle += " " + channel;
		}
		
		try {

			SongJson json = api.getFilteredSongJson(newtitle);

			if (json == null) {
				
				log.warn("Online Parse Error - Parsing Offline");
				return parseOffline(title, channel);
				
			}

			log.info("Successfully Parsed");
			return json;

		} catch (IllegalArgumentException e) {
			return null;
		}

	}

	private boolean titleContainsAuthor(String title) {

		String[] parts = title.split(" - ");

		if (parts.length >= 1) {
			return true;
		} else {
			return false;
		}

	}

	private SongJson parseOffline(String title, String channel) {
		log.info("Parsing Offline");

		if (titleContainsAuthor(title)) {
			log.debug("Parsing YT Title Offline - Author in title");
			return parseTitleWithAuthor(title);
		} else {
			log.debug("Parsing YT Title Offline - Author not in title");
			return parseTitleWithoutAuthor(title, channel);
		}

	}

	private SongJson parseTitleWithAuthor(String title) {

		String[] parts = title.split(" - ");

		try {
			SongJson json = SongJson.ofUnvalidated(parts[1], parts[0], "0",
					"https://www.discogs.com/de/search/?q=" + parts[1] + "+" + parts[0], "");
			log.info("Successfully Parsed");
			return json;
		} catch (IllegalArgumentException e) {
			log.info("Couldn't Parse");
			return null;
		}

	}

	private SongJson parseTitleWithoutAuthor(String title, String channel) {
		try {
			SongJson json = SongJson.ofUnvalidated(title, channel, "0",
					"https://www.discogs.com/de/search/?q=" + title + "+" + channel, "");
			log.info("Successfully Parsed");
			return json;
		} catch (IllegalArgumentException e) {
			log.info("Couldn't Parse");
			return null;
		}

	}

}
