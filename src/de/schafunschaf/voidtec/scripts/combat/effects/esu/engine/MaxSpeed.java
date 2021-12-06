package de.schafunschaf.voidtec.scripts.combat.effects.esu.engine;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;

import java.util.Random;

public class MaxSpeed extends BaseStatMod {
    public MaxSpeed(float baseValue) {
        super(baseValue);
        this.category = UpgradeCategory.ENGINE;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getMaxSpeed().modifyPercent(id, 1f + generateModValue(random, quality), quality.getName());
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getMaxSpeed().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        MutableStat.StatMod statMod = stats.getMaxSpeed().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Top speed %s by %s";
        generateTooltip(tooltip, statMod, description, false);
    }
}
