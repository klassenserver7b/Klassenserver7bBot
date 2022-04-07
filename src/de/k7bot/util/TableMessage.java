package de.k7bot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TableMessage {
    public static final String EMPTY_CHARACTER = "\u200E";

    private final List<String> headers;
    private final List<Cell> content;

    public TableMessage() {
        headers = new ArrayList<>();
        content = new ArrayList<>();
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<Cell> getContent() {
        return content;
    }

    public TableMessage addHeader(String header) {
        headers.add(header);
        return this;
    }

    public TableMessage addHeadline(String... headers) {
        this.headers.addAll(List.of(headers));
        return this;
    }

    public TableMessage addCell(String cell) {
        content.add(Cell.of(cell));
        return this;
    }

    public TableMessage addCell(Cell cell) {
        content.add(cell);
        return this;
    }

    public TableMessage addRow(String... cells) {
        content.addAll(Stream.of(cells).map(Cell::of).collect(Collectors.toList()));
        return this;
    }

    public TableMessage addRow(Cell... cells) {
        content.addAll(Arrays.asList(cells));
        return this;
    }

    public TableMessage addRow(Object... objCells) {
        List<Cell> cells = new ArrayList<>();

        for (Object cell : objCells) {
            if (cell == null)
                cells.add(Cell.of(""));
            else if (cell.getClass().isAssignableFrom(String.class))
                cells.add(Cell.of((String) cell));
            else if (cell.getClass().isAssignableFrom(Cell.class))
                cells.add((Cell) cell);
            else
                cells.add(Cell.of(""));
        }

        content.addAll(cells);

        return this;
    }

    public String build() {
        int columns = headers.size();
        int rows = content.size() / columns + (content.size() % columns > 0 ? 1 : 0);

        List<Integer> columnWidths = new ArrayList<>();
        for (int c = 0; c < columns; c++) {
            int maxWidth = headers.get(c).length();
            for (int r = 0; r < rows; r++) {
                Cell cell = getCell(r, c);
                if (cell.getContent().length() > maxWidth)
                    maxWidth = cell.getContent().length();
            }
            columnWidths.add(maxWidth);
        }

        List<StringBuilder> lines = new ArrayList<>();

        for (int r = -1; r < rows; r++) {
            StringBuilder line = new StringBuilder();

            for (int c = 0; c < columns; c++) {
                Cell cell = getCell(r, c);
                String formatted = getFormattedString(cell);

                if (c != columns - 1) {
                    line.append(formatted).append(EMPTY_CHARACTER)
                            .append("`").append(whiteSpace(columnWidths.get(c) - cell.length())).append("`").append(EMPTY_CHARACTER)
                            .append("**`|`**").append(EMPTY_CHARACTER);
                } else {
                    line.append(formatted).append(EMPTY_CHARACTER)
                            .append("`").append(whiteSpace(columnWidths.get(c) - cell.length())).append("`");
                }
            }

            lines.add(line);
        }

        return String.join("\n", lines);
    }

    private Cell getCell(int r, int c) {
        if (r < 0) {
            if (c >= headers.size())
                return Cell.of("");

            return Cell.of(headers.get(c), Cell.STYLE_BOLD | Cell.STYLE_UNDERLINED);
        }

        int index = headers.size() * r + c;
        if (index >= content.size())
            return Cell.of("");
        return content.get(index);
    }

    private String whiteSpace(int length) {
        if (length < 1)
            return EMPTY_CHARACTER;
        return " ".repeat(length);
    }

    private String getFormattedString(Cell cell) {
        StringBuilder builder = new StringBuilder();

        if (cell.hasLink())
            builder.append("[");

        if (cell.isBold())
            builder.append("**");
        if (cell.isUnderlined())
            builder.append("__");
        if (cell.isStrikethrough())
            builder.append("~~");
        if (cell.isItalic())
            builder.append("*");

        builder.append("`");
        if (cell.length() == 0)
            builder.append(EMPTY_CHARACTER);
        else
            builder.append(cell.getContent());
        builder.append("`");

        if (cell.isItalic())
            builder.append("*");
        if (cell.isStrikethrough())
            builder.append("~~");
        if (cell.isUnderlined())
            builder.append("__");
        if (cell.isBold())
            builder.append("**");

        if (cell.hasLink()) {
            builder.append("](");
            builder.append(cell.getLinkURL());

            if (cell.hasLinkTitle())
                builder.append(" \"").append(cell.getLinkTitle()).append("\"");

            builder.append(")");
        }

        return builder.toString();
    }
}
