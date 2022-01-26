package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import de.schafunschaf.voidtec.VT_Settings;
import de.schafunschaf.voidtec.campaign.ids.VT_Augments;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestData;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.listeners.VT_CampaignListener;
import de.schafunschaf.voidtec.campaign.listeners.VT_LootListener;
import de.schafunschaf.voidtec.helper.ModLoadingHelper;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.VT_RainbowEngines;
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
        ModLoadingHelper.loadAugmentData();
        AugmentDataManager.storeAugmentData(VT_Augments.VT_RAINBOW_ENGINES, new VT_RainbowEngines());
    }

    @Override
    public void onGameLoad(boolean newGame) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        if (!intelManager.hasIntelOfClass(AugmentManagerIntel.class)) {
            intelManager.addIntel(new AugmentManagerIntel(), true);
        }

        Global.getSector().addTransientListener(new VT_CampaignListener(false));
        Global.getSector().getListenerManager().addListener(new VT_LootListener(), true);

        VT_Settings.isIndEvoActive = Global.getSettings().getModManager().isModEnabled("IndEvo");

        if (!newGame) {
            CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
            for (int i = 0; i < 20; i++) {
                cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, AugmentDataManager.getRandomAugment(null)), 1);
            }
            cargo.addSpecial(new AugmentChestData(VT_Items.STORAGE_CHEST, null, 100), 1f);
            cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null,
                                                 AugmentDataManager.getAugment(VT_Augments.VT_RAINBOW_ENGINES, AugmentQuality.UNIQUE)), 1);
        }

    }
}
