package de.schafunschaf.voidtec.combat.vesai.statmodifiers.logistic;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;

public class CargoCapacity extends BaseStatMod {

    public CargoCapacity(String statID, String displayName) {
        super(statID, displayName);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed,
                            AugmentApplier parentAugment) {
        stats.getCargoMod().modifyMult(id, 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getCargoMod().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getCargoMod().getMultBonus(id);
        if (ComparisonTools.isNull(statMod)) {
            return;
        }

        generateTooltip(tooltip, statMod, bulletColor, parentAugment);
    }

    @Override
    public LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue,
                                            boolean isFighterStat) {
        boolean isPositive = minValue >= 0;
        String incDec = isPositive ? "Increases" : "Reduces";
        String hlString = "cargo capacity";
        String description = String.format("the %s", hlString);

        return generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, isFighterStat,
                                       hlString);
    }

    @Override
    public boolean hasNegativeValueAsBenefit() {
        return false;
    }
}
