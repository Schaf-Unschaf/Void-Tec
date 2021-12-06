package de.schafunschaf.voidtec.scripts.combat.effects.esu.engine;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;

import java.util.Random;

public class Acceleration extends BaseStatMod {
    public Acceleration(float baseValue) {
        super(baseValue);
        this.category = UpgradeCategory.ENGINE;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getAcceleration().modifyPercent(id, 1f + generateModValue(random, quality), quality.getName());
        stats.getDeceleration().modifyPercent(id, 1f + generateModValue(random, quality), quality.getName());
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        MutableStat.StatMod statMod = stats.getAcceleration().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Acceleration %s by %s";
        generateTooltip(tooltip, statMod, description, false);
    }
}
