package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.sensor;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.*;
import java.util.Random;

public class SensorProfile extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random, AugmentQuality quality) {
        stats.getSensorProfile().modifyPercent(id, -generateModValue(statModValue, random, quality));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getSensorProfile().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getSensorProfile().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Sensor profile %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, true);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue) {
        boolean isPositive = avgModValue <= 0;
        String incDec = isPositive ? "Lowers" : "Increases";
        String hlString = "sensor profile";
        String description = String.format("the ships %s", hlString);

        generateStatDescription(tooltip, description, incDec, bulletColor, isPositive, hlString);
    }
}
