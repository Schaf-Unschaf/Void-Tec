package de.schafunschaf.voidtec.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    public static float getAverage(float minValue, float maxValue) {
        return (maxValue - minValue) / 2 + minValue;
    }

    public static int roundWholeNumber(int number, int numPlaces) {
        double pow = Math.pow(10, numPlaces);
        return (int) (Math.round(number / pow) * pow);
    }

    public static float roundWholeNumber(float number, int numPlaces) {
        double pow = Math.pow(10, numPlaces);
        return (float) (Math.round(number / pow) * pow);
    }

    public static double roundWholeNumber(double number, int numPlaces) {
        double pow = Math.pow(10, numPlaces);
        return (Math.round(number / pow) * pow);
    }

    public static float roundDecimals(float number, int numDecimals) {
        BigDecimal bigDecimal = new BigDecimal(number);
        return bigDecimal.setScale(numDecimals, RoundingMode.HALF_UP).floatValue();
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }
}
