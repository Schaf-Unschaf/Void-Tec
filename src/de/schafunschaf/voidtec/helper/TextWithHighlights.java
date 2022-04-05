package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.util.Misc;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@Setter
public class TextWithHighlights {

    private static final Pattern highlightPattern = Pattern.compile("==.*?==", Pattern.MULTILINE);
    private final String originalString;
    private final String displayString;
    private final String[] highlights;
    private Color hlColor;

    public TextWithHighlights(String string, Color hlColor) {
        this.originalString = string;
        this.displayString = cleanStringFromHighlightSymbols(string);
        this.highlights = parseHighlights(string);
        this.hlColor = isNull(hlColor) ? Misc.getHighlightColor() : hlColor;
    }

    private static String[] parseHighlights(String text) {
        List<String> matchList = new ArrayList<>();

        Matcher matcher = highlightPattern.matcher(text);
        while (matcher.find()) {
            matchList.add(cleanStringFromHighlightSymbols(matcher.group()));
        }

        return matchList.toArray(new String[0]);
    }

    private static String cleanStringFromHighlightSymbols(String string) {
        return string.replaceAll("==", "");
    }
}
