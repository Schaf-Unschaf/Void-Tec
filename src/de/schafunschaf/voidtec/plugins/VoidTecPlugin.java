package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ModLoadingHelper;
import de.schafunschaf.voidtec.ids.VT_Augments;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.extern.log4j.Log4j;

@Log4j
public class VoidTecPlugin extends BaseModPlugin {

    public static final String MOD_ID = "voidtec";

    @Override
    public void afterGameSave() {
        ModLoadingHelper.initIntel();
    }

    @Override
    public void beforeGameSave() {
        HullModDataStorage.getInstance().saveToMemory();
        Global.getSector().getIntelManager().removeIntel(AugmentManagerIntel.getInstance());
    }

    @Override
    public void onApplicationLoad() {
        ModLoadingHelper.initStatMods();
        ModLoadingHelper.loadExternalData();

        AugmentDataManager.initCustomAugments();
    }

    @Override
    public void onGameLoad(boolean newGame) {
        ModLoadingHelper.initManagerAndPlugins();

        VT_Settings.isIndEvoActive = Global.getSettings().getModManager().isModEnabled("IndEvo");

        // TODO remove before final release
        if (!newGame) {
            CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
            VoidTecUtils.addChestToFleetCargo(100);
            VoidTecUtils.addRandomAugmentsToFleetCargo(null, 10, 20);
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_ENGINES, AugmentQuality.CUSTOMISED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_ENGINES, AugmentQuality.DESTROYED));
            VoidTecUtils.addAugmentToFleetCargo(AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_SHIELDS, AugmentQuality.CUSTOMISED));

            AugmentApplier augment1 = AugmentDataManager.getAugment(VT_Augments.VT_PURSUIT_ENGINES, AugmentQuality.DOMAIN);
            augment1.damageAugment(5);
            VoidTecUtils.addAugmentToFleetCargo(augment1);

            for (int i = 0; i < 5; i++) {
                AugmentApplier augment = AugmentDataManager.getRandomAugment(SlotCategory.getRandomCategory(null),
                                                                             AugmentQuality.getRandomQualityInRange(
                                                                                     new String[]{AugmentQuality.EXPERIMENTAL.getName(),
                                                                                                  AugmentQuality.DOMAIN.getName()}, null,
                                                                                     true), null, null);
                augment.damageAugment(3);
                VoidTecUtils.addAugmentToFleetCargo(augment);
            }

            cargo.getCredits().add(90_000_000f);
        }
    }
}
