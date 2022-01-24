package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.system;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;
import java.util.Random;

public class SystemUses extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue,
                      Random random, AugmentQuality quality) {
        stats.getSystemUsesBonus().modifyFlat(id, Math.round(generateModValue(statModValue, random, quality)));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getSystemUsesBonus().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getSystemUsesBonus().getFlatBonus(id);
        if (ComparisonTools.isNull(statMod)) {
            return;
        }

        String description = "System charge amount %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, false);
    }

    @Override
    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description,
                                   Color bulletColor, boolean flipColors) {
        float value = statMod.value;
        boolean isPositive = value >= 1f;
        String bullet = "•";
        String incDec = isPositive ? "increased" : "decreased";
        if (flipColors) {
            isPositive = !isPositive;
        }
        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        tooltip.addPara("%s " + description, 0f, new Color[]{bulletColor, hlColor,
                                                             hlColor}, bullet, incDec, String.valueOf(Math.abs(Math.round(value))));
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue) {
        boolean isPositive = avgModValue >= 0;
        String incDec = isPositive ? "Increases" : "Decreases";
        String hlString = "maximum amount";
        String hlString2 = "system ability charges";
        String description = String.format("the %s of the ships %s", hlString, hlString2);

        generateStatDescription(tooltip, description, incDec, bulletColor, isPositive, hlString, hlString2);
    }
}
