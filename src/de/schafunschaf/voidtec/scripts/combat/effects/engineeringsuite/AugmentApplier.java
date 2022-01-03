package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;

import java.util.List;
import java.util.Random;

public interface AugmentApplier {
    void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality, boolean isPrimary);

    void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory, boolean isPrimary);

    String getAugmentID();

    String getName();

    String getDescription();

    SlotCategory getPrimarySlot();

    List<SlotCategory> getSecondarySlots();

    List<BaseStatMod> getPrimaryStatMods();

    List<BaseStatMod> getSecondaryStatMods();

    UpgradeQuality getUpgradeQuality();
}
