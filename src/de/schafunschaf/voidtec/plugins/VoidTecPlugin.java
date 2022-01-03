package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.loading.VariantSource;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.helper.ModLoadingUtils;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.HullModDataStorage;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.ReactorOverdrive;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.ReinforcedPlating;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;
import lombok.extern.log4j.Log4j;

@Log4j
public class VoidTecPlugin extends BaseModPlugin {
    @Override
    public void onApplicationLoad() {
        ModLoadingUtils.loadAugmentData();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        generateShipWithVTES();
        generateShipWithVTES();
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        if (!intelManager.hasIntelOfClass(AugmentManagerIntel.class))
            intelManager.addIntel(new AugmentManagerIntel(), true);
        Global.getSector().getPlayerFleet().getCargo().addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, new ReactorOverdrive()), 1f);
        Global.getSector().getPlayerFleet().getCargo().addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, new ReinforcedPlating()), 1f);
    }

    @Override
    public void beforeGameSave() {
        HullModDataStorage.getInstance().saveToMemory();
    }

    public static void generateShipWithVTES() {
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
