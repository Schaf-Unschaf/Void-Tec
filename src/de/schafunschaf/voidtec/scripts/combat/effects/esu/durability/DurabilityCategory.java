package de.schafunschaf.voidtec.scripts.combat.effects.esu.durability;

import de.schafunschaf.voidtec.scripts.combat.effects.BaseCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;

import java.util.HashSet;

public class DurabilityCategory extends BaseCategory {
    public DurabilityCategory() {
        upgradeCategory = UpgradeCategory.DURABILITY;
        statApplierSet = new HashSet<>();
    }
}
