package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.engine;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.*;
import java.util.Random;

public class BurnLevel extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random, UpgradeQuality quality) {
        stats.getMaxBurnLevel().modifyFlat(id, Math.round(generateModValue(statModValue, random, quality)));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getMaxBurnLevel().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getMaxBurnLevel().getFlatStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Burn level %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, false);
    }

    @Override
    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, Color bulletColor, boolean flipColors) {
        int value = (int) statMod.value;
        boolean isPositive = value >= 0f;
        String incDec = isPositive ? "increased" : "decreased";
        if (flipColors)
            isPositive = !isPositive;
        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        tooltip.addPara("%s " + description, 0f, new Color[]{bulletColor, hlColor, hlColor}, "•", incDec, String.valueOf(Math.abs(value)));
    }
}
