package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.listeners.VT_CampaignListener;
import de.schafunschaf.voidtec.campaign.listeners.VT_LootListener;
import de.schafunschaf.voidtec.combat.scripts.stats.StatScriptProvider;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatProvider;
import de.schafunschaf.voidtec.imported.CustomFleetCategories;
import lombok.extern.log4j.Log4j;

@Log4j
public class ModLoadingHelper {

    public static void initManagerAndPlugins() {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        if (!intelManager.hasIntelOfClass(AugmentManagerIntel.class)) {
            intelManager.addIntel(new AugmentManagerIntel(), true);
        }

        Global.getSector().addTransientListener(new VT_CampaignListener(false));
        Global.getSector().getListenerManager().addListener(new VT_LootListener(), true);
    }

    public static void loadAugmentData() {
        StatProvider.initStatMap();
        StatScriptProvider.initStatScripts();
        AugmentDataLoader.loadAugmentsFromFiles();
    }

    public static void loadFactionData() {
        CustomFleetCategories.initVanillaFactions();
    }

}
