package de.schafunschaf.voidtec.helper;

import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RainbowString {

    private final String originalString;
    private final String convertedString;
    private final Color[] hlColors;
    private final String[] hlStrings;

    public RainbowString(String inputString, Color startingColor, float colorShiftAmount) {
        ColorShifter colorShifter = new ColorShifter(startingColor);
        char[] charArray = inputString.toCharArray();
        List<Color> hlColors = new ArrayList<>();
        List<String> hlStrings = new ArrayList<>();
        StringBuilder outputString = new StringBuilder();

        for (char c : charArray) {
            if (c == ' ') {
                outputString.append(" ");
                continue;
            }

            outputString.append("%s ");
            hlColors.add(colorShifter.getCurrentColor());
            colorShifter.shiftColor(colorShiftAmount);
            hlStrings.add(Character.toString(c));
        }

        this.originalString = inputString;
        this.convertedString = outputString.toString();
        this.hlColors = hlColors.toArray(new Color[0]);
        this.hlStrings = hlStrings.toArray(new String[0]);
    }
}
