package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.resistance;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.*;
import java.util.Random;

public class ExplosiveArmorDamageTaken extends BaseStatMod {
    @Override
    public void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random, UpgradeQuality quality) {
        stats.getHighExplosiveDamageTakenMult().modifyPercent(id, 1f - generateModValue(statModValue, random, quality));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getHighExplosiveDamageTakenMult().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor) {
        MutableStat.StatMod statMod = stats.getHighExplosiveDamageTakenMult().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Explosive damage taken by armor and hull %s by %s";
        generateTooltip(tooltip, statMod, description, bulletColor, true);
    }
}