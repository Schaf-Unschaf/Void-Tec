package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.SlotCategory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentManager {
    private static final Map<String, BaseAugment> augmentMap = new HashMap<>();

    public static BaseAugment getAugment(String augmentID) {
        return augmentMap.get(augmentID);
    }

    public static BaseAugment storeAugment(String augmentID, BaseAugment augment) {
        return augmentMap.put(augmentID, augment);
    }

    public static BaseAugment getRandomAugment(Random random) {
        return getRandomAugment(null, random);
    }

    public static BaseAugment getRandomAugment(SlotCategory slotCategory, Random random) {
        if (isNull(random))
            random = new Random();

        if (isNull(slotCategory))
            slotCategory = SlotCategory.getRandomCategory(null);

        WeightedRandomPicker<BaseAugment> picker = new WeightedRandomPicker<>(random);

        for (BaseAugment baseAugment : augmentMap.values()) {
            if (baseAugment.getPrimarySlot().equals(slotCategory) || baseAugment.getSecondarySlots().contains(slotCategory))
                picker.add(baseAugment);
        }

        return picker.pick();
    }
}
