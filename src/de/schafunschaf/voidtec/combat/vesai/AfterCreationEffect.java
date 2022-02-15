package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.combat.ShipAPI;

public interface AfterCreationEffect {

    void applyAfterCreation(ShipAPI ship, String id);
}
