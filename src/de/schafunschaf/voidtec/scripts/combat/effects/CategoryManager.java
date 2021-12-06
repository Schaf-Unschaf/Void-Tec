package de.schafunschaf.voidtec.scripts.combat.effects;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.DurabilityCategory;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.EngineCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.FluxCategory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CategoryManager {
    private static transient CategoryManager categoryManager;
    private final Set<de.schafunschaf.voidtec.scripts.combat.effects.StatCategory> allCategories = new HashSet<>();

    public CategoryManager(de.schafunschaf.voidtec.scripts.combat.effects.StatCategory... statCategories) {
        allCategories.addAll(Arrays.asList(statCategories));
    }

    public static CategoryManager getInstance() {
        if (ComparisonTools.isNull(categoryManager))
            categoryManager = new CategoryManager(
                    new DurabilityCategory(), new EngineCategory(), new FluxCategory()
            );
        return categoryManager;
    }

    public void generateCategoryEntries(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        for (de.schafunschaf.voidtec.scripts.combat.effects.StatCategory category : allCategories)
            if (category.hasStatApplier())
                category.generateCategorySection(stats, id, tooltip);
    }

    public de.schafunschaf.voidtec.scripts.combat.effects.StatCategory getMatchingCategory(UpgradeCategory upgradeCategory) {
        for (StatCategory category : allCategories)
            if (category.getUpgradeCategory().equals(upgradeCategory))
                return category;

        return null;
    }
}
