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

public class SuppliesPerMonth extends BaseStatMod {

    public SuppliesPerMonth(String statID, String displayName) {
        super(statID, displayName);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed,
                            AugmentApplier parentAugment) {
        stats.getSuppliesPerMonth().modifyMult(id, 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getSuppliesPerMonth().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getSuppliesPerMonth().getMultStatMod(id);
        String description = "Supply use per month %s by %s";
        if (ComparisonTools.isNull(statMod)) {
            return;
        }

        generateTooltip(tooltip, statMod, description, bulletColor, parentAugment);
    }

    @Override
    public LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue) {
        boolean isPositive = minValue <= 0;
        String incDec = isPositive ? "Reduces" : "Increases";
        String hlString = "supplies used per month";
        String description = String.format("the ships %s", hlString);

        return generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, hlString);
    }

    @Override
    public boolean hasNegativeValueAsBenefit() {
        return true;
    }
}
