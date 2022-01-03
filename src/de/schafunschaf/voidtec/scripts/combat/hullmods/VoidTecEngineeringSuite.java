package de.schafunschaf.voidtec.scripts.combat.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.VT_Colors;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.HullModDataStorage;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.SlotManager;

import java.awt.*;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;


public class VoidTecEngineeringSuite extends BaseHullMod {
    public static final String HULL_MOD_ID = "voidTec_engineeringSuite";

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fleetMember = stats.getFleetMember();
        if (isNull(fleetMember))
            return;

        Random random = new Random(fleetMember.getId().hashCode());
        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        SlotManager slotManager = hullModDataStorage.getSlotManager(fleetMember);

        if (isNull(slotManager)) {
            slotManager = new SlotManager(fleetMember);
            hullModDataStorage.storeShipData(fleetMember, slotManager);
        }

        slotManager.applySlotEffects(stats, id, random);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        FleetMemberAPI fleetMember = ship.getFleetMember();
        if (isNull(fleetMember))
            return;

        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        SlotManager slotManager = hullModDataStorage.getSlotManager(fleetMember);
        if (isNull(slotManager))
            return;

        slotManager.generateTooltip(fleetMember.getStats(), HULL_MOD_ID, tooltip, width);
    }

    @Override
    public Color getBorderColor() {
        return VT_Colors.VT_COLOR_MAIN;
    }

    @Override
    public Color getNameColor() {
        return VT_Colors.VT_COLOR_MAIN;
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
    }
}
