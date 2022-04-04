package de.schafunschaf.voidtec.combat.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.scripts.fx.AugmentationEffect;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.ids.VT_Colors;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;


public class VoidTecEngineeringSuite extends BaseHullMod {

    public static final String HULL_MOD_ID = "voidTec_engineeringSuite";

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        HullModManager hullModManager = HullModDataStorage.getInstance().getHullModManager(ship.getFleetMemberId());
        if (isNull(hullModManager)) {
            return;
        }

        hullModManager.applyAfterCreation(ship, id);
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        FleetMemberAPI fleetMember = stats.getFleetMember();
        if (isNull(fleetMember)) {
            return;
        }

        disableVanillaSModInstallation(stats, id);

        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        HullModManager hullmodManager = hullModDataStorage.getHullModManager(fleetMember.getId());

        if (isNull(hullmodManager)) {
            hullmodManager = new HullModManager(fleetMember);
        }

        hullmodManager.applySlotEffects(stats, id);
    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        FleetMemberAPI fleetMember = ship.getFleetMember();
        if (isNull(fleetMember) || !ship.isAlive() || ship.isPiece()) {
            return;
        }

        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        HullModManager hullmodManager = hullModDataStorage.getHullModManager(fleetMember.getId());
        if (isNull(hullmodManager)) {
            return;
        }

        for (AugmentSlot augmentSlot : hullmodManager.getFilledSlots()) {
            augmentSlot.getSlottedAugment().runCustomScript(ship, amount);
        }

        hullmodManager.getShipStatEffectManager().runScripts(ship, amount);
        AugmentationEffect.run(ship, amount, this);
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width,
                                          boolean isForModSpec) {
        FleetMemberAPI fleetMember = ship.getFleetMember();
        if (isNull(fleetMember)) {
            return;
        }

        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        HullModManager hullmodManager = hullModDataStorage.getHullModManager(fleetMember.getId());
        if (isNull(hullmodManager)) {
            return;
        }

        hullmodManager.generateTooltip(fleetMember.getStats(), HULL_MOD_ID, tooltip, width);
    }

    @Override
    public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
        FleetMemberAPI fleetMember = ship.getFleetMember();
        if (isNull(fleetMember)) {
            return;
        }

        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        HullModManager hullmodManager = hullModDataStorage.getHullModManager(fleetMember.getId());
        if (isNull(hullmodManager)) {
            return;
        }

        hullmodManager.applyFighterEffects(fighter, id);
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
    public int getDisplaySortOrder() {
        return 1;
    }

    @Override
    public int getDisplayCategoryIndex() {
        return 1;
    }

    private void disableVanillaSModInstallation(MutableShipStatsAPI stats, String id) {
        float maxPermanentHullmods = Global.getSettings().getFloat("maxPermanentHullmods");
        stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat(id, -maxPermanentHullmods);
    }
}
