package de.schafunschaf.voidtec.helper;

import java.awt.Color;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ColorShifter {

    private float hueValue;

    public ColorShifter(Color color) {
        this.hueValue = isNull(color)
                        ? new Random().nextInt(360)
                        : Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[0];
    }

    public float getNextHueValue(float shiftAmount) {
        if (hueValue + shiftAmount >= 360f) {
            hueValue = hueValue + shiftAmount - 360f;
        } else {
            hueValue += shiftAmount;
        }

        return hueValue;
    }

    public Color shiftColor(float shiftAmount) {
        return Color.getHSBColor(getNextHueValue(shiftAmount) / 360f, 1f, 1f);
    }
}
