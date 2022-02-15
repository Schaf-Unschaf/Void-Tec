package de.schafunschaf.voidtec.combat.vesai.augments;

import com.fs.starfarer.api.util.WeightedRandomPicker;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum AugmentQuality {
    DESTROYED("Destroyed", 0f, new Color(255, 0, 0), 30f),
    DEGRADED("Degraded", 0.6f, new Color(200, 50, 0), 100f),
    COMMON("Common", 1f, new Color(200, 200, 200), 70f),
    MILITARY("Military", 1.2f, new Color(0, 180, 50), 45f),
    EXPERIMENTAL("Experimental", 1.5f, new Color(0, 150, 255), 20f),
    REMNANT("Remnant", 1.7f, new Color(70, 255, 235), 5f),
    DOMAIN("Domain", 2f, new Color(255, 120, 0), 1f),
    UNIQUE("Unique", 1f, new Color(200, 0, 255), 0f);

    public static final AugmentQuality[] values = values();
    public static final String[] allowedValues = getAllowedQualitiesArray();
    private final String name;
    private final float modifier;
    private final Color color;
    private final float weighing;

    private static List<AugmentQuality> getAllowedQualities() {
        List<AugmentQuality> allowedSet = new ArrayList<>();

        allowedSet.add(DEGRADED);
        allowedSet.add(COMMON);
        allowedSet.add(MILITARY);
        allowedSet.add(EXPERIMENTAL);
        allowedSet.add(REMNANT);
        allowedSet.add(DOMAIN);

        return allowedSet;
    }

    private static String[] getAllowedQualitiesArray() {
        return new String[]{DEGRADED.name(), COMMON.name(), MILITARY.name(), EXPERIMENTAL.name(), REMNANT.name(), DOMAIN.name()};
    }

    public static AugmentQuality getRandomQuality(Random random, List<AugmentQuality> allowedQualities, boolean ignoreWeighting) {
        if (isNull(random)) {
            random = new Random();
        }

        if (allowedQualities.isEmpty()) {
            allowedQualities.addAll(getAllowedQualities());
        }

        WeightedRandomPicker<AugmentQuality> picker = new WeightedRandomPicker<>(random);
        if (ignoreWeighting) {
            picker.addAll(getAllowedQualities());
        } else {
            for (AugmentQuality quality : allowedQualities) {
                picker.add(quality, quality.getWeighing());
            }
        }

        return picker.pick();
    }

    public static AugmentQuality getRandomQuality(Random random, boolean ignoreWeighting) {
        return getRandomQuality(random, getAllowedQualities(), ignoreWeighting);
    }

    public static AugmentQuality getMinQuality(AugmentQuality minQuality, Random random, boolean ignoreWeighting) {
        if (isNull(minQuality)) {
            return getRandomQuality(random, ignoreWeighting);
        }
        List<AugmentQuality> augmentQualityList = new ArrayList<>();
        for (AugmentQuality value : getAllowedQualities()) {
            if (value.ordinal() >= minQuality.ordinal()) {
                augmentQualityList.add(value);
            }
        }

        return getRandomQuality(random, augmentQualityList, ignoreWeighting);
    }

    public static AugmentQuality getMaxQuality(AugmentQuality maxQuality, Random random, boolean ignoreWeighting) {
        if (isNull(maxQuality)) {
            return getRandomQuality(random, ignoreWeighting);
        }
        List<AugmentQuality> augmentQualityList = new ArrayList<>();
        for (AugmentQuality value : getAllowedQualities()) {
            if (value.ordinal() <= maxQuality.ordinal()) {
                augmentQualityList.add(value);
            }
        }

        return getRandomQuality(random, augmentQualityList, ignoreWeighting);
    }

    public static AugmentQuality getRandomQualityInRange(String[] qualityStrings, Random random, boolean ignoreWeighting) {
        if (isNull(qualityStrings)) {
            return getRandomQuality(random, ignoreWeighting);
        }

        if (qualityStrings.length == 1) {
            return getEnum(qualityStrings[0]);
        }

        AugmentQuality minQuality = getEnum(qualityStrings[0]);
        AugmentQuality maxQuality = getEnum(qualityStrings[1]);
        if (isNull(minQuality) && isNull(maxQuality)) {
            return getRandomQuality(random, ignoreWeighting);
        }
        if (isNull(minQuality)) {
            return getMaxQuality(maxQuality, random, ignoreWeighting);
        }
        if (isNull(maxQuality)) {
            return getMinQuality(minQuality, random, ignoreWeighting);
        }

        List<AugmentQuality> augmentQualityList = new ArrayList<>();
        for (AugmentQuality value : getAllowedQualities()) {
            if (value.ordinal() >= minQuality.ordinal() && value.ordinal() <= maxQuality.ordinal()) {
                augmentQualityList.add(value);
            }
        }

        return getRandomQuality(random, augmentQualityList, ignoreWeighting);
    }

    public static AugmentQuality getEnum(String valueString) {
        for (AugmentQuality value : values) {
            if (value.name.equalsIgnoreCase(valueString)) {
                return value;
            }
        }

        return null;
    }

    public AugmentQuality getHigherQuality() {
        return ordinal() == values.length - 1 ? this : values[ordinal() + 1];
    }

    public AugmentQuality getLowerQuality() {
        return ordinal() == 0 ? this : values[ordinal() - 1];
    }
}
