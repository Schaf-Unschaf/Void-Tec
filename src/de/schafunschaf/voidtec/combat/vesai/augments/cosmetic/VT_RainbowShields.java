package de.schafunschaf.voidtec.combat.vesai.augments.cosmetic;

import com.fs.starfarer.api.combat.ShipAPI;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;

import java.awt.Color;
import java.util.Random;

public class VT_RainbowShields extends AugmentData {

    public VT_RainbowShields() {
        this.augmentID = VT_Augments.VT_RAINBOW_SHIELDS;
        this.manufacturer = "VoidTec";
        this.name = "Rainbow Shields";
        this.description = new TextWithHighlights(
                "Bored of always looking at that static and dull shield matrix in front of you? Try the all new '==Rainbow Crystal " +
                        "Matrix==' for your ship and kiss those " +
                        "boring visuals goodbye!");
        this.rarity = 10;
        this.primarySlot = SlotCategory.SPECIAL;
        this.augmentQualityRange = new String[]{AugmentQuality.UNIQUE.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your shield change color over time.");
        this.combatScript = new CombatScriptRunner() {
            private final Random random = new Random();
            private final Color startColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
            private final ColorShifter colorShifter = new ColorShifter(startColor);

            @Override
            public void run(ShipAPI ship, float amount, Object data) {
                Color shiftColor = colorShifter.shiftColor(0.25f);
                ship.getShield().setInnerColor(shiftColor);
                ship.getShield().setRingColor(shiftColor);
            }
        };
    }
}
