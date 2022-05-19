package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentData;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModProvider;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import de.schafunschaf.voidtec.util.ui.StringAutocorrect;
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
                if (row.has("augmentID") && !isNull(row.getString("augmentID")) && !row.getString("augmentID")
                                                                                       .isEmpty() && !row.getString(
                        "augmentID").contains("#")) {
                    augmentID = row.getString("augmentID");
                    log.info(String.format("VoidTec: Loading Augment %s", augmentID));
                } else {
                    log.info("VoidTec: Hit empty line, skipping");
                    continue;
                }

                try {
                    String manufacturer = row.optString("manufacturer");
                    List<String> manufacturerList = manufacturer.isEmpty()
                                                    ? new ArrayList<String>()
                                                    : fixManufacturerData(Arrays.asList(getSeparatedStrings(manufacturer)));

                    String name = row.optString("name");
                    TextWithHighlights description = new TextWithHighlights(row.optString("description"), null);
                    int rarity = row.optInt("rarity");
                    SlotCategory primarySlot = SlotCategory.getEnum(row.getString("primarySlot"));

                    String primaryStatModString = row.optString("primaryStatMods");
                    List<StatApplier> primaryStatMods = new ArrayList<>();
                    if (!primaryStatModString.isEmpty()) {
                        primaryStatMods = getStatModsFromString(primaryStatModString);
                    }

                    String primaryStatModValueString = row.optString("primaryStatValues");
                    List<StatModValue<Float, Float, Boolean, Boolean>> primaryStatValues = new ArrayList<>();
                    if (!primaryStatModValueString.isEmpty()) {
                        List<StatModValue<Float, Float, Boolean, Boolean>> statValuesFromString = getStatValuesFromString(
                                primaryStatModValueString);
                        for (int j = 0; j < statValuesFromString.size(); j++) {
                            statValuesFromString.set(j, applyStatRangeModifier(statValuesFromString.get(j),
                                                                               VT_Settings.statRollRangeModifier));
                        }

                        primaryStatValues = statValuesFromString;
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
                        List<StatModValue<Float, Float, Boolean, Boolean>> statValuesFromString = getStatValuesFromString(
                                secondaryStatModValueString);
                        for (int j = 0; j < statValuesFromString.size(); j++) {
                            statValuesFromString.set(j, applyStatRangeModifier(statValuesFromString.get(j),
                                                                               VT_Settings.statRollRangeModifier));
                        }

                        secondaryStatValues = statValuesFromString;
                    }

                    String[] augmentQualityValueString = getSeparatedStrings(row.optString("allowedAugmentQualities"));
                    String[] augmentQuality = getAugmentQualityRange(augmentQualityValueString);

                    TextWithHighlights additionalDescription = new TextWithHighlights(row.optString("additionalDescription"), null);
                    boolean equalQualityRoll = row.optBoolean("equalQualityRoll");
                    String beforeCreationScript = row.optString("beforeCreationScript");
                    String afterCreationScript = row.optString("afterCreationScript");
                    String combatScript = row.optString("combatScript");
                    String rightClickScript = row.optString("rightClickScript");
                    boolean uniqueMod = row.optBoolean("uniqueMod");

                    String incompatibleWith = row.optString("incompatibleWith");
                    List<String> incompatibleWithList = incompatibleWith.isEmpty()
                                                        ? new ArrayList<String>()
                                                        : Arrays.asList(getSeparatedStrings(incompatibleWith));

                    String allowedFactions = row.optString("allowedFactions");
                    List<String> allowedFactionList = allowedFactions.isEmpty()
                                                      ? new ArrayList<String>()
                                                      : fixFactionData(getSeparatedStrings(allowedFactions));
                    String forbiddenFactions = row.optString("forbiddenFactions");
                    List<String> forbiddenFactionsList = forbiddenFactions.isEmpty()
                                                         ? new ArrayList<String>()
                                                         : fixFactionData(getSeparatedStrings(forbiddenFactions));
                    String tags = row.optString("tags");
                    List<String> tagsList = tags.isEmpty()
                                            ? new ArrayList<String>()
                                            : Arrays.asList(getSeparatedStrings(tags));

                    AugmentData augmentData = new AugmentData(augmentID, manufacturerList, name, description, rarity, primarySlot,
                                                              primaryStatMods, primaryStatValues, secondarySlots, secondaryStatMods,
                                                              secondaryStatValues, augmentQuality, additionalDescription, equalQualityRoll,
                                                              beforeCreationScript, afterCreationScript, combatScript, rightClickScript,
                                                              uniqueMod, incompatibleWithList, allowedFactionList, forbiddenFactionsList,
                                                              tagsList);

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

    private static List<StatApplier> getStatModsFromString(String statModString) {
        String[] strings = getSeparatedStrings(statModString);

        return convertStatMods(strings);
    }

    public static List<StatApplier> convertStatMods(String... statModKeys) {
        List<StatApplier> statMods = new ArrayList<>();

        for (String statModAsString : statModKeys) {
            String match = StringAutocorrect.findBestStringMatch(statModAsString, StatModProvider.getStatModIDs());
            StatApplier statMod = StatModProvider.getStatMod(match);

            if (isNull(statMod)) {
                log.warn(String.format("VT: Error while parsing '%s' - No matching StatMod found", statModAsString));
            } else {
                statMods.add(statMod);
            }
        }

        return statMods;
    }

    public static StatModValue<Float, Float, Boolean, Boolean> applyStatRangeModifier(StatModValue<Float, Float, Boolean, Boolean> stats,
                                                                                      float modifier) {
        if (stats.minValue == null || stats.maxValue == null) {
            return stats;
        }

        float mod = Math.min(1f, 1f - modifier);
        float halfRange = (stats.maxValue - stats.minValue) / 2;

        return new StatModValue<>(
                stats.minValue + (halfRange * mod),
                stats.maxValue - (halfRange * mod),
                stats.getsModified,
                stats.invertModifier
        );
    }

    private static List<SlotCategory> getSlotsFromString(String slotString) {
        List<SlotCategory> slotCategories = new ArrayList<>();

        for (String slotAsString : slotString.split("\\s*(,\\s*)+")) {
            SlotCategory category = SlotCategory.getEnum(slotAsString);

            if (!isNull(category)) {
                slotCategories.add(category);
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

    private static String[] getAugmentQualityRange(String[] augmentQualityValueString) {
        if (augmentQualityValueString.length == 1 && augmentQualityValueString[0].isEmpty()) {
            return AugmentQuality.allowedValues;
        }

        List<String> qualityList = new ArrayList<>();
        for (String quality : augmentQualityValueString) {
            String match = StringAutocorrect.findBestStringMatch(quality, AugmentQuality.getNameList());

            if (!isNull(match)) {
                qualityList.add(match);
            }
        }

        return qualityList.isEmpty() ? AugmentQuality.allowedValues : qualityList.toArray(new String[]{});
    }

    private static String findFaction(String factionID) {
        FactionAPI match = StringAutocorrect.findBestFactionMatch(factionID);

        return isNull(match) ? null : match.getId();
    }

    private static List<String> fixManufacturerData(List<String> manufacturerData) {
        List<String> fixedList = new ArrayList<>();

        for (String data : manufacturerData) {
            String factionID = findFaction(data);
            fixedList.add(isNull(factionID) ? data : factionID);
        }

        return fixedList;
    }

    private static List<String> fixFactionData(String[] factionData) {
        List<String> factionList = new ArrayList<>();

        for (String factionID : factionData) {
            String matchingFaction = findFaction(factionID);

            if (!isNull(matchingFaction)) {
                factionList.add(matchingFaction);
            }
        }

        return factionList;
    }
}

