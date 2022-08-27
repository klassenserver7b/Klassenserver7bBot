package de.k7bot.util;

import com.vdurmont.emoji.EmojiParser;

public class SongDataStripper {

	public static SongTitle stripTitle(String title) {

		SongTitle stitle = new SongTitle();
		
		String[] split = title.trim().toLowerCase().split(" - ");

		String strippedtitle;

		if (split.length <= 1) {
			strippedtitle = split[0];
			stitle.setAuthorContainment(false);
		} else {
			strippedtitle = split[0] + " - " + split[1];
			stitle.setAuthorContainment(true);
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
		
		strippedtitle = strippedtitle.replaceAll("\\(.*\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[.*\\]", "");

		stitle.setTitle(strippedtitle.toLowerCase().trim());
		
		return stitle;
	}
	
	public static String stripAuthor(String author) {
		
		String strippedauthor = author.trim().toLowerCase();
		strippedauthor = strippedauthor.replaceAll(" - Thema", "");
		strippedauthor = strippedauthor.replaceAll(" - ", "");
		strippedauthor = strippedauthor.replaceAll("-", "");
		
		
		return strippedauthor;
	}

}
