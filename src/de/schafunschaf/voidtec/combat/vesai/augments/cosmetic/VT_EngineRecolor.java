package de.schafunschaf.voidtec.combat.vesai.augments.cosmetic;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;

public class VT_EngineRecolor extends AugmentData {

    public VT_EngineRecolor() {
        this.augmentID = VT_Augments.VT_ENGINE_RECOLOR;
        this.manufacturer = "VoidTec";
        this.name = "Variable Flame Colorizer";
        this.description = new TextWithHighlights(
                "Bored of those always same and boring looking engine flames? Try the all new '==Exhaust Colorizer==' for your ship and " +
                        "kiss those boring exhausts goodbye!", null);
        this.rarity = 20;
        this.primarySlot = SlotCategory.COSMETIC;
        this.augmentQualityRange = new String[]{AugmentQuality.CUSTOMISED.name()};
        this.combatScriptDescription = new TextWithHighlights("Makes your engine flames change to your ==specified color==.", null);
        this.rightClickActionPath = "de.schafunschaf.voidtec.combat.scripts.interactions.OpenColorPickerAction";
        this.combatScriptPath = "de.schafunschaf.voidtec.combat.scripts.fx.EngineRecolorEffect";
        storeAugment();
    }

}
