package de.k7bot.music.utilities;

public class SongTitle {
	
	private String strippedtitle;
	private boolean containsauthor;
	
	public void setTitle(String title) {	
		strippedtitle = title;
	}
	
	public void setAuthorContainment(boolean acment) {	
		containsauthor = acment;
	}
	
	public String getTitle() {
		return strippedtitle;
	}
	
	public boolean containsauthor() {
		return containsauthor;
	}
}
