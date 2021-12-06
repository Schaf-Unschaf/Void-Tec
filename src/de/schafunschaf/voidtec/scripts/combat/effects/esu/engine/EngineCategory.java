package de.schafunschaf.voidtec.scripts.combat.effects.esu.engine;

import de.schafunschaf.voidtec.scripts.combat.effects.BaseCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;

import java.util.HashSet;

public class EngineCategory extends BaseCategory {
    public EngineCategory() {
        upgradeCategory = UpgradeCategory.ENGINE;
        statApplierSet = new HashSet<>();
    }
}
