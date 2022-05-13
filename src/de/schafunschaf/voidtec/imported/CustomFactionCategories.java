package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import de.schafunschaf.voidtec.util.JSONParser;
import lombok.extern.log4j.Log4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Log4j
public class CustomFactionCategories {

    private static final String FILE_PATH = "data/config/voidtec/vt_faction_category_override.json";

    private static final Set<String> IGNORED_FACTIONS = new HashSet<>();
    private static final Set<String> OUTLAW_FACTIONS = new HashSet<>();
    private static final Set<String> CIVILIAN_FACTIONS = new HashSet<>();
    private static final Set<String> SPECIAL_FACTIONS = new HashSet<>();
    private static final Set<String> EXOTIC_FACTIONS = new HashSet<>();
    private static final Set<String> DOMAIN_FACTIONS = new HashSet<>();

    public static void loadFactionCategoryFiles() {
        try {
            JSONObject mergedFactionObject = Global.getSettings().getMergedJSONForMod(FILE_PATH, VoidTecPlugin.MOD_ID);

            JSONArray ignoredFactions = mergedFactionObject.getJSONArray("ignored");
            JSONArray outlawFactions = mergedFactionObject.getJSONArray("outlaw");
            JSONArray civilianFactions = mergedFactionObject.getJSONArray("civilian");
            JSONArray experimentalFactions = mergedFactionObject.getJSONArray("experimental");
            JSONArray exoticFactions = mergedFactionObject.getJSONArray("exotic");
            JSONArray domainFactions = mergedFactionObject.getJSONArray("domain");

            IGNORED_FACTIONS.addAll(Arrays.asList((String[]) JSONParser.parseJSONArray(ignoredFactions)));
            OUTLAW_FACTIONS.addAll(Arrays.asList((String[]) JSONParser.parseJSONArray(outlawFactions)));
            CIVILIAN_FACTIONS.addAll(Arrays.asList((String[]) JSONParser.parseJSONArray(civilianFactions)));
            SPECIAL_FACTIONS.addAll(Arrays.asList((String[]) JSONParser.parseJSONArray(experimentalFactions)));
            EXOTIC_FACTIONS.addAll(Arrays.asList((String[]) JSONParser.parseJSONArray(exoticFactions)));
            DOMAIN_FACTIONS.addAll(Arrays.asList((String[]) JSONParser.parseJSONArray(domainFactions)));

        } catch (IOException | JSONException e) {
            log.error(String.format("Failed to load CustomFactionCategories: %s", e));
        }
    }

    public static boolean isIgnoredFaction(String factionID) {
        return IGNORED_FACTIONS.contains(factionID);
    }

    public static boolean isOutlawFaction(String factionID) {
        return OUTLAW_FACTIONS.contains(factionID);
    }

    public static boolean isCivilianFaction(String factionID) {
        return CIVILIAN_FACTIONS.contains(factionID);
    }

    public static boolean isSpecialFaction(String factionID) {
        return SPECIAL_FACTIONS.contains(factionID);
    }

    public static boolean isRemnantFaction(String factionID) {
        return EXOTIC_FACTIONS.contains(factionID);
    }

    public static boolean isDomainFaction(String factionID) {
        return DOMAIN_FACTIONS.contains(factionID);
    }
}