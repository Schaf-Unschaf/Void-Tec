package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.VT_Icons.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum SlotCategory {
    UNIVERSAL("Universal", new Color(200, 200, 200), 1, UNIVERSAL_SLOT_ICON),
    WEAPON("Weapon", new Color(200, 0, 0), 4, WEAPON_SLOT_ICON),
    STRUCTURE("Structure", new Color(0, 200, 0), 4, STRUCTURE_SLOT_ICON),
    SHIELD("Shield", new Color(0, 200, 255), 4, SHIELD_SLOT_ICON),
    REACTOR("Reactor", new Color(170, 70, 225), 3, REACTOR_SLOT_ICON),
    ENGINE("Engine", new Color(255, 100, 0), 3, ENGINE_SLOT_ICON),
    LOGISTIC("Logistic", new Color(200, 200, 0), 1, LOGISTIC_SLOT_ICON),
    SPECIAL("Special", new Color(0, 200, 150), 0, SPECIAL_SLOT_ICON);

    String name;
    Color color;
    int weighting;
    String icon;

    public static final SlotCategory[] values = values();

    public static List<SlotCategory> getAllowedCategories() {
        List<SlotCategory> allowedCategories = new ArrayList<>();

        allowedCategories.add(UNIVERSAL);
        allowedCategories.add(WEAPON);
        allowedCategories.add(STRUCTURE);
        allowedCategories.add(SHIELD);
        allowedCategories.add(REACTOR);
        allowedCategories.add(ENGINE);
        allowedCategories.add(LOGISTIC);

        return allowedCategories;
    }

    public static SlotCategory getRandomCategory(Random random) {
        if (isNull(random))
            random = new Random();

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : getAllowedCategories())
            picker.add(slotCategory, slotCategory.weighting);

        return picker.pick();
    }

    public static SlotCategory getRandomCategory(Random random, List<SlotCategory> excludeList) {
        if (isNull(random))
            random = new Random();

        if (isNull(excludeList) || excludeList.isEmpty())
            return getRandomCategory(random);

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : getAllowedCategories()) {
            if (!excludeList.contains(slotCategory))
                picker.add(slotCategory, slotCategory.weighting);
        }

        return picker.pick();
    }

    @Override
    public String toString() {
        return ("" + name().charAt(0)).toUpperCase() + name().substring(1).toLowerCase();
    }

    public static SlotCategory getEnum(String valueString) {
        for (SlotCategory value : values)
            if (value.name.equalsIgnoreCase(valueString))
                return value;

        return null;
    }
}
