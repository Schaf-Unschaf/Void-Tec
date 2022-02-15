package de.schafunschaf.voidtec.combat.vesai.statmodifiers;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@RequiredArgsConstructor
public abstract class BaseStatMod implements StatApplier {

    protected final String statID;

    protected int generateModValue(StatModValue<Float, Float, Boolean> statModValue, Random random, AugmentQuality quality) {
        float qualityModifier = isNull(quality) ? 1f : quality.getModifier();
        float value;

        if (statModValue.minValue >= statModValue.maxValue) {
            value = statModValue.minValue;
        } else {
            int valueRange = Math.round(statModValue.maxValue) - Math.round(statModValue.minValue);
            boolean isPositive = valueRange >= 0;
            value = random.nextInt(Math.abs(valueRange) + 1) + Math.abs(statModValue.minValue);
            if (!isPositive) {
                value = -value;
            }
        }

        float modifiedValue = statModValue.getsModified ? value * qualityModifier : value;

        return Math.round(modifiedValue);
    }

    @Override
    public void applyToFighter(MutableShipStatsAPI stats, String id, float value) {}

    @Override
    public void runCombatScript(ShipAPI ship, float amount, Object data) {}

    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, Color bulletColor,
                                   boolean flipColors, boolean isPercentage) {
        setBulletMode(tooltip, bulletColor);
        String percentageSign = isPercentage ? "%" : "";
        int value = (int) (100 * statMod.value) - 100;
        boolean isPositive = value >= 0f;
        String incDec = isPositive ? "increased" : "decreased";

        if (flipColors) {
            isPositive = !isPositive;
        }

        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();

        tooltip.addPara(description, 0f, new Color[]{hlColor, hlColor}, incDec, Math.abs(value) + percentageSign);
        unindent(tooltip);
    }

    protected void generateStatDescription(TooltipMakerAPI tooltip, String description, String incDecText, Color bulletColor,
                                           float minValue, float maxValue, boolean isPositive, boolean isPercentage, String... highlights) {
        setBulletMode(tooltip, bulletColor);
        String percentageSign = isPercentage ? "%" : "";
        int roundedMinValue = Math.round(Math.abs(minValue));
        int roundedMaxValue = Math.round(Math.abs(maxValue));
        String minValueString = roundedMinValue + percentageSign;
        String maxValueString = roundedMaxValue + percentageSign;

        Color incDecColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        List<String> hlStrings = new ArrayList<>();
        List<Color> hlColors = new ArrayList<>();

        hlColors.add(incDecColor);
        hlStrings.add(incDecText);
        for (String highlight : highlights) {
            hlColors.add(Misc.getHighlightColor());
            hlStrings.add(highlight);
        }
        hlColors.add(incDecColor);
        hlColors.add(incDecColor);
        hlStrings.add(minValueString);
        hlStrings.add(maxValueString);

        if (roundedMinValue == roundedMaxValue) {
            tooltip.addPara(String.format("%s %s by %s", incDecText, description, minValueString + percentageSign), 0f,
                            hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]));
        } else {
            tooltip.addPara(String.format("%s %s between %s and %s", incDecText, description, minValueString + percentageSign,
                                          maxValueString + percentageSign), 0f, hlColors.toArray(new Color[0]),
                            hlStrings.toArray(new String[0]));
        }

        unindent(tooltip);
    }

    protected void setBulletMode(TooltipMakerAPI tooltip, Color bulletColor) {
        String bullet = "• ";
        tooltip.setBulletColor(bulletColor);
        tooltip.setBulletedListMode(bullet);
    }

    protected void unindent(TooltipMakerAPI tooltip) {
        tooltip.setBulletedListMode(null);
    }
}
