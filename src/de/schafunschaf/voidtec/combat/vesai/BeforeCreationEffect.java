package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;

public interface BeforeCreationEffect {

    void applyBeforeCreation(MutableShipStatsAPI stats, String id);
}
