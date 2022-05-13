package de.schafunschaf.voidtec.combat.vesai.augments;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;

import java.util.*;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

public class AugmentDataManager {

    private static final Map<String, AugmentData> AUGMENT_DATA_MAP = new HashMap<>();

    public static AugmentApplier getAugment(String augmentID, AugmentQuality augmentQuality) {
        return createAugmentFromData(AUGMENT_DATA_MAP.get(augmentID.toLowerCase()), augmentQuality);
    }

    public static AugmentApplier cloneAugment(AugmentApplier augment) {
        AugmentApplier clonedAugment = createAugmentFromData(AUGMENT_DATA_MAP.get(augment.getAugmentID().toLowerCase()),
                                                             augment.getInitialQuality());
        if (!isNull(clonedAugment)) {
            clonedAugment.damageAugment(augment.getInitialQuality().ordinal() - augment.getAugmentQuality().ordinal(), false);
        }

        return clonedAugment;
    }

    public static void storeAugmentData(String augmentID, AugmentData augment) {
        AUGMENT_DATA_MAP.put(augmentID.toLowerCase(), augment);
    }

    public static AugmentApplier getRandomAugment(Random random, boolean ignoreWeighting) {
        return getRandomAugment(null, null, null, null, random, ignoreWeighting);
    }

    public static AugmentApplier getRandomAugment(SlotCategory slotCategory, AugmentQuality augmentQuality, FactionAPI faction,
                                                  List<String> tags, Random random, boolean ignoreWeighting) {
        if (isNull(random)) {
            random = new Random();
        }

        WeightedRandomPicker<AugmentData> picker = new WeightedRandomPicker<>(random);
        if (!ignoreWeighting) {
            picker.add(null, 100);
        }

        Collection<AugmentData> augmentDataCollection = AUGMENT_DATA_MAP.values();

        if (isNull(augmentQuality)) {
            augmentQuality = AugmentQuality.getRandomQuality(random, ignoreWeighting);
        }

        float factionMult;
        float sameFactionMult = 3f;

        if (!isNull(slotCategory)) {
            for (AugmentData augmentData : augmentDataCollection) {
                String[] augmentQualityRange = augmentData.getAugmentQualityRange();
                List<String> allowedFactions = augmentData.getAllowedFactions();
                List<String> forbiddenFactions = augmentData.getForbiddenFactions();
                List<String> augmentDataTags = augmentData.getTags();

                factionMult = !isNull(faction) && augmentData.getManufacturer().contains(faction.getId()) ? sameFactionMult : 1f;
                float weight = ignoreWeighting ? 1 : augmentData.getRarity() * factionMult;

                boolean matchesTag = true;
                boolean matchesAllowedFaction = true;
                boolean isNotForbiddenFaction = true;
                boolean isPrimarySlot = augmentData.getPrimarySlot().equals(slotCategory);
                boolean isSecondarySlot = !isNull(augmentData.getSecondarySlots())
                        && augmentData.getSecondarySlots().contains(slotCategory);
                boolean matchesSlot = isPrimarySlot || !isNull(augmentData.getSecondarySlots()) && isSecondarySlot;
                boolean matchesQuality = Arrays.asList(augmentQualityRange).contains(augmentQuality.name());

                if (!isNullOrEmpty(augmentDataTags) && !isNullOrEmpty(tags)) {
                    matchesTag = !Collections.disjoint(augmentData.getTags(), tags);
                }

                if (!isNull(faction) && !isNullOrEmpty(allowedFactions)) {
                    matchesAllowedFaction = allowedFactions.contains(faction.getId());
                }

                if (!isNull(faction) && !isNullOrEmpty(forbiddenFactions)) {
                    isNotForbiddenFaction = !forbiddenFactions.contains(faction.getId());
                }

                if (matchesTag && matchesAllowedFaction && isNotForbiddenFaction && matchesSlot && matchesQuality) {
                    picker.add(augmentData, weight);
                }
            }
        } else {
            for (AugmentData augmentData : augmentDataCollection) {
                String[] augmentQualityRange = augmentData.getAugmentQualityRange();
                List<String> allowedFactions = augmentData.getAllowedFactions();
                List<String> forbiddenFactions = augmentData.getForbiddenFactions();
                List<String> augmentDataTags = augmentData.getTags();

                factionMult = !isNull(faction) && augmentData.getManufacturer().contains(faction.getId()) ? sameFactionMult : 1f;
                float weight = ignoreWeighting ? 1 : augmentData.getRarity() * factionMult;

                boolean matchesTag = true;
                boolean matchesAllowedFaction = true;
                boolean isNotForbiddenFaction = true;
                boolean matchesQuality = Arrays.asList(augmentQualityRange).contains(augmentQuality.name());

                if (!isNullOrEmpty(augmentDataTags) && !isNullOrEmpty(tags)) {
                    matchesTag = !Collections.disjoint(augmentData.getTags(), tags);
                }

                if (!isNull(faction) && !isNullOrEmpty(allowedFactions)) {
                    matchesAllowedFaction = allowedFactions.contains(faction.getId());
                }

                if (!isNull(faction) && !isNullOrEmpty(forbiddenFactions)) {
                    isNotForbiddenFaction = !forbiddenFactions.contains(faction.getId());
                }

                if (matchesTag && matchesAllowedFaction && isNotForbiddenFaction && matchesQuality) {
                    picker.add(augmentData, weight);
                }
            }
        }

        return createAugmentFromData(picker.pick(), augmentQuality);
    }

    private static AugmentApplier createAugmentFromData(AugmentData augmentData, AugmentQuality augmentQuality) {
        return isNull(augmentData) ? null : new BaseAugment(augmentData, augmentQuality);
    }
}
