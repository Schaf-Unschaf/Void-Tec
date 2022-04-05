package de.schafunschaf.voidtec.combat.vesai.augments.engine;

import com.fs.starfarer.api.impl.campaign.ids.Factions;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.AugmentDataLoader;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Augments;
import de.schafunschaf.voidtec.ids.VT_StatModKeys;

import java.awt.Color;
import java.util.Arrays;

public class VT_PursuitEngines extends AugmentData {

    public static final Color engineColor = new Color(0, 150, 255, 255);

    public VT_PursuitEngines() {
        this.augmentID = VT_Augments.VT_PURSUIT_ENGINES;
        this.manufacturer = Factions.PIRATES;
        this.name = "Pursuit Engines";
        this.description = new TextWithHighlights("Refits the ship to excel at pursuing enemies during combat. Suffers from drawbacks " +
                                                          "when traveling in hyperspace.", null);
        this.rarity = 30;
        this.primarySlot = SlotCategory.ENGINE;
        this.primaryStatMods = AugmentDataLoader.convertStatMods(VT_StatModKeys.ZERO_FLUX_SPEED, VT_StatModKeys.SHIP_ACCELERATION,
                                                                 VT_StatModKeys.SHIP_TURN, VT_StatModKeys.BURN_LEVEL,
                                                                 VT_StatModKeys.FUEL_USE, VT_StatModKeys.ARMOR_HEALTH);
        this.primaryStatValues = Arrays.asList(new StatModValue<>(30f, 50f, true, false),
                                               new StatModValue<>(20f, 40f, true, false),
                                               new StatModValue<>(20f, 40f, true, false),
                                               new StatModValue<>(-1f, -2f, false, false),
                                               new StatModValue<>(30f, 50f, true, false),
                                               new StatModValue<>(-10f, -15f, true, true));
        this.augmentQualityRange = new String[]{AugmentQuality.DEGRADED.name(), AugmentQuality.DOMAIN.name()};
        this.combatScriptPath = "de.schafunschaf.voidtec.combat.scripts.fx.PursuitEnginesEffect";
        this.uniqueMod = true;
        storeAugment();
    }

}
