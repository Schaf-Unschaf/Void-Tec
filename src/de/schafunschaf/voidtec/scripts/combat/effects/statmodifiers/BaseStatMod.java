package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers;

import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.UpgradeQuality;

import java.awt.*;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public abstract class BaseStatMod implements StatApplier {
    protected float generateModValue(StatModValue<Float, Float, Boolean> statModValue, Random random, UpgradeQuality quality) {
        float qualityModifier = isNull(quality) ? 1f : quality.getModifier();
        int valueRange = (int) (statModValue.maxValue - statModValue.minValue);
        boolean isPositive = valueRange >= 0;
        float value = random.nextInt(Math.abs(valueRange) + 1) + Math.abs(statModValue.minValue);
        float modifiedValue = statModValue.getsModified ? value * qualityModifier : value;

        if (!isPositive)
            modifiedValue = -modifiedValue;

        return Math.round(modifiedValue);
    }

    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, Color bulletColor, boolean flipColors) {
        int value = (int) statMod.value;
        boolean isPositive = value >= 0f;
        String incDec = isPositive ? "increased" : "decreased";

        if (flipColors)
            isPositive = !isPositive;

        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();

        tooltip.addPara("%s " + description, 0f, new Color[]{bulletColor, hlColor, hlColor}, "•", incDec, Math.abs(value) + "%");
    }
}
