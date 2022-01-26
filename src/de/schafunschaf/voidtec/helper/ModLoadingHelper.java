package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatProvider;
import lombok.extern.log4j.Log4j;

@Log4j
public class ModLoadingHelper {

    public static void initManagerAndPlugins() {

    }

    public static void loadAugmentData() {
        StatProvider.initStatMap();
        AugmentDataLoader.loadAugmentsFromFiles();
    }

}
