package de.schafunschaf.voidtec.combat.vesai.augments.cosmetic;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;

public class VT_RainbowShields extends AugmentData {

    public VT_RainbowShields() {
        this.augmentID = VT_Augments.VT_RAINBOW_SHIELDS;
        this.manufacturer = "VoidTec";
        this.name = "Rainbow Shields";
        this.description = new TextWithHighlights(
                "Bored of always looking at that static and dull barrier in front of your ship? Try the all new " +
                        "'==Rainbow Crystal Shield Matrix==' and kiss those boring visuals goodbye!", null);
        this.rarity = 10;
        this.primarySlot = SlotCategory.COSMETIC;
        this.augmentQualityRange = new String[]{AugmentQuality.CUSTOMISED.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your shield change color over time.", null);
        this.combatScriptPath = "de.schafunschaf.voidtec.combat.scripts.fx.RainbowShieldEffect";
        storeAugment();
    }

}
