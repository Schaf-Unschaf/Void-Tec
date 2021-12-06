package de.schafunschaf.voidtec.scripts.combat.effects.esu.logistic;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import de.schafunschaf.bountiesexpanded.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.bountiesexpanded.scripts.combat.effects.esu.UpgradeQuality;

import java.util.Random;

public class ShieldMalfunction extends BaseStatMod {
    public ShieldMalfunction(float baseValue) {
        super(baseValue);
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getShieldMalfunctionChance().modifyPercent(id, 1f - generateModValue(random, ));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {

    }

    @Override
    public boolean canApply(MutableShipStatsAPI stats) {
        return false;
    }
}
