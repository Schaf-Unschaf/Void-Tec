package de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;

import java.util.*;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentDataManager {
    private static final Map<String, AugmentData> AUGMENT_DATA_MAP = new HashMap<>();

    public static BaseAugment getAugment(String augmentID) {
        return createAugmentFromData(AUGMENT_DATA_MAP.get(augmentID), null);
    }

    public static void storeAugmentData(String augmentID, AugmentData augment) {
        AUGMENT_DATA_MAP.put(augmentID, augment);
    }

    public static BaseAugment getRandomAugment(Random random) {
        return getRandomAugment(null, random);
    }

    public static BaseAugment getRandomAugment(AugmentQuality augmentQuality, Random random) {
        return getRandomAugment(null, augmentQuality, random);
    }

    public static BaseAugment getRandomAugment(SlotCategory slotCategory, AugmentQuality augmentQuality, Random random) {
        if (isNull(random))
            random = new Random();

        WeightedRandomPicker<AugmentData> picker = new WeightedRandomPicker<>(random);

        Collection<AugmentData> augmentDataCollection = AUGMENT_DATA_MAP.values();

        if (!isNull(slotCategory)) {
            for (AugmentData augmentData : augmentDataCollection)
                if (!isNull(augmentData.getPrimarySlot()) && augmentData.getPrimarySlot().equals(slotCategory))
                    if (!isNull(augmentData.getSecondarySlots()) && augmentData.getSecondarySlots().contains(slotCategory))
                        if (AugmentQuality.getQualitiesInRange(augmentData.augmentQualityRange).contains(augmentQuality))
                            picker.add(augmentData);
        } else {
            for (AugmentData augmentData : augmentDataCollection)
                if (!isNull(augmentData.getPrimarySlot()))
                    if (AugmentQuality.getQualitiesInRange(augmentData.augmentQualityRange).contains(augmentQuality))
                        picker.add(augmentData);
        }

        return createAugmentFromData(picker.pick(), random);
    }

    private static BaseAugment createAugmentFromData(AugmentData augmentData, Random random) {
        return isNull(augmentData) ? null : new BaseAugment(augmentData, random);
    }
}
