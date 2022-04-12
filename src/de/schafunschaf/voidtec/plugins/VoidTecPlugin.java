package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
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
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.extern.log4j.Log4j;

import java.util.Random;

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
    public void onApplicationLoad() {
        ModLoadingHelper.initStatMods();
        ModLoadingHelper.loadExternalData();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        ModLoadingHelper.initManagerAndPlugins();

        VT_Settings.isIndEvoActive = Global.getSettings().getModManager().isModEnabled("IndEvo");

        // TODO remove before final release
        if (!Global.getSector().getMemoryWithoutUpdate().contains(DEV_CHEST)) {
            VoidTecUtils.addChestToFleetCargo(new AugmentChestData(VT_Items.STORAGE_CHEST_XS, null, "Augment Storage Chest", 200));
        }

        if (VT_Settings.sheepDebug) {
            VoidTecUtils.addRandomAugmentsToFleetCargo(null, 10, 50);
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_rainbowEngines", AugmentQuality.CUSTOMISED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_rainbowShields", AugmentQuality.CUSTOMISED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_engineRecolor", AugmentQuality.CUSTOMISED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_engineRecolor", AugmentQuality.CUSTOMISED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_engineRecolor", AugmentQuality.CUSTOMISED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_pursuitEngines", AugmentQuality.getRandomQuality(null,
                                                                                                                                   true)));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment("vt_travelDrives", AugmentQuality.getRandomQuality(null,
                                                                                                                                 true)));

            for (int i = 0; i < 80; i++) {
                AugmentApplier augment = AugmentDataManager.getRandomAugment(SlotCategory.getRandomCategory(null, true),
                                                                             AugmentQuality.DOMAIN, null, null);
                augment.damageAugment(new Random().nextInt(6));
                VoidTecUtils.addAugmentToFleetCargo(augment);
            }
        }
    }
}
