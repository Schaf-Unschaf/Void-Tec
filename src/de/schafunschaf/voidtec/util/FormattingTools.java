package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.util.Misc;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Log4j
public class FormattingTools {

    public static String singularOrPlural(int number, String wordAsSingular) {
        return (number == 1) ? wordAsSingular : wordAsSingular + "s";
    }

    public static String aOrAn(String name) {
        List<Character> vowels = new ArrayList<>();
        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        return vowels.contains(name.toLowerCase(Locale.ROOT).charAt(0)) ? "an" : "a";
    }

    public static String capitalizeFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String formatCredits(float creditValue) {
        boolean toLarge = creditValue > 1_000_000_000;
        String postFix = toLarge ? "k " : "";
        return Misc.getFormat().format((int) creditValue / (toLarge ? 1_000 : 1)) + postFix + Strings.C;
    }

    public static int parseInteger(String string, int defaultValue) {
        int parsedValue = defaultValue;
        try {
            parsedValue = Integer.parseInt(string);

        } catch (NumberFormatException exception) {
            log.error(exception);
        }

        return parsedValue;
    }
}
