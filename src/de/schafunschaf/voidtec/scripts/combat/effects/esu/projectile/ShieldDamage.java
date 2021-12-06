package de.schafunschaf.voidtec.scripts.combat.effects.esu.projectile;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;

import java.util.Random;

public class ShieldDamage extends BaseStatMod {
    public ShieldDamage(float baseValue) {
        super(baseValue);
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getShieldDamageTakenMult().modifyPercent(id, 1f + generateModValue(random, ));
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {

    }

    @Override
    public boolean canApply(MutableShipStatsAPI stats) {
        return false;
    }
}
