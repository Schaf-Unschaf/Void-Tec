package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.util.ui.StringAutocorrect;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum SlotCategory {
    WEAPON("Weapon", new Color(200, 0, 0), 2),
    FLIGHT_DECK("Flight Deck", new Color(255, 100, 0), 0),
    STRUCTURE("Structure", new Color(0, 170, 0), 2),
    SHIELD("Shield", new Color(0, 200, 255), 2),
    REACTOR("Reactor", new Color(170, 70, 225), 2),
    ENGINE("Engine", new Color(255, 200, 0), 2),
    SYSTEM("System", new Color(0, 100, 255), 2),
    COSMETIC("Cosmetic", new Color(255, 0, 255), 0),
    SPECIAL("Special", new Color(0, 200, 150), 0);

    public static final SlotCategory[] values = values();
    String name;
    Color color;
    int weighting;

    public static List<SlotCategory> getGeneralCategories() {
        List<SlotCategory> slotCategories = new ArrayList<>();

        slotCategories.add(WEAPON);
        slotCategories.add(FLIGHT_DECK);
        slotCategories.add(STRUCTURE);
        slotCategories.add(SHIELD);
        slotCategories.add(REACTOR);
        slotCategories.add(ENGINE);
        slotCategories.add(SYSTEM);

        return slotCategories;
    }

    public static SlotCategory getRandomCategory(Random random, boolean ignoreWeighting) {
        if (isNull(random)) {
            random = new Random();
        }

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : getGeneralCategories()) {
            int weight = ignoreWeighting ? 1 : slotCategory.weighting;
            picker.add(slotCategory, weight);
        }

        return picker.pick();
    }

    public static SlotCategory getRandomCategory(Random random, Map<SlotCategory, Integer> allowedCategories) {
        if (isNull(random)) {
            random = new Random();
        }

        if (isNull(allowedCategories) || allowedCategories.isEmpty()) {
            return getRandomCategory(random, false);
        }

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : allowedCategories.keySet()) {
            picker.add(slotCategory, allowedCategories.get(slotCategory));
        }

        return picker.pick();
    }

    public static SlotCategory getEnum(String valueString) {
        List<String> enumList = new ArrayList<>();
        for (SlotCategory category : values) {
            enumList.add(category.name());
        }

        String match = StringAutocorrect.findBestStringMatch(valueString, enumList);

        return isNull(match) ? null : SlotCategory.valueOf(match);
    }

    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }
}
