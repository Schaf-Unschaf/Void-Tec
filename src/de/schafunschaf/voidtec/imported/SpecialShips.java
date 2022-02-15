package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import de.schafunschaf.voidtec.util.JSONParser;
import lombok.extern.log4j.Log4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class SpecialShips {

    private static final String FILE_PATH = "data/config/voidtec/vt_special_ships.json";

    private static final Map<String, String[]> specialHulls = new HashMap<>();
    private static final Map<String, String[]> specialVariants = new HashMap<>();


    public static void loadSpecialShipFiles() {
        try {
            JSONObject mergedSpecialShips = Global.getSettings().getMergedJSONForMod(FILE_PATH, VoidTecPlugin.MOD_ID);
            JSONObject specialHulls = mergedSpecialShips.getJSONObject("specialHulls");
            JSONObject specialVariants = mergedSpecialShips.getJSONObject("specialVariants");

            Map<String, Object[]> specialHullMap = JSONParser.parseJSONObject(specialHulls);
            for (Map.Entry<String, Object[]> entry : specialHullMap.entrySet()) {
                specialHulls.put(entry.getKey(), entry.getValue());
            }

            Map<String, Object[]> specialVariantMap = JSONParser.parseJSONObject(specialVariants);
            for (Map.Entry<String, Object[]> entry : specialVariantMap.entrySet()) {
                specialVariants.put(entry.getKey(), entry.getValue());
            }
        } catch (IOException | JSONException e) {
            log.error(String.format("Failed to load SpecialShips: %s", e));
        }
    }

    public static boolean isSpecialShip(FleetMemberAPI ship) {
        return specialHulls.containsKey(ship.getHullId())
                || specialVariants.containsKey(ship.getVariant().getHullVariantId());
    }

    public static String[] getQualityRange(FleetMemberAPI ship) {
        String[] qualityRange = specialHulls.get(ship.getHullId());
        return isNull(qualityRange) ? specialVariants.get(ship.getVariant().getHullVariantId()) : qualityRange;
    }
}
