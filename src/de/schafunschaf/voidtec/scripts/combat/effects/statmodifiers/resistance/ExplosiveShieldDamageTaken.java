package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.resistance;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;
import java.util.Random;

public class ExplosiveShieldDamageTaken extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue,
                      Random random, AugmentQuality quality) {
        stats.getHighExplosiveShieldDamageTakenMult().modifyPercent(id, -generateModValue(statModValue, random, quality));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getHighExplosiveShieldDamageTakenMult().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getHighExplosiveShieldDamageTakenMult().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod)) {
            return;
        }

        String description = "Explosive damage taken by shields %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, true);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue) {
        boolean isPositive = avgModValue <= 0;
        String incDec = isPositive ? "Reduces" : "Increases";
        String hlString1 = "damage taken";
        String hlString2 = "explosive";
        String hlString3 = "shields";
        String description = String.format("the %s from all %s weapons on %s", hlString1, hlString2, hlString3);

        generateStatDescription(tooltip, description, incDec, bulletColor, isPositive, hlString1, hlString2, hlString3);
    }
}
