package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.combat.ShipAPI;

public interface CombatScriptRunner {

    void run(ShipAPI ship, float amount, Object data);
}
