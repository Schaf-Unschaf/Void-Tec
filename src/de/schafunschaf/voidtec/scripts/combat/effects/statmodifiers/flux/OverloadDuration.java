package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.flux;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;
import java.util.Random;

public class OverloadDuration extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue,
                      Random random, AugmentQuality quality) {
        stats.getOverloadTimeMod().modifyPercent(id, generateModValue(statModValue, random, quality));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getOverloadTimeMod().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getOverloadTimeMod().getPercentBonus(id);
        if (ComparisonTools.isNull(statMod)) {
            return;
        }

        String description = "Overload time %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, true);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue) {
        boolean isPositive = avgModValue <= 0;
        String incDec = isPositive ? "Shortens" : "Lengthens";
        String hlString = "'Overloaded'";
        String description = String.format("the time needed to exit the %s-state", hlString);

        generateStatDescription(tooltip, description, incDec, bulletColor, isPositive, hlString);
    }
}
