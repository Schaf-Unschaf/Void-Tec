package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.combat.ShipAPI;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;

public interface CombatScriptRunner {

    void run(ShipAPI ship, float amount, AugmentApplier augment);
}
