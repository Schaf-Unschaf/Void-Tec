package de.schafunschaf.voidtec.combat.vesai.statmodifiers;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;

import java.awt.Color;
import java.util.Random;

public interface StatApplier {

    void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
                     AugmentApplier parentAugment);

    void applyToFighter(MutableShipStatsAPI stats, String id, float value);

    void remove(MutableShipStatsAPI stats, String id);

    void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                              AugmentApplier parentAugment);

    void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue);

    void runCombatScript(ShipAPI ship, float amount, Object data);

    String getStatID();
}
