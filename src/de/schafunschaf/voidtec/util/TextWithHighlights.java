package de.schafunschaf.voidtec.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class TextWithHighlights {
    private final String originalString;
    private final String displayString;
    private final String[] highlights;

    private static final Pattern highlightPattern = Pattern.compile("==.*?==", Pattern.MULTILINE);

    public TextWithHighlights(String string) {
        this.originalString = string;
        this.displayString = cleanStringFromHighlightSymbols(string);
        this.highlights = parseHighlights(string);
    }

    private static String[] parseHighlights(String text) {
        List<String> matchList = new ArrayList<>();

        Matcher matcher = highlightPattern.matcher(text);
        while (matcher.find())
            matchList.add(cleanStringFromHighlightSymbols(matcher.group()));

        return matchList.toArray(new String[0]);
    }

    private static String cleanStringFromHighlightSymbols(String string) {
        return string.replaceAll("==", "");
    }
}
