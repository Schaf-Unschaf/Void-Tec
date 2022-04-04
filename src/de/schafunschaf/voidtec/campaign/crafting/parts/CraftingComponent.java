package de.schafunschaf.voidtec.campaign.crafting.parts;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;

public interface CraftingComponent {

    String getName();

    SlotCategory getPartCategory();

    AugmentQuality getPartQuality();

    int getAmount();

    void addAmount(int amount);
}
