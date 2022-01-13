package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers;

import de.schafunschaf.voidtec.helper.StatLoader;

import java.util.Map;

public class StatProvider {
    private static Map<String, BaseStatMod> statApplierMap;

    public static void initStatMap() {
        statApplierMap = StatLoader.getAllStatMods();
    }

    public static BaseStatMod getStatMod(String statModID) {
        return statApplierMap.get(statModID);
    }
}
