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

public class VT_TravelDrives extends AugmentData {

    public static final Color engineColor = new Color(200, 0, 255, 255);

    public VT_TravelDrives() {
        this.augmentID = VT_Augments.VT_TRAVEL_DRIVES;
        this.manufacturer = Factions.INDEPENDENT;
        this.name = "Travel Drives";
        this.description = new TextWithHighlights("Refits the ship for traveling long distances in hyperspace. Serious drawbacks when " +
                                                          "engaged during combat.", null);
        this.rarity = 30;
        this.primarySlot = SlotCategory.ENGINE;
        this.primaryStatMods = AugmentDataLoader.convertStatMods(VT_StatModKeys.BURN_LEVEL, VT_StatModKeys.FUEL_USE,
                                                                 VT_StatModKeys.SENSOR_PROFILE, VT_StatModKeys.ZERO_FLUX_SPEED,
                                                                 VT_StatModKeys.SHIP_MAX_SPEED, VT_StatModKeys.ENGINE_HEALTH);
        this.primaryStatValues = Arrays.asList(new StatModValue<>(2f, 4f, false, false),
                                               new StatModValue<>(30f, 45f, true, false),
                                               new StatModValue<>(30f, 45f, true, false),
                                               new StatModValue<>(30f, 50f, false, false),
                                               new StatModValue<>(10f, 20f, true, true),
                                               new StatModValue<>(15f, 25f, true, true));
        this.augmentQualityRange = new String[]{AugmentQuality.DEGRADED.name(), AugmentQuality.DOMAIN.name()};
        this.combatScriptPath = "de.schafunschaf.voidtec.combat.scripts.fx.TravelDriveEffect";
        this.uniqueMod = true;
        storeAugment();
    }

}
