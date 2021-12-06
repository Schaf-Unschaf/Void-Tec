package de.schafunschaf.voidtec.scripts.combat.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.StatManager;

import java.awt.*;
import java.util.Random;

import static de.schafunschaf.bountiesexpanded.util.ComparisonTools.isNull;

public class VoidTecEngineeringSuite extends BaseHullMod {
    public static final String HULL_MOD_ID = "voidTec_engineeringSuite";

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI ship = stats.getFleetMember();
        if (isNull(ship))
            return;

        StatManager statManager = StatManager.getInstance();
        Random random = new Random(ship.getId().hashCode());

        statManager.apply(stats, id, random, null);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        StatManager statManager = StatManager.getInstance();
        statManager.generateTooltipEntry(ship.getMutableStats(), HULL_MOD_ID, tooltip);
    }

    @Override
    public Color getBorderColor() {
        return new Color(255, 150, 0);
    }

    @Override
    public Color getNameColor() {
        return new Color(255, 150, 0);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
    }
}
