package de.schafunschaf.voidtec.combat.scripts.stats;

import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.helper.StatScriptLoader;

import java.util.Map;

public class StatScriptProvider {

    private static Map<String, CombatScriptRunner> statScripts;

    public static void initStatScripts() {
        statScripts = StatScriptLoader.initStatScripts();
    }

    public static CombatScriptRunner getScript(String scriptID) {
        return statScripts.get(scriptID);
    }
}
