package de.schafunschaf.voidtec.combat.vesai.augments.cosmetic;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;

public class VT_RainbowEngines extends AugmentData {

    public VT_RainbowEngines() {
        this.augmentID = VT_Augments.VT_RAINBOW_ENGINES;
        this.manufacturer = "VoidTec";
        this.name = "Rainbow Engines";
        this.description = new TextWithHighlights(
                "Bored of those static and dull engine flames? Try the all new '==Rainbow Fuel Addition==' for your ship and kiss those boring exhausts goodbye!",
                null);
        this.rarity = 10;
        this.primarySlot = SlotCategory.COSMETIC;
        this.augmentQualityRange = new String[]{AugmentQuality.CUSTOMISED.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your engine flames change color over time.", null);
        this.combatScriptPath = "de.schafunschaf.voidtec.combat.scripts.fx.RainbowEngineEffect";
        storeAugment();
    }

}
