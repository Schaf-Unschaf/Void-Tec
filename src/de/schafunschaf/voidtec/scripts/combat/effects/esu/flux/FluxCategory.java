package de.schafunschaf.voidtec.scripts.combat.effects.esu.flux;

import de.schafunschaf.voidtec.scripts.combat.effects.BaseCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;

import java.util.HashSet;

public class FluxCategory extends BaseCategory {
    public FluxCategory() {
        upgradeCategory = UpgradeCategory.FLUX;
        statApplierSet = new HashSet<>();
    }
}
