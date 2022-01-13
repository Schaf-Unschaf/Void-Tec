package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.special;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.*;
import java.util.Random;

public class SModSlot extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random, AugmentQuality quality) {
        stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(id, generateModValue(statModValue, random, quality));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Permanent Build-In slots %s by %s";

        int value = (int) statMod.value;
        int maxValue = stats.getDynamic().getStat(Stats.MAX_PERMANENT_HULLMODS_MOD).getModifiedInt();
        boolean isPositive = value >= 0f;
        String bullet = "•";
        String incDec = isPositive ? "increased" : "decreased";

        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();

        tooltip.addPara("%s " + description, 0f, new Color[]{bulletColor, hlColor, hlColor}, bullet, incDec, String.valueOf(Math.abs(value)));
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue) {
        boolean isPositive = avgModValue >= 0;
        String incDec = isPositive ? "Increases" : "Decreases";
        String hlString = "permanent hullmods";
        String description = String.format("the ships maximum amount of %s", hlString);

        generateStatDescription(tooltip, description, incDec, bulletColor, isPositive, hlString);
    }
}
