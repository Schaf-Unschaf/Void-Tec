package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Random;

public class MalfunctionEffect {
    private final float breathingLength;
    private final float maxTimeAtFull;
    private final int flickerChance;
    private final int maxNumFlickers;
    private final float speedModifier;

    private boolean isBreathingIn = false;
    private float counter;
    private float timeSpentOn = 0f;
    private boolean isFlickering = false;
    private int numFlickers = 0;
    private float flickerSpeed = 0f;

    /**
     * Renders a breathing-effect with flashing malfunctions.
     * The malfunctions occur only when the color is at the end
     * of its 'breath', aka at full strength.
     *
     * @param breathingLength the length in frames of a full cycle (in-out-in) without malfunctions
     * @param maxTimeAtFull amount of frames the 'breath' is getting hold (full intensity)
     * @param flickerChance chance to trigger a malfunction (chance/10_000)
     * @param maxNumFlickers maximum number of malfunction flickers
     * @param speedModifier modifies the speed of the breathing and flickering
     */
    public MalfunctionEffect(float breathingLength, float maxTimeAtFull, int flickerChance, int maxNumFlickers, float speedModifier) {
        this.breathingLength = breathingLength;
        this.maxTimeAtFull = maxTimeAtFull;
        this.flickerChance = flickerChance;
        this.maxNumFlickers = maxNumFlickers;
        this.speedModifier = speedModifier;
        this.counter = breathingLength;
    }

    public Color renderFlicker(Color color) {
        generateGlowAndFlicker();
        return Misc.scaleColor(color, getIntensity());
    }

    public float getIntensity() {
        return Math.min(Math.max(counter / breathingLength, 0f), 1f);
    }

    private void generateGlowAndFlicker() {
        int baseFrameChange = 10;

        if (isFlickering) {
            if (numFlickers > 0) {
                if (counter < breathingLength) {
                    counter += flickerSpeed;
                } else {
                    counter = 0f;
                    flickerSpeed = new Random().nextInt(20) + baseFrameChange;
                    numFlickers--;
                }
            } else if (counter < breathingLength) {
                counter += speedModifier * baseFrameChange;
            } else {
                isFlickering = false;
            }
        } else {
            if (isBreathingIn) {
                if (counter >= breathingLength) {
                    if (timeSpentOn < maxTimeAtFull) {
                        if (new Random().nextInt(10000) <= flickerChance) {
                            counter = 0f;
                            numFlickers = new Random().nextInt(maxNumFlickers) + 1;
                            flickerSpeed = new Random().nextInt(baseFrameChange * 2) + baseFrameChange;
                            isFlickering = true;
                        }
                        timeSpentOn++;
                    } else {
                        timeSpentOn = 0f;
                        isBreathingIn = false;
                    }
                } else {
                    counter += speedModifier;
                }
            } else {
                if (counter <= breathingLength / 2) {
                    isBreathingIn = true;
                } else {
                    counter -= speedModifier;
                }
            }
        }
    }
}
