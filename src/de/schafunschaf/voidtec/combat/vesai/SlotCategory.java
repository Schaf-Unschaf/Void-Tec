package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static de.schafunschaf.voidtec.ids.VT_Icons.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum SlotCategory {
    WEAPON("Weapon", new Color(200, 0, 0), 2, WEAPON_SLOT_ICON),
    FLIGHT_DECK("Flight Deck", new Color(255, 100, 0), 0, FLIGHT_DECK_SLOT_ICON),
    STRUCTURE("Structure", new Color(0, 170, 0), 2, STRUCTURE_SLOT_ICON),
    SHIELD("Shield", new Color(0, 200, 255), 2, SHIELD_SLOT_ICON),
    REACTOR("Reactor", new Color(170, 70, 225), 2, REACTOR_SLOT_ICON),
    ENGINE("Engine", new Color(255, 200, 0), 2, ENGINE_SLOT_ICON),
    SYSTEM("System", new Color(0, 100, 255), 2, SYSTEM_SLOT_ICON),
    COSMETIC("Cosmetic", new Color(200, 200, 200), 0, COSMETIC_SLOT_ICON),
    SPECIAL("Special", new Color(0, 200, 150), 0, SPECIAL_SLOT_ICON);

    public static final SlotCategory[] values = values();
    String name;
    Color color;
    int weighting;
    String icon;

    public static List<SlotCategory> getAllowedCategories() {
        List<SlotCategory> allowedCategories = new ArrayList<>();

        allowedCategories.add(WEAPON);
        allowedCategories.add(FLIGHT_DECK);
        allowedCategories.add(STRUCTURE);
        allowedCategories.add(SHIELD);
        allowedCategories.add(REACTOR);
        allowedCategories.add(ENGINE);
        allowedCategories.add(SYSTEM);

        return allowedCategories;
    }

    public static SlotCategory getRandomCategory(Random random) {
        if (isNull(random)) {
            random = new Random();
        }

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : getAllowedCategories()) {
            picker.add(slotCategory, slotCategory.weighting);
        }

        return picker.pick();
    }

    public static SlotCategory getRandomCategory(Random random, Map<SlotCategory, Integer> allowedCategories) {
        if (isNull(random)) {
            random = new Random();
        }

        if (isNull(allowedCategories) || allowedCategories.isEmpty()) {
            return getRandomCategory(random);
        }

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : allowedCategories.keySet()) {
            picker.add(slotCategory, allowedCategories.get(slotCategory));
        }

        return picker.pick();
    }

    public static SlotCategory getEnum(String valueString) {
        for (SlotCategory value : values) {
            if (value.name().equalsIgnoreCase(valueString) || value.name.equalsIgnoreCase(valueString)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name().replaceAll("_", " ");
    }
}
