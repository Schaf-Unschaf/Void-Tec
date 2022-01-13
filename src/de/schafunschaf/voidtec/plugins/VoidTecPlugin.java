package de.schafunschaf.voidtec.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import de.schafunschaf.voidtec.Settings;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.listeners.VT_CampaignListener;
import de.schafunschaf.voidtec.campaign.listeners.VT_LootListener;
import de.schafunschaf.voidtec.helper.ModLoadingUtils;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.BaseAugment;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.VT_RainbowEngines;
import lombok.extern.log4j.Log4j;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class VoidTecPlugin extends BaseModPlugin {
    public static final String MOD_ID = "voidtec";

    @Override
    public void onApplicationLoad() {
        ModLoadingUtils.loadAugmentData();
        AugmentDataManager.storeAugmentData(VT_RainbowEngines.AUGMENT_ID, new VT_RainbowEngines());
    }

    @Override
    public void onGameLoad(boolean newGame) {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        if (!intelManager.hasIntelOfClass(AugmentManagerIntel.class))
            intelManager.addIntel(new AugmentManagerIntel(), true);

        Global.getSector().addTransientListener(new VT_CampaignListener(false));
        Global.getSector().getListenerManager().addListener(new VT_LootListener(), true);

        Settings.isIndEvoActive = Global.getSettings().getModManager().isModEnabled("IndEvo");

        for (AugmentQuality augmentQuality : AugmentQuality.getAllowedQualities()) {
            BaseAugment randomAugment = AugmentDataManager.getRandomAugment(augmentQuality, null);
            if (!isNull(randomAugment))
                Global.getSector().getPlayerFleet().getCargo().addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, randomAugment), 1f);
        }

        Global.getSector().getPlayerFleet().getCargo().addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, AugmentDataManager.getAugment(VT_RainbowEngines.AUGMENT_ID)), 1f);
    }

    @Override
    public void beforeGameSave() {
        HullModDataStorage.getInstance().saveToMemory();
    }
}
