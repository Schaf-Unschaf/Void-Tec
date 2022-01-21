package de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ColorShifter;
import de.schafunschaf.voidtec.util.TextWithHighlights;

import java.awt.*;

public class VT_RainbowEngines extends AugmentData {
    public static final String AUGMENT_ID = "vt_rainbowEngines";

    public VT_RainbowEngines() {
        this.augmentID = AUGMENT_ID;
        this.manufacturer = "VoidTec";
        this.name = "Rainbow Engines";
        this.description = new TextWithHighlights("Bored of those static and dull engine flames? Try the all new '==Rainbow Fuel Addition==' for your ship and kiss those boring exhausts goodbye!");
        this.rarity = 10;
        this.primarySlot = SlotCategory.SPECIAL;
        this.augmentQualityRange = new String[]{AugmentQuality.UNIQUE.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your engine flames change color over time.");
        this.combatScript = new CombatScriptRunner() {
            private final ColorShifter colorShifter = new ColorShifter(Color.ORANGE);

            @Override
            public void run(ShipAPI ship, float amount) {
                Color shiftColor = colorShifter.shiftColor(0.25f);
                ship.getEngineController().fadeToOtherColor(augmentID, shiftColor, Misc.scaleColor(shiftColor, 0.3f), 1f, 1f);
            }
        };
    }
}
