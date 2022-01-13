package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.resistance;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.*;
import java.util.Random;

public class KineticShieldDamageTaken extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random, AugmentQuality quality) {
        stats.getKineticShieldDamageTakenMult().modifyPercent(id, -generateModValue(statModValue, random, quality));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getKineticShieldDamageTakenMult().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getKineticShieldDamageTakenMult().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Kinetic damage taken by shields %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, true);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue) {
        boolean isPositive = avgModValue <= 0;
        String incDec = isPositive ? "Reduces" : "Increases";
        String hlString1 = "damage taken";
        String hlString2 = "kinetic";
        String hlString3 = "shields";
        String description = String.format("the %s from all %s weapons on %s", hlString1, hlString2, hlString3);

        generateStatDescription(tooltip, description, incDec, bulletColor, isPositive, hlString1, hlString2, hlString3);
    }
}
