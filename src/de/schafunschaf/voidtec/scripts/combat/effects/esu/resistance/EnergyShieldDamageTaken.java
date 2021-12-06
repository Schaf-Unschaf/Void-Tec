package de.schafunschaf.voidtec.scripts.combat.effects.esu.resistance;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;

import java.util.Random;

public class EnergyShieldDamageTaken extends BaseStatMod {
    public EnergyShieldDamageTaken(float baseValue) {
        super(baseValue);
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getEnergyShieldDamageTakenMult().modifyPercent(id, 1f - generateModValue(random, ));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {

    }

    @Override
    public boolean canApply(MutableShipStatsAPI stats) {
        return false;
    }
}
