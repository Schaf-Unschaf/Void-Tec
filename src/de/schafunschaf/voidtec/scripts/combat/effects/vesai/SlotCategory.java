package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.VT_Icons.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum SlotCategory {
    UNIVERSAL("Universal", new Color(255, 255, 255), 5, UNIVERSAL_SLOT_ICON),
    WEAPON("Weapon", new Color(255, 0, 0), 10, WEAPON_SLOT_ICON),
    STRUCTURE("Structure", new Color(0, 200, 0), 10, STRUCTURE_SLOT_ICON),
    REACTOR("Reactor", new Color(100, 100, 255), 8, REACTOR_SLOT_ICON),
    ENGINE("Engine", new Color(255, 100, 0), 8, ENGINE_SLOT_ICON),
    LOGISTIC("Logistic", new Color(255, 255, 0), 9, LOGISTIC_SLOT_ICON),
    SYSTEM("System", new Color(200, 0, 255), 3, SYSTEM_SLOT_ICON),
    SPECIAL("Special", new Color(0, 255, 150), 100, SPECIAL_SLOT_ICON);

    String name;
    Color color;
    int weighting;
    String icon;

    public static final SlotCategory[] values = values();


    public static SlotCategory getRandomCategory(Random random) {
        if (isNull(random))
            random = new Random();

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : values)
            picker.add(slotCategory, slotCategory.weighting);

        return picker.pick();
    }

    public static SlotCategory getRandomCategory(Random random, List<SlotCategory> excludeList) {
        if (isNull(random))
            random = new Random();

        if (isNull(excludeList) || excludeList.isEmpty())
            return getRandomCategory(random);

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : values) {
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
