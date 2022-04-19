package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModProvider;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import lombok.extern.log4j.Log4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class AugmentDataLoader {

    public static final String AUGMENT_FILE_PATH = "data/config/voidtec/vt_augment_data.csv";

    public static void loadAugmentFiles() {
        try {
            JSONArray spreadsheet = Global.getSettings()
                                          .getMergedSpreadsheetDataForMod("augmentID", AUGMENT_FILE_PATH, VoidTecPlugin.MOD_ID);

            for (int i = 0; i < spreadsheet.length(); i++) {
                JSONObject row = spreadsheet.getJSONObject(i);

                String augmentID;
                if (row.has("augmentID") && !isNull(row.getString("augmentID")) && !row.getString("augmentID").isEmpty() && !row.getString(
                        "augmentID").contains("#")) {
                    augmentID = row.getString("augmentID");
                    log.info(String.format("VoidTec: Loading Augment %s", augmentID));
                } else {
                    log.info("VoidTec: Hit empty line, skipping");
                    continue;
                }

                String primaryStatModString = row.optString("primaryStatMods");
                List<StatApplier> primaryStatMods = new ArrayList<>();
                if (!primaryStatModString.isEmpty()) {
                    primaryStatMods = getStatModsFromString(primaryStatModString);
                }

                String primaryStatModValueString = row.optString("primaryStatValues");
                List<StatModValue<Float, Float, Boolean, Boolean>> primaryStatValues = new ArrayList<>();
                if (!primaryStatModValueString.isEmpty()) {
                    primaryStatValues = getStatValuesFromString(primaryStatModValueString);
                }

                String secondarySlotString = row.optString("secondarySlots");
                List<SlotCategory> secondarySlots = new ArrayList<>();
                if (!secondarySlotString.isEmpty()) {
                    secondarySlots = getSlotsFromString(secondarySlotString);
                }

                String secondaryStatModString = row.optString("secondaryStatMods");
                List<StatApplier> secondaryStatMods = new ArrayList<>();
                if (!secondaryStatModString.isEmpty()) {
                    secondaryStatMods = getStatModsFromString(secondaryStatModString);
                }

                String secondaryStatModValueString = row.optString("secondaryStatValues");
                List<StatModValue<Float, Float, Boolean, Boolean>> secondaryStatValues = new ArrayList<>();
                if (!secondaryStatModValueString.isEmpty()) {
                    secondaryStatValues = getStatValuesFromString(secondaryStatModValueString);
                }

                String[] augmentQualityValueString = getSeparatedStrings(row.optString("allowedAugmentQualities"));
                String[] augmentQuality;
                if (augmentQualityValueString.length == 1 && augmentQualityValueString[0].isEmpty()) {
                    augmentQuality = AugmentQuality.allowedValues;
                } else {
                    augmentQuality = augmentQualityValueString;
                }

                String[] allowedFactions = getSeparatedStrings(row.optString("allowedFactions"));
                String[] forbiddenFactions = getSeparatedStrings(row.optString("forbiddenFactions"));
                String[] tags = getSeparatedStrings(row.optString("tags"));

                try {
                    AugmentData augmentData = new AugmentData(augmentID, row.optString("manufacturer"), row.optString("name"),
                                                              new TextWithHighlights(row.optString("description"), null),
                                                              row.optInt("rarity"), SlotCategory.getEnum(row.getString("primarySlot")),
                                                              primaryStatMods, primaryStatValues, secondarySlots, secondaryStatMods,
                                                              secondaryStatValues, augmentQuality,
                                                              new TextWithHighlights(row.optString("additionalDescription"), null),
                                                              row.optBoolean("equalQualityRoll"), row.optString("beforeCreationScript"),
                                                              row.optString("afterCreationScript"), row.optString("combatScript"),
                                                              row.optString("rightClickScript"), row.optBoolean("uniqueMod"),
                                                              Arrays.asList(allowedFactions), Arrays.asList(forbiddenFactions),
                                                              Arrays.asList(tags));

                    AugmentDataManager.storeAugmentData(augmentID, augmentData);
                } catch (JSONException error) {
                    log.warn(String.format("VT: An error occurred while loading Augment '%s' \n %s", augmentID, error));
                }
            }
        } catch (IOException | JSONException error) {
            log.warn(String.format("VT: An error occurred while loading Augment data:\n %s", error));
        }
    }

    private static String[] getSeparatedStrings(String string) {
        return string.split("\\s*(,\\s*)+");
    }

    public static List<StatApplier> convertStatMods(String... statModKeys) {
        List<StatApplier> statMods = new ArrayList<>();

        for (String statModAsString : statModKeys) {
            StatApplier statMod = StatModProvider.getStatMod(statModAsString.toLowerCase().trim());
            if (isNull(statMod)) {
                log.warn(String.format("VT: Error while parsing '%s' - No matching StatMod found", statModAsString));
            } else {
                statMods.add(statMod);
            }
        }

        return statMods;
    }

    private static List<StatApplier> getStatModsFromString(String statModString) {
        String[] strings = getSeparatedStrings(statModString);

        return convertStatMods(strings);
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

    private static List<StatModValue<Float, Float, Boolean, Boolean>> getStatValuesFromString(String statModValueString) {
        List<StatModValue<Float, Float, Boolean, Boolean>> statModValues = new ArrayList<>();

        for (String statModValue : statModValueString.split("\\s*(\\|\\s*)+")) {
            if (!isNull(statModValue) && !statModValue.isEmpty()) {
                statModValues.add(getStatModValueDataFromString(statModValue.trim()));
            }
        }

        return statModValues;
    }

    private static StatModValue<Float, Float, Boolean, Boolean> getStatModValueDataFromString(String statModValueString) {
        String[] data = getSeparatedStrings(statModValueString);
        return new StatModValue<>(Float.valueOf(data[0]), Float.valueOf(data[1]), Boolean.valueOf(data[2].trim()),
                                  Boolean.valueOf(data[3].trim()));
    }
}

