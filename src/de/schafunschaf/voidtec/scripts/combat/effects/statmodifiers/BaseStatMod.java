package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers;

import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public abstract class BaseStatMod implements StatApplier {
    protected float generateModValue(StatModValue<Float, Float, Boolean> statModValue, Random random, AugmentQuality quality) {
        float qualityModifier = isNull(quality) ? 1f : quality.getModifier();
        float value;

        if (statModValue.minValue <= statModValue.maxValue) {
            value = statModValue.minValue;
        } else {
            int valueRange = (int) (statModValue.maxValue - statModValue.minValue);
            boolean isPositive = valueRange >= 0;
            value = random.nextInt(Math.abs(valueRange) + 1) + Math.abs(statModValue.minValue);
            if (!isPositive)
                value = -value;
        }

        float modifiedValue = statModValue.getsModified ? value * qualityModifier : value;

        return Math.round(modifiedValue);
    }

    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, Color bulletColor, boolean flipColors) {
        int value = (int) statMod.value;
        boolean isPositive = value >= 0f;
        String bullet = "•";
        String incDec = isPositive ? "increased" : "decreased";

        if (flipColors)
            isPositive = !isPositive;

        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();

        tooltip.addPara("%s " + description, 0f, new Color[]{bulletColor, hlColor, hlColor}, bullet, incDec, Math.abs(value) + "%");
    }

    protected void generateStatDescription(TooltipMakerAPI tooltip, String description, String incDecText, Color bulletColor, boolean isPositive, String... highlights) {
        String bullet = "•";
        Color incDecColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        List<String> hlStrings = new ArrayList<>();
        List<Color> hlColors = new ArrayList<>();

        hlColors.add(bulletColor);
        hlColors.add(incDecColor);
        hlStrings.add(bullet);
        hlStrings.add(incDecText);
        for (String highlight : highlights) {
            hlStrings.add(highlight);
            hlColors.add(Misc.getHighlightColor());
        }

        tooltip.addPara(String.format("%s %s %s", bullet, incDecText, description), 0f, hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]));
    }
}
