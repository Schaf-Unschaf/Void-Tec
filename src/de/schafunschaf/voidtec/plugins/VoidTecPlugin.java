package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.VariantSource;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;
import lombok.extern.log4j.Log4j;

@Log4j
public class VoidTecPlugin extends BaseModPlugin {
    @Override
    public void onGameLoad(boolean newGame) {
        generateShipWithESU();
        generateShipWithESU();
    }

    public static void generateShipWithESU() {
        CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        ShipVariantAPI variant = playerFleet.getFlagship().getVariant();
        FleetMemberAPI fleetMember = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variant);
        if (variant.isStockVariant() || variant.getSource() != VariantSource.REFIT) {
            variant = variant.clone();
            variant.setOriginalVariant(null);
            variant.setSource(VariantSource.REFIT);
            fleetMember.setVariant(variant, false, false);
        }

        variant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
        playerFleet.getFleetData().addFleetMember(fleetMember);
    }
}
