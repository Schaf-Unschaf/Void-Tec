package de.schafunschaf.voidtec.scripts.combat.effects;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;

import java.util.Set;

public abstract class BaseCategory implements StatCategory {
    protected UpgradeCategory upgradeCategory;
    protected Set<de.schafunschaf.voidtec.scripts.combat.effects.StatApplier> statApplierSet;

    @Override
    public void addStatApplier(de.schafunschaf.voidtec.scripts.combat.effects.StatApplier statApplier) {
        statApplierSet.add(statApplier);
    }

    @Override
    public Set<de.schafunschaf.voidtec.scripts.combat.effects.StatApplier> getAllStatAppliers() {
        return statApplierSet;
    }

    @Override
    public boolean hasStatApplier() {
        return !statApplierSet.isEmpty();
    }

    @Override
    public void generateCategorySection(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        tooltip.addSectionHeading(upgradeCategory.name(), Alignment.MID, 3f);
        tooltip.addSpacer(3f);
        for (StatApplier statApplier : statApplierSet) {
            statApplier.generateTooltipEntry(stats, id, tooltip);
        }
        reset();
    }

    @Override
    public UpgradeCategory getUpgradeCategory() {
        return upgradeCategory;
    }

    @Override
    public void reset() {
        statApplierSet.clear();
    }
}
