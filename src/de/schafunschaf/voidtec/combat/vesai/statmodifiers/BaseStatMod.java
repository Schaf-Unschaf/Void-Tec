package de.schafunschaf.voidtec.combat.vesai.statmodifiers;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Strings;
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
    protected final String displayName;

    public static void generateStatTooltip(TooltipMakerAPI tooltip, String statID, int value) {
        BaseStatMod statMod = StatModProvider.getStatMod(statID);
        String text = "%s %s by %s";
        statMod.generateTooltip(tooltip, value, text, null);
    }

    protected int generateModValue(StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed, AugmentQuality quality) {
        float qualityModifier = isNull(quality) ? 1f : quality.getModifier();
        float value;
        Random random = new Random(randomSeed);

        if (Math.abs(statModValue.minValue) >= Math.abs(statModValue.maxValue)) {
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
    public void runCombatScript(ShipAPI ship, float amount, AugmentApplier augment) {}

    @Override
    public void collectStatValue(float value, AugmentApplier parentAugment, boolean negativeBenefits) {
        parentAugment.getInstalledSlot().getHullModManager().addStatModifier(statID, value, isMult());
    }

    @Override
    public boolean hasNegativeValueAsBenefit() {
        return false;
    }

    @Override
    public boolean isMult() {
        return true;
    }

    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, Color bulletColor,
                                   AugmentApplier parentAugment) {
        int value = isMult() ? (int) (100 * statMod.value) - 100 : (int) statMod.value;

        if (isNull(tooltip)) {
            collectStatValue(value, parentAugment, hasNegativeValueAsBenefit());
            return;
        }

        generateTooltip(tooltip, value, description, bulletColor);
    }

    private void generateTooltip(TooltipMakerAPI tooltip, int value, String text, Color bulletColor) {
        BaseStatMod statMod = StatModProvider.getStatMod(statID);
        String percentageSign = isMult() ? "%" : "";
        boolean isPositive = value >= 0;
        String incDec = isPositive ? "increased" : "decreased";

        if (hasNegativeValueAsBenefit()) {
            isPositive = !isPositive;
        }

        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();

        setBulletMode(tooltip, isNull(bulletColor) ? hlColor : bulletColor);
        tooltip.addPara(text, 0f, new Color[]{hlColor, Misc.getTextColor(), hlColor}, statMod.displayName, incDec,
                        Math.abs(value) + percentageSign);
        unindent(tooltip);
    }

    protected LabelAPI generateStatDescription(TooltipMakerAPI tooltip, String description, String incDecText, Color bulletColor,
                                               float minValue, float maxValue, boolean isPositive, String... highlights) {
        setBulletMode(tooltip, bulletColor);
        String percentageSign = isMult() ? "%" : "";
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

        LabelAPI generatedLabel;

        if (roundedMinValue == roundedMaxValue) {
            generatedLabel = tooltip.addPara(String.format("%s %s by %s", incDecText, description, minValueString + percentageSign), 0f,
                                             hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]));
        } else {
            generatedLabel = tooltip.addPara(String.format("%s %s between %s and %s", incDecText, description,
                                                           minValueString + percentageSign,
                                                           maxValueString + percentageSign), 0f, hlColors.toArray(new Color[0]),
                                             hlStrings.toArray(new String[0]));
        }

        unindent(tooltip);

        return generatedLabel;
    }

    protected void setBulletMode(TooltipMakerAPI tooltip, Color bulletColor) {
        String bullet = VT_Strings.BULLET_CHAR + " ";
        tooltip.setBulletColor(bulletColor);
        tooltip.setBulletedListMode(bullet);
    }

    protected void unindent(TooltipMakerAPI tooltip) {
        tooltip.setBulletedListMode(null);
    }
}
