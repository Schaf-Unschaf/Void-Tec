package de.schafunschaf.voidtec.helper;

import lombok.Setter;

import java.awt.Color;

public class ColorBreathing {

    private final float maxDuration;
    private final float changeMult;
    private final float maxIntensity;
    private final float minIntensity;

    @Setter
    private Color color;
    private float breathingCounter;
    private boolean breathingIn;

    public ColorBreathing(Color color, float maxDuration, float maxIntensity, float minIntensity, boolean startAtFull) {
        this.color = color;
        this.maxDuration = maxDuration;
        this.maxIntensity = maxIntensity;
        this.minIntensity = minIntensity;

        this.changeMult = (maxIntensity - minIntensity) / maxDuration;
        this.breathingIn = !startAtFull;
        this.breathingCounter = startAtFull ? maxDuration : 0f;
    }

    public Color breath(float amount, boolean withAlpha) {
        if (breathingIn) {
            if (breathingCounter < maxDuration * maxIntensity) {
                breathTick(amount, false);
            } else {
                changeDirection();
            }
        } else if (breathingCounter > maxDuration * minIntensity) {
            breathTick(amount, true);
        } else {
            changeDirection();
        }

        return updateColor(withAlpha);
    }

    private void breathTick(float amount, boolean reverse) {
        breathingCounter = reverse
                           ? breathingCounter - amount * changeMult
                           : breathingCounter + amount * changeMult;
    }

    private void changeDirection() {
        breathingIn = !breathingIn;
    }

    private Color updateColor(boolean withAlpha) {
        float changeAmount = Math.min(Math.max(breathingCounter / maxDuration, minIntensity), maxIntensity);
        return new Color((int) (color.getRed() * changeAmount),
                         (int) (color.getGreen() * changeAmount),
                         (int) (color.getBlue() * changeAmount),
                         (int) (withAlpha
                                ? color.getAlpha() * changeAmount
                                : color.getAlpha()));
    }
}
