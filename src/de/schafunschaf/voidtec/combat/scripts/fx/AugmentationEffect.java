package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.ids.VT_Colors;
import de.schafunschaf.voidtec.util.MathUtils;

import java.awt.Color;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentationEffect {

    private static final String EFFECT_KEY = "vt_augmentationEffectKey";
    private static final String INTERVAL_TRACKER = "vt_effectIntervalTracker";
    private static final String ACTIVE_TRACKER = "vt_effectActiveTracker";
    private static final String COLOR_MEMORY = "vt_effectColorMemory";
    private static final float EFFECT_INTERVAL = 6f;
    private static final float ACTIVE_DURATION = 3f;

    public static void run(ShipAPI ship, float amount, Object source) {
        MutableStat stat = ship.getMutableStats().getDynamic().getStat(EFFECT_KEY);

        MutableStat.StatMod intervalTracker = getTracker(stat, INTERVAL_TRACKER, EFFECT_INTERVAL, true);
        MutableStat.StatMod activeTracker = getTracker(stat, ACTIVE_TRACKER, amount, false);
        MutableStat.StatMod colorTracker = getTracker(stat, COLOR_MEMORY, VT_Colors.VT_COLOR_MAIN.darker().darker().getRGB(), false);

        float intervalTrackerValue = intervalTracker.getValue();

        if (intervalTrackerValue > EFFECT_INTERVAL) {
            float activeTrackerValue = activeTracker.getValue();

            if (activeTrackerValue < ACTIVE_DURATION) {
                Color jitterColor = new Color((int) colorTracker.getValue());
                float halfDuration = ACTIVE_DURATION / 2;
                float colorFactor = activeTrackerValue < halfDuration ? activeTrackerValue : ACTIVE_DURATION - activeTrackerValue;
                colorFactor = MathUtils.clamp(colorFactor, 0f, 1f);

                ship.setJitterUnder(source, Misc.scaleColor(jitterColor, colorFactor), 1f, 4, 15f);

                stat.modifyFlat(ACTIVE_TRACKER, activeTrackerValue + amount);
            } else {
                stat.modifyFlat(ACTIVE_TRACKER, amount);
                stat.modifyFlat(INTERVAL_TRACKER, amount);

                pickNextColor(ship, stat);
            }
        } else {
            stat.modifyFlat(INTERVAL_TRACKER, intervalTrackerValue + amount);
        }
    }

    private static void pickNextColor(ShipAPI ship, MutableStat stat) {
        WeightedRandomPicker<Color> colorPicker = new WeightedRandomPicker<>();
        HullModManager hullmodManager = HullModDataStorage.getInstance().getHullModManager(ship.getFleetMemberId());

        for (AugmentSlot filledSlot : hullmodManager.getFilledSlots()) {
            colorPicker.add(filledSlot.getSlotCategory().getColor());
        }

        if (!colorPicker.isEmpty()) {
            stat.modifyFlat(COLOR_MEMORY, colorPicker.pick().getRGB());
        }
    }

    private static MutableStat.StatMod getTracker(MutableStat stat, String key, float initialValue, boolean randomInitValue) {
        MutableStat.StatMod timedTracker = stat.getFlatStatMod(key);
        if (isNull(timedTracker)) {
            float value = randomInitValue ? new Random().nextInt((int) initialValue + 1) + 0.1f : initialValue;
            stat.modifyFlat(key, value);
            timedTracker = stat.getFlatStatMod(key);
        }
        return timedTracker;
    }
}
