package de.schafunschaf.voidtec.scripts.combat.effects.esu;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.Random;

@Getter
@AllArgsConstructor
public enum UpgradeQuality {
    SALVAGED("Salvaged", 30, .5f, 1.25f, 50, new Color(160, 60, 0)),
    CIVILIAN("Civilian", 50, .8f, 1f, 35, new Color(200, 200, 200)),
    MILITARY("Military", 30, 1f, .7f, 20, new Color(0, 180, 50)),
    EXPERIMENTAL("Experimental", 12, 1.3f, .50f, 15, new Color(0, 150, 255)),
    REMNANT("Remnant", 5, 1.7f, .4f, 10, new Color(70, 255, 235)),
    DOMAIN("Domain", 1, 2f, .25f, 5, new Color(255, 120, 0));

    String name;
    int weightingModifier;
    float positiveModifier;
    float negativeModifier;
    int chanceForNegativeStats;
    Color color;

    public static UpgradeQuality getRandomQuality(Random random) {
        WeightedRandomPicker<UpgradeQuality> picker = new WeightedRandomPicker<>(random);
        UpgradeQuality[] values = values();
        for (int i = 0; i < values.length; i++)
            picker.add(values[i], values[i].weightingModifier);

        return picker.pick();
    }

    public static Color getColorByName(String qualityName) {
        for (UpgradeQuality value : values())
            if (value.name.equals(qualityName))
                return value.getColor();

        return null;
    }
}
