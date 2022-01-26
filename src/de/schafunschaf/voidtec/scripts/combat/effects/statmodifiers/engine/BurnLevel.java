package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.engine;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;
import java.util.Random;

public class BurnLevel extends BaseStatMod {

    public BurnLevel(String statID) {
        super(statID);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() != SlotCategory.FLIGHT_DECK) {
            stats.getMaxBurnLevel().modifyFlat(id, Math.round(generateModValue(statModValue, random, parentAugment.getAugmentQuality())));
        }
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getMaxBurnLevel().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getMaxBurnLevel().getFlatStatMod(id);

        String description = "Burn level %s by %s";
        if (ComparisonTools.isNull(statMod)) {
            Float fighterStatValue = parentAugment.getFighterStatValue(id + "_" + statID);
            if (!ComparisonTools.isNull(fighterStatValue)) {
                description = "(Fighter) " + description;
                statMod = new MutableStat.StatMod(id + "_" + statID, null, fighterStatValue);
            } else {
                return;
            }
        }
        generateTooltip(tooltip, statMod, description, bulletColor, false);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue) {
        boolean isPositive = minValue >= 0;
        String incDec = isPositive ? "Increases" : "Decreases";
        String hlString = "burn level";
        String description = String.format("the ships maximum %s", hlString);

        generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, false, hlString);
    }

    @Override
    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, Color bulletColor,
                                   boolean flipColors) {
        setBulletMode(tooltip, bulletColor);
        int value = (int) statMod.value;
        boolean isPositive = value >= 0f;
        String incDec = isPositive ? "increased" : "decreased";
        if (flipColors) {
            isPositive = !isPositive;
        }
        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        tooltip.addPara(description, 0f, new Color[]{bulletColor, hlColor, hlColor}, incDec, String.valueOf(Math.abs(value)));
        unindent(tooltip);
    }
}
