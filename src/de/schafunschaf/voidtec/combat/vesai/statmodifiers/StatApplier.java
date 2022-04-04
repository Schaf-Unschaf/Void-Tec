package de.schafunschaf.voidtec.combat.vesai.statmodifiers;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;

import java.awt.Color;

public interface StatApplier {

    void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed,
                     AugmentApplier parentAugment);

    void applyToFighter(MutableShipStatsAPI stats, String id, float value);

    void remove(MutableShipStatsAPI stats, String id);

    void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                              AugmentApplier parentAugment);

    LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue);

    void runCombatScript(ShipAPI ship, float amount, AugmentApplier augment);

    String getStatID();

    String getDisplayName();

    void collectStatValue(float value, AugmentApplier parentAugment);

    // true if a negative (ex: -10 overload duration) value grants a benefit
    boolean hasNegativeValueAsBenefit();

    boolean isPercentage();
}
