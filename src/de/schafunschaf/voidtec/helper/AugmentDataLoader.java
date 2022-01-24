package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatProvider;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.util.TextWithHighlights;
import lombok.extern.log4j.Log4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class AugmentDataLoader {
    public static final String AUGMENT_FILE_PATH = "data/config/voidtec/vt_augment_data.csv";

    public static void loadAugmentsFromFiles() {
        try {
            JSONArray spreadsheet = Global.getSettings().getMergedSpreadsheetDataForMod("augmentID", AUGMENT_FILE_PATH, VoidTecPlugin.MOD_ID);

            for (int i = 0; i < spreadsheet.length(); i++) {
                JSONObject row = spreadsheet.getJSONObject(i);

                String augmentID;
                if (row.has("augmentID") && !isNull(row.getString("augmentID")) && !row.getString("augmentID").isEmpty() && !row.getString("augmentID").contains("#")) {
                    augmentID = row.getString("augmentID");
                    log.info(String.format("VoidTec: Loading Augment %s", augmentID));
                } else {
                    log.info("VoidTec: Hit empty line, skipping");
                    continue;
                }

                String primaryStatModString = row.optString("primaryStatMods");
                List<BaseStatMod> primaryStatMods = null;
                if (!primaryStatModString.isEmpty()) {
                    primaryStatMods = getStatModsFromString(primaryStatModString);
                }

                String primaryStatModValueString = row.optString("primaryStatValues");
                List<StatModValue<Float, Float, Boolean>> primaryStatValues = null;
                if (!primaryStatModValueString.isEmpty()) {
                    primaryStatValues = getStatValuesFromString(primaryStatModValueString);
                }

                String secondarySlotString = row.optString("secondarySlots");
                List<SlotCategory> secondarySlots = null;
                if (!secondarySlotString.isEmpty()) {
                    secondarySlots = getSlotsFromString(secondarySlotString);
                }

                String secondaryStatModString = row.optString("secondaryStatMods");
                List<BaseStatMod> secondaryStatMods = null;
                if (!secondaryStatModString.isEmpty()) {
                    secondaryStatMods = getStatModsFromString(secondaryStatModString);
                }

                String secondaryStatModValueString = row.optString("secondaryStatValues");
                List<StatModValue<Float, Float, Boolean>> secondaryStatValues = null;
                if (!secondaryStatModValueString.isEmpty()) {
                    secondaryStatValues = getStatValuesFromString(secondaryStatModValueString);
                }

                String[] augmentQualityValueString = row.optString("allowedAugmentQualities").split("\\s*(,\\s*)+");
                String[] augmentQuality;
                if (augmentQualityValueString.length == 1 && augmentQualityValueString[0].isEmpty()) {
                    augmentQuality = AugmentQuality.allowedValues;
                } else {
                    augmentQuality = augmentQualityValueString;
                }

                try {
                    AugmentData augmentData = new AugmentData(augmentID, row.optString("manufacturer"), row.optString("name"), new TextWithHighlights(row.optString("description")), row.optInt("rarity"), SlotCategory.getEnum(row.getString("primarySlot")), primaryStatMods, primaryStatValues, secondarySlots, secondaryStatMods, secondaryStatValues, augmentQuality, new TextWithHighlights(row.optString("combatScriptDescription")), row.optBoolean("equalQualityRoll"), null);

                    AugmentDataManager.storeAugmentData(augmentID, augmentData);
                } catch (JSONException error) {
                    log.warn(String.format("VT: An error occurred while loading Augment '%s' \n %s", augmentID, error));
                }
            }
        } catch (IOException | JSONException error) {
            log.warn(String.format("VT: An error occurred while loading Augment data:\n %s", error));
        }
    }

    private static List<BaseStatMod> getStatModsFromString(String primaryStatModString) {
        List<BaseStatMod> statMods = new ArrayList<>();

        for (String statModAsString : primaryStatModString.split("\\s*(,\\s*)+")) {
            BaseStatMod statMod = StatProvider.getStatMod(statModAsString.toLowerCase().trim());
            if (!isNull(statMod)) {
                statMods.add(statMod);
            }
        }

        return statMods;
    }

    private static List<SlotCategory> getSlotsFromString(String slotString) {
        List<SlotCategory> slotCategories = new ArrayList<>();

        for (String slotAsString : slotString.split("\\s*(,\\s*)+")) {
            if (!isNull(slotAsString) && !slotAsString.isEmpty()) {
                slotCategories.add(SlotCategory.valueOf(slotAsString.trim()));
            }
        }

        return slotCategories;
    }

    private static List<StatModValue<Float, Float, Boolean>> getStatValuesFromString(String primaryStatModValueString) {
        List<StatModValue<Float, Float, Boolean>> statModValues = new ArrayList<>();

        for (String statModValue : primaryStatModValueString.split("\\s*(_\\s*)+")) {
            if (!isNull(statModValue) && !statModValue.isEmpty()) {
                statModValues.add(getStatModValueDataFromString(statModValue.trim()));
            }
        }

        return statModValues;
    }

    private static StatModValue<Float, Float, Boolean> getStatModValueDataFromString(String statModValueString) {
        String[] data = statModValueString.split("\\s*(,\\s*)+");
        return new StatModValue<>(Float.valueOf(data[0].trim()), Float.valueOf(data[1].trim()), Boolean.valueOf(data[2].trim()));
    }
}

