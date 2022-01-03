package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.Random;

import static de.schafunschaf.voidtec.VT_Icons.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum SlotCategory {
    UNIVERSAL(new Color(255, 255, 255), new Color(90, 90, 90), 3, UNIVERSAL_SLOT_ICON),
    WEAPON(new Color(255, 0, 0), new Color(75, 0, 0), 10, WEAPON_SLOT_ICON),
    STRUCTURE(new Color(0, 200, 0), new Color(0, 50, 0), 10, STRUCTURE_SLOT_ICON),
    REACTOR(new Color(100, 100, 255), new Color(10, 10, 70), 8, REACTOR_SLOT_ICON),
    ENGINE(new Color(255, 150, 0), new Color(70, 20, 0), 8, ENGINE_SLOT_ICON),
    LOGISTIC(new Color(150, 150, 150), new Color(50, 50, 50), 9, LOGISTIC_SLOT_ICON),
    SYSTEM(new Color(200, 0, 255), new Color(50, 0, 50), 1, SYSTEM_SLOT_ICON),
    SPECIAL(new Color(0, 255, 150), new Color(0, 50, 25), 1, SPECIAL_SLOT_ICON);

    Color color;
    Color darkColor;
    int weighting;
    String icon;

    public static SlotCategory getRandomCategory(Random random) {
        if (isNull(random))
            random = new Random();

        WeightedRandomPicker<SlotCategory> picker = new WeightedRandomPicker<>(random);
        for (SlotCategory slotCategory : values())
            picker.add(slotCategory, slotCategory.weighting);

        return picker.pick();
    }

    @Override
    public String toString() {
        return ("" + name().charAt(0)).toUpperCase() + name().substring(1).toLowerCase();
    }
}
