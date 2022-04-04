package de.schafunschaf.voidtec.combat.vesai.augments.cosmetic;

import com.fs.starfarer.api.combat.ShipAPI;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;

import java.awt.Color;

public class VT_RainbowShields extends AugmentData {

    public VT_RainbowShields() {
        this.augmentID = VT_Augments.VT_RAINBOW_SHIELDS;
        this.manufacturer = "VoidTec";
        this.name = "Rainbow Shields";
        this.description = new TextWithHighlights(
                "Bored of always looking at that static and dull barrier in front of your ship? Try the all new " +
                        "'==Rainbow Crystal Shield Matrix==' and kiss those boring visuals goodbye!");
        this.rarity = 10;
        this.primarySlot = SlotCategory.COSMETIC;
        this.augmentQualityRange = new String[]{AugmentQuality.CUSTOMISED.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your shield change color over time.");
        this.combatScript = new CombatScriptRunner() {
            private final ColorShifter colorShifter = new ColorShifter(null);

            @Override
            public void run(ShipAPI ship, float amount, AugmentApplier augment) {
                Color shiftColor = colorShifter.shiftColor(0.4f);
                ship.getShield().setInnerColor(shiftColor.darker().darker());
                ship.getShield().setRingColor(shiftColor);
            }
        };
        storeAugment();
    }
}
