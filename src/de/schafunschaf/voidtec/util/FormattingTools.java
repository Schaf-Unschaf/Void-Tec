package de.schafunschaf.voidtec.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
}
