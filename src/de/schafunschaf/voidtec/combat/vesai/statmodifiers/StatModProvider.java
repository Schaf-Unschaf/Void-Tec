package de.schafunschaf.voidtec.combat.vesai.statmodifiers;

import de.schafunschaf.voidtec.helper.StatLoader;

import java.util.Map;
import java.util.Set;

public class StatModProvider {

    private static Map<String, BaseStatMod> statApplierMap;

    public static void initStatMap() {
        statApplierMap = StatLoader.initStatMods();
    }

    public static BaseStatMod getStatMod(String statModID) {
        return statApplierMap.get(statModID);
    }

    public static Set<String> getStatModIDs() {
        return statApplierMap.keySet();
    }
}
