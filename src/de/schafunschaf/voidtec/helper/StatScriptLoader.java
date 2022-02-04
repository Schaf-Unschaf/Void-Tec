package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.combat.scripts.stats.TimeAccelerationEffect;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.ids.VT_StatModScripts;

import java.util.HashMap;
import java.util.Map;

public class StatScriptLoader {

    public static Map<String, CombatScriptRunner> initStatScripts() {
        Map<String, CombatScriptRunner> map = new HashMap<>();

        map.put(VT_StatModScripts.TIME_ACCELERATION, new TimeAccelerationEffect(VT_StatModScripts.TIME_ACCELERATION));

        return map;
    }
}
