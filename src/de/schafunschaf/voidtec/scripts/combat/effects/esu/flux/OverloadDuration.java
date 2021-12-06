package de.schafunschaf.voidtec.scripts.combat.effects.esu.flux;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;

import java.util.Random;

public class OverloadDuration extends BaseStatMod {
    public OverloadDuration(float baseValue) {
        super(baseValue);
        this.category = UpgradeCategory.FLUX;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getOverloadTimeMod().modifyPercent(id, 1f - generateModValue(random, quality), quality.getName());
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getOverloadTimeMod().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        MutableStat.StatMod statMod = stats.getOverloadTimeMod().getPercentBonus(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Overload time %s by %s";
        generateTooltip(tooltip, statMod, description, true);
    }
}
