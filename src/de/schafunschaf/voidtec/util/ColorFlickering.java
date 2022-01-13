package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Random;

public class ColorFlickering {
    private boolean isFlickeringNow = false;
    private boolean pausedBetween = false;
    private final float minIntensity;
    private final float maxIntensity;
    private final int maxFramesBeforeRestart;
    private final int minFramesBeforeRestart;
    private final int maxFramesBetweenFlicker;
    private final int maxNumOfFlickers;
    private final int maxFlickerSpeed;
    private final int minFlickerSpeed;
    private final int maxSpeedAfterLastFlicker;

    private float currentIntensity;
    private int framesBetweenRestart;
    private int flickerSpeed;
    private int numOfFlickers;

    float frameCounter = 0f;

    public ColorFlickering(float minIntensity, float maxIntensity, int maxFramesBeforeRestart, int minFramesBeforeRestart, int maxFramesBetweenFlicker, int maxNumOfFlickers, int maxFlickerSpeed, int minFlickerSpeed, int maxSpeedAfterLastFlicker) {
        this.minIntensity = minIntensity * 100f;
        this.maxIntensity = maxIntensity * 100f;
        this.maxFramesBeforeRestart = maxFramesBeforeRestart;
        this.minFramesBeforeRestart = minFramesBeforeRestart;
        this.maxFramesBetweenFlicker = maxFramesBetweenFlicker;
        this.maxNumOfFlickers = maxNumOfFlickers;
        this.maxFlickerSpeed = maxFlickerSpeed;
        this.minFlickerSpeed = minFlickerSpeed;
        this.maxSpeedAfterLastFlicker = maxSpeedAfterLastFlicker;

        this.framesBetweenRestart = new Random().nextInt(Math.max(maxFramesBeforeRestart + 1 - minFramesBeforeRestart, 1)) + minFramesBeforeRestart;
        this.currentIntensity = this.maxIntensity;
    }

    public Color renderFlicker(Color color) {
        generateFlicker();
        return Misc.scaleColor(color, getIntensity());
    }

    public float getIntensity() {
        return currentIntensity / 100f;
    }

    private void generateFlicker() {
        if (isFlickeringNow) { // Color is in flicker-mode?
            if (numOfFlickers > 0) { // are there flickers left?
                if (pausedBetween) { // is paused at max between each flicker
                    if (frameCounter < maxFramesBetweenFlicker) { // time left in pause mode
                        frameCounter++;
                    } else { // end the pause
                        frameCounter = 0;
                        currentIntensity = minIntensity;
                        pausedBetween = false;
                        numOfFlickers--;
                        flickerSpeed = new Random().nextInt(Math.max((numOfFlickers == 0 ? maxSpeedAfterLastFlicker : maxFlickerSpeed) + 1 - minFlickerSpeed, 1)) + minFlickerSpeed;
                    }
                } else if (currentIntensity >= maxIntensity) { // light recovered after flicker?
                    pausedBetween = true;
                } else if (currentIntensity + flickerSpeed > maxIntensity) { // check if intensity doesn't exceed max
                    currentIntensity = maxIntensity;
                } else { // increase intensity after flicker
                    currentIntensity += flickerSpeed;
                }
            } else { // flickering finished
                isFlickeringNow = false;
            }
        } else if (frameCounter < framesBetweenRestart) { // flicker still in cooldown?
            frameCounter++;
        } else { // start next flickering cycle
            frameCounter = 0;
            isFlickeringNow = true;
            numOfFlickers = new Random().nextInt(maxNumOfFlickers);
            flickerSpeed = new Random().nextInt(Math.max(maxFlickerSpeed + 1 - minFlickerSpeed, 1)) + minFlickerSpeed;
            framesBetweenRestart = new Random().nextInt(Math.max(maxFramesBeforeRestart + 1 - minFramesBeforeRestart, 1)) + minFramesBeforeRestart;
        }
    }
}
