package de.k7bot.util;

import com.vdurmont.emoji.EmojiParser;

public class TitleStripper {

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

		strippedtitle = strippedtitle.replaceAll("\\(offizielles musikvideo\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles musikvideo\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(official musicvideo\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[official musicvideo\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(official music video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[official music video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(official video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[official video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles audio\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles audio\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(official audio\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[official audio\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(official lyric video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[official lyric video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles lyric video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles lyric video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(lyric video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[lyric video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(lyrics video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[lyrics video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(short version\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[short version\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(videoclip\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[videoclip\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(lyrics\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[lyrics\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles musikvideo\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles musikvideo\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(official musicvideo\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[official musicvideo\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(official music video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[official music video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(official video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[official video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(official lyric video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[official lyric video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles lyric video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles lyric video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(lyric video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[lyric video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(lyrics video\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[lyrics video\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(short version\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[short version\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(videoclip\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[videoclip\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(lyrics\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[lyrics\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(offizielles audio\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[offizielles audio\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(official audio\\)\\p{Alnum}", "");
		strippedtitle = strippedtitle.replaceAll("\\[official audio\\]\\p{Alnum}", "");

		strippedtitle = strippedtitle.replaceAll("\\(\\p{Alnum} edit\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[\\p{Alnum} edit\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(\\p{Alnum} video\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[\\p{Alnum} video\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(\\p{Alnum} musikvideo\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[\\p{Alnum} musikvideo\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(\\p{Alnum} musicvideo\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[\\p{Alnum} musicvideo\\]", "");

		strippedtitle = strippedtitle.replaceAll("\\(\\p{Alnum} version\\)", "");
		strippedtitle = strippedtitle.replaceAll("\\[\\p{Alnum} version\\]", "");

		stitle.setTitle(strippedtitle.toLowerCase().trim());
		
		return stitle;
	}

}
