package de.schafunschaf.voidtec.combat.vesai.augments;

import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;

import java.util.*;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentDataManager {

    private static final Map<String, AugmentData> AUGMENT_DATA_MAP = new HashMap<>();

    public static AugmentApplier getAugment(String augmentID, AugmentQuality augmentQuality) {
        return createAugmentFromData(AUGMENT_DATA_MAP.get(augmentID), augmentQuality);
    }

    public static AugmentApplier cloneAugment(AugmentApplier augment) {
        AugmentApplier clonedAugment = createAugmentFromData(AUGMENT_DATA_MAP.get(augment.getAugmentID()), augment.getInitialQuality());
        if (!isNull(clonedAugment)) {
            clonedAugment.damageAugment(augment.getInitialQuality().ordinal() - augment.getAugmentQuality().ordinal());
        }

        return clonedAugment;
    }

    public static void storeAugmentData(String augmentID, AugmentData augment) {
        AUGMENT_DATA_MAP.put(augmentID, augment);
    }

    public static AugmentApplier getRandomAugment(Random random) {
        return getRandomAugment(null, null, null, random);
    }

    public static AugmentApplier getRandomAugment(SlotCategory slotCategory, AugmentQuality augmentQuality, FactionAPI faction,
                                                  Random random) {
        if (isNull(random)) {
            random = new Random();
        }

        WeightedRandomPicker<AugmentData> picker = new WeightedRandomPicker<>(random);

        Collection<AugmentData> augmentDataCollection = AUGMENT_DATA_MAP.values();

        if (isNull(augmentQuality)) {
            augmentQuality = AugmentQuality.getRandomQuality(random, false);
        }

        float factionMult;

        if (!isNull(slotCategory)) {
            for (AugmentData augmentData : augmentDataCollection) {
                factionMult = !isNull(faction) && augmentData.getManufacturer().equals(faction.getId()) ? 10f : 1f;
                if (augmentData.getPrimarySlot().equals(slotCategory)) {
                    if (Arrays.asList(augmentData.augmentQualityRange).contains(augmentQuality.name())) {
                        picker.add(augmentData, augmentData.getRarity() * factionMult);
                    }
                } else if (!isNull(augmentData.getSecondarySlots()) && augmentData.getSecondarySlots().contains(slotCategory)) {
                    if (Arrays.asList(augmentData.augmentQualityRange).contains(augmentQuality.name())) {
                        picker.add(augmentData, augmentData.getRarity() * factionMult);
                    }
                }
            }
        } else {
            for (AugmentData augmentData : augmentDataCollection) {
                factionMult = !isNull(faction) && augmentData.getManufacturer().equals(faction.getId()) ? 10f : 1f;
                if (Arrays.asList(augmentData.augmentQualityRange).contains(augmentQuality.name())) {
                    picker.add(augmentData, augmentData.getRarity() * factionMult);
                }
            }
        }

        return createAugmentFromData(picker.pick(), augmentQuality);
    }

    private static AugmentApplier createAugmentFromData(AugmentData augmentData, AugmentQuality augmentQuality) {
        return isNull(augmentData) ? null : new BaseAugment(augmentData, augmentQuality);
    }
}
