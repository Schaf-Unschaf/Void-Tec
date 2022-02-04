package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.combat.scripts.stats.StatScriptProvider;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatProvider;
import lombok.extern.log4j.Log4j;

@Log4j
public class ModLoadingHelper {

    public static void initManagerAndPlugins() {

    }

    public static void loadAugmentData() {
        StatProvider.initStatMap();
        StatScriptProvider.initStatScripts();
        AugmentDataLoader.loadAugmentsFromFiles();
    }

}
