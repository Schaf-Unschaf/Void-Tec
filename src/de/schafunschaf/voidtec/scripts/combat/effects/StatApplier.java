package de.schafunschaf.voidtec.scripts.combat.effects;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;

import java.util.Random;

public interface StatApplier {
    void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality);

    void remove(MutableShipStatsAPI stats, String id);

    boolean canApply(MutableShipStatsAPI stats);

    void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip);

    UpgradeCategory getCategory();
}
