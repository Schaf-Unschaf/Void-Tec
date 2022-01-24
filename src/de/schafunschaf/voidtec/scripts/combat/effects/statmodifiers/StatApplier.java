package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;

import java.awt.Color;
import java.util.Random;

public interface StatApplier {
    void apply(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
               AugmentQuality quality);

    void remove(MutableShipStatsAPI stats, String id);

    void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor);

    void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float avgModValue);
}
