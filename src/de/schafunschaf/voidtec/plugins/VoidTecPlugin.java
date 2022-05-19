package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.chests.AugmentChestData;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ModLoadingHelper;
import de.schafunschaf.voidtec.ids.VT_Items;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.CargoUtils;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;

import java.io.IOException;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class VoidTecPlugin extends BaseModPlugin {

    public static final String MOD_ID = "voidtec";
    public static final String DEV_CHEST = "$_vt_dev_chest_spawned";

    @Override
    public void afterGameSave() {
        ModLoadingHelper.initIntel();
    }

    @Override
    public void beforeGameSave() {
        HullModDataStorage.getInstance().saveToMemory();
        AugmentManagerIntel.reset();
        Global.getSector().getIntelManager().removeIntel(AugmentManagerIntel.getInstance());
    }

    @Override
    public void onApplicationLoad() throws JSONException, IOException {
        ModLoadingHelper.initStatMods();
        ModLoadingHelper.loadExternalData();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        ModLoadingHelper.initManagerAndPlugins();

        VT_Settings.isIndEvoActive = Global.getSettings().getModManager().isModEnabled("IndEvo");

        // TODO remove before final release
        MemoryAPI memoryAPI = Global.getSector().getMemoryWithoutUpdate();
        if (!memoryAPI.contains(DEV_CHEST)) {
            for (MarketAPI marketAPI : Global.getSector().getEconomy().getMarketsCopy()) {
                SubmarketAPI submarket = marketAPI.getSubmarket(Submarkets.SUBMARKET_STORAGE);
                if (isNull(submarket)) {
                    continue;
                }
                CargoAPI submarketCargo = submarket.getCargo();
                if (isNull(submarketCargo)) {
                    continue;
                }
                removeFaultyChests(submarketCargo);
            }
            removeFaultyChests(Global.getSector().getPlayerFleet().getCargo());

            CargoUtils.addChestToFleetCargo(new AugmentChestData(VT_Items.STORAGE_CHEST_XS, null, "Augment Storage Chest", 200));
            memoryAPI.set(DEV_CHEST, 1);
        }

        if (VT_Settings.sheepDebug) {
            CargoUtils.addRandomAugmentsToFleetCargo(null, 10, 50);
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_rainbowEngines", AugmentQuality.CUSTOMISED));
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_rainbowShields", AugmentQuality.CUSTOMISED));
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_engineRecolor", AugmentQuality.CUSTOMISED));
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_engineRecolor", AugmentQuality.CUSTOMISED));
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_engineRecolor", AugmentQuality.CUSTOMISED));
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_pursuitEngines", AugmentQuality.getRandomQuality(null,
                                                                                                                                 true)));
            CargoUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_travelDrives", AugmentQuality.getRandomQuality(null,
                                                                                                                               true)));

            for (int i = 0; i < 80; i++) {
                AugmentApplier augment = AugmentDataManager.getRandomAugment(SlotCategory.getRandomCategory(null, true),
                                                                             AugmentQuality.DOMAIN, null, null, null, true);
                augment.damageAugment(new Random().nextInt(6), false);
                CargoUtils.addAugmentToFleetCargo(augment);
            }
        }
    }

    private void removeFaultyChests(CargoAPI cargoAPI) {
        for (CargoStackAPI cargoStackAPI : cargoAPI.getStacksCopy()) {
            SpecialItemData specialItemData = cargoStackAPI.getSpecialDataIfSpecial();
            if (isNull(specialItemData)) {
                continue;
            }

            if (specialItemData.getId().contains(VT_Items.STORAGE_CHEST_ID) && specialItemData.getData() == null) {
                cargoAPI.removeStack(cargoStackAPI);
            }
        }
    }
}
