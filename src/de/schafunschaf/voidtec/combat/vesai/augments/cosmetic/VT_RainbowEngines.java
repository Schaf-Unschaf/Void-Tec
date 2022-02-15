package de.schafunschaf.voidtec.combat.vesai.augments.cosmetic;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModProvider;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;
import de.schafunschaf.voidtec.ids.VT_StatModKeys;

import java.awt.Color;
import java.util.ArrayList;

public class VT_RainbowEngines extends AugmentData {

    public VT_RainbowEngines() {
        this.augmentID = VT_Augments.VT_RAINBOW_ENGINES;
        this.manufacturer = "VoidTec";
        this.name = "Rainbow Engines";
        this.description = new TextWithHighlights(
                "Bored of those static and dull engine flames? Try the all new '==Rainbow Fuel Addition==' for your ship and kiss those boring exhausts goodbye!");
        this.rarity = 10;
        this.primarySlot = SlotCategory.COSMETIC;
        this.primaryStatMods = new ArrayList<>();
        this.primaryStatMods.add(StatModProvider.getStatMod(VT_StatModKeys.SHIP_MAX_SPEED));
        this.primaryStatValues = new ArrayList<>();
        this.primaryStatValues.add(new StatModValue<>(50f, 100f, true));
        this.augmentQualityRange = new String[]{AugmentQuality.UNIQUE.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your engine flames change color over time.");
        this.combatScript = new CombatScriptRunner() {
            private final ColorShifter colorShifter = new ColorShifter(null);

            @Override
            public void run(ShipAPI ship, float amount, Object data) {
                Color shiftColor = colorShifter.shiftColor(0.4f);
                ship.getEngineController().fadeToOtherColor(augmentID, shiftColor, Misc.scaleColor(shiftColor, 0.3f), 1f, 1f);
            }
        };
        this.additionalDescription = new TextWithHighlights("Test ==additionalDescription Field==.");
    }
}
