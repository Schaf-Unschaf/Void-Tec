package de.schafunschaf.voidtec.scripts.combat.effects;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;

import java.util.Set;

public interface StatCategory {
    void addStatApplier(de.schafunschaf.voidtec.scripts.combat.effects.StatApplier statApplier);

    Set<StatApplier> getAllStatAppliers();

    boolean hasStatApplier();

    void generateCategorySection(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip);

    UpgradeCategory getUpgradeCategory();

    void reset();
}
