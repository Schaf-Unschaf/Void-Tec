package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestData;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ModLoadingHelper;
import de.schafunschaf.voidtec.ids.VT_Augments;
import de.schafunschaf.voidtec.ids.VT_Items;
import de.schafunschaf.voidtec.ids.VT_Settings;
import lombok.extern.log4j.Log4j;

@Log4j
public class VoidTecPlugin extends BaseModPlugin {

    public static final String MOD_ID = "voidtec";

    @Override
    public void beforeGameSave() {
        HullModDataStorage.getInstance().saveToMemory();
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
            for (int i = 0; i < 20; i++) {
                cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, AugmentDataManager.getRandomAugment(null)), 1);
            }
            cargo.addSpecial(new AugmentChestData(VT_Items.STORAGE_CHEST, null, 100), 1f);
            cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null,
                                                 AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_ENGINES, AugmentQuality.UNIQUE)), 1);
            cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null,
                                                 AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_ENGINES, AugmentQuality.DESTROYED)),
                             1);
            cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null,
                                                 AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_SHIELDS, AugmentQuality.UNIQUE)), 1);
            AugmentApplier augment = AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_ENGINES, AugmentQuality.COMMON);
            augment.damageAugment(1);
            cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null,
                                                 augment), 1);
            cargo.getCredits().add(100_000_000f);
        }
    }
}
