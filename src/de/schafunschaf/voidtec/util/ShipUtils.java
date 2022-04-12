package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.fleet.FleetMemberAPI;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ShipUtils {

    public static String generateShipNameWithClass(FleetMemberAPI fleetMember) {
        if (isNull(fleetMember)) {
            return "NO SHIP FOR NAME AND CLASS";
        }

        String shipName = fleetMember.getShipName();
        String shipClass = fleetMember.getHullSpec().getHullNameWithDashClass();
        String shipDesignation = fleetMember.getHullSpec().getDesignation().toLowerCase();
        String shipType = String.format("%s %s", shipClass, shipDesignation);

        return String.format("%s, %s", shipName, shipType);
    }
}
