package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.fleet.FleetMemberAPI;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ShipUtils {
    public static String generateShipNameWithClass(FleetMemberAPI ship) {
        if (isNull(ship)) {
            return "NO SHIP FOR NAME AND CLASS";
        }

        String shipName = ship.getShipName();
        String shipClass = ship.getHullSpec().getHullNameWithDashClass();
        String shipDesignation = ship.getHullSpec().getDesignation().toLowerCase();
        String shipType = String.format("%s %s", shipClass, shipDesignation);

        return String.format("%s, %s", shipName, shipType);
    }
}
