package de.schafunschaf.voidtec.scripts.combat.effects.esu.durability;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;

import java.util.Random;

public class WeaponRepair extends BaseStatMod {
    public WeaponRepair(float baseValue) {
        super(baseValue);
        this.category = UpgradeCategory.DURABILITY;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        super.apply(stats, id, random, quality);
        stats.getCombatWeaponRepairTimeMult().modifyPercent(id, 1f - generateModValue(random, quality), quality.getName());
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getCombatEngineRepairTimeMult().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        MutableStat.StatMod statMod = stats.getCombatWeaponRepairTimeMult().getMultStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Engine repair time %s by %s";
        generateTooltip(tooltip, statMod, description, true);
    }
}
