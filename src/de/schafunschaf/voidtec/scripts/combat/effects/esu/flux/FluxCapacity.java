package de.schafunschaf.voidtec.scripts.combat.effects.esu.flux;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;

import java.util.Random;

public class FluxCapacity extends BaseStatMod {
    public FluxCapacity(float baseValue) {
        super(baseValue);
        this.category = UpgradeCategory.FLUX;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getFluxCapacity().modifyPercent(id, 1f + generateModValue(random, quality), quality.getName());
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getFluxCapacity().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        MutableStat.StatMod statMod = stats.getFluxCapacity().getPercentStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Flux capacity %s by %s";
        generateTooltip(tooltip, statMod, description, false);
    }
}
