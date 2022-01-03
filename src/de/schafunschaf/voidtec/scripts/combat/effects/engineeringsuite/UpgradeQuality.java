package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.util.ComparisonTools;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

@Getter
@AllArgsConstructor
public enum UpgradeQuality {
    SALVAGED("Salvaged", 0.75f, new Color(160, 60, 0)),
    INDUSTRIAL("Industrial", 1f, new Color(200, 200, 200)),
    MILITARY("Military", 1.2f, new Color(0, 180, 50)),
    EXPERIMENTAL("Experimental", 1.4f, new Color(0, 150, 255)),
    REMNANT("Remnant", 1.7f, new Color(70, 255, 235)),
    DOMAIN("Domain", 2f, new Color(255, 120, 0));

    String name;
    float modifier;
    Color color;

    public static UpgradeQuality getRandomQuality(Random random) {
        if (ComparisonTools.isNull(random))
            random = new Random();

        WeightedRandomPicker<UpgradeQuality> picker = new WeightedRandomPicker<>(random);
        picker.addAll(Arrays.asList(values()));
        return picker.pick();
    }

    public static Color getColorByName(String qualityName) {
        for (UpgradeQuality value : values())
            if (value.name.equals(qualityName))
                return value.getColor();

        return null;
    }
}
