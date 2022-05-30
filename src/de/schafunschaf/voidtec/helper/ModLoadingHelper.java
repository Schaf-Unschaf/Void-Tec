package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.listeners.VT_CampaignListener;
import de.schafunschaf.voidtec.campaign.listeners.VT_LootListener;
import de.schafunschaf.voidtec.combat.scripts.stats.StatScriptProvider;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModProvider;
import de.schafunschaf.voidtec.imported.*;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;

import java.io.IOException;

@Log4j
public class ModLoadingHelper {

    public static void initManagerAndPlugins() {
        initIntel();

        Global.getSector().addTransientListener(new VT_CampaignListener(false));
        Global.getSector().getListenerManager().addListener(new VT_LootListener(), true);
    }

    public static void initIntel() {
        AugmentManagerIntel augmentManagerIntel = AugmentManagerIntel.getInstance();
        augmentManagerIntel.setNew(false);
        Global.getSector().getIntelManager().addIntel(augmentManagerIntel, true);
    }

    public static void initStatMods() {
        StatModProvider.initStatMap();
        StatScriptProvider.initStatScripts();
    }

    public static void loadExternalData() throws JSONException, IOException {
        SettingsLoader.loadSettings();
//        AugmentDataLoader.loadAugmentFiles();
        SpecialShips.loadSpecialShipFiles();
        CustomFactionCategories.loadFactionCategoryFiles();
        WelcomeMessageLoader.loadMessageFiles();
    }

}
