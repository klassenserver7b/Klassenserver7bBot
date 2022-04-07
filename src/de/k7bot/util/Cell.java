package de.k7bot.util;

public class Cell {
    public static final int STYLE_BOLD = 0b0001;
    public static final int STYLE_UNDERLINED = 0b0010;
    public static final int STYLE_STRIKETHROUGH = 0b0100;
    public static final int STYLE_ITALIC = 0b1000;
    public static final int STYLE_NONE = 0b0000;

    private String content;
    private String linkURL;
    private String linkTitle;

    private boolean isBold;
    private boolean isUnderlined;
    private boolean isStrikethrough;
    private boolean isItalic;

    public Cell(String content, String linkURL, String linkTitle, boolean isBold, boolean isUnderlined, boolean isStrikethrough, boolean isItalic) {
        this.content = content;
        this.linkURL = linkURL;
        this.linkTitle = linkTitle;
        this.isBold = isBold;
        this.isUnderlined = isUnderlined;
        this.isStrikethrough = isStrikethrough;
        this.isItalic = isItalic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int length() {
        return getContent() == null ? 0 : getContent().length();
    }

    public boolean hasLink() {
        return getLinkURL() != null;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public boolean hasLinkTitle() {
        return getLinkTitle() != null;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean bold) {
        isBold = bold;
    }

    public boolean isUnderlined() {
        return isUnderlined;
    }

    public void setUnderlined(boolean underlined) {
        isUnderlined = underlined;
    }

    public boolean isStrikethrough() {
        return isStrikethrough;
    }

    public void setStrikethrough(boolean strikethrough) {
        isStrikethrough = strikethrough;
    }

    public boolean isItalic() {
        return isItalic;
    }

    public void setItalic(boolean italic) {
        isItalic = italic;
    }

    public static Cell of(String content) {
        return new Cell(content, null, null, false, false, false, false);
    }

    public static Cell of(String content, String linkURL, String linkTitle) {
        return new Cell(content, linkURL, linkTitle, false, false, false, false);
    }

    public static Cell of(String content, boolean isBold, boolean isUnderlined, boolean isStrikethrough, boolean isItalic) {
        return new Cell(content, null, null, isBold, isUnderlined, isStrikethrough, isItalic);
    }

    public static Cell of(String content, String linkURL, String linkTitle, boolean isBold, boolean isUnderlined, boolean isStrikethrough, boolean isItalic) {
        return new Cell(content, linkURL, linkTitle, isBold, isUnderlined, isStrikethrough, isItalic);
    }

    public static Cell of(String content, int style) {
        return new Cell(content, null, null, (style & STYLE_BOLD) != 0, (style & STYLE_UNDERLINED) != 0, (style & STYLE_STRIKETHROUGH) != 0, (style & STYLE_ITALIC) != 0);
    }

    public static Cell of(String content, String linkURL, String linkTitle, int style) {
        return new Cell(content, linkURL, linkTitle, (style & STYLE_BOLD) != 0, (style & STYLE_UNDERLINED) != 0, (style & STYLE_STRIKETHROUGH) != 0, (style & STYLE_ITALIC) != 0);
    }
}
