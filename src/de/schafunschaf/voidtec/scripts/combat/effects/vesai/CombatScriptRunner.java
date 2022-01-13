package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.combat.ShipAPI;

public interface CombatScriptRunner {
    void run(ShipAPI ship, float amount);
}
