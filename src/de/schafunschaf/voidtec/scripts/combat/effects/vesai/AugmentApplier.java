package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;

import java.util.List;
import java.util.Random;

public interface AugmentApplier {
    void apply(MutableShipStatsAPI stats, String id, Random random, AugmentQuality quality, boolean isPrimary);

    void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory, boolean isPrimary);

    void generateStatDescription(TooltipMakerAPI tooltip, boolean isPrimary, float padding);

    void runCombatScript(ShipAPI ship, float amount);

    String getAugmentID();

    String getName();

    String getDescription();

    SlotCategory getPrimarySlot();

    List<SlotCategory> getSecondarySlots();

    List<BaseStatMod> getPrimaryStatMods();

    List<BaseStatMod> getSecondaryStatMods();

    AugmentQuality getAugmentQuality();

    AugmentQuality getInitialQuality();

    void damageAugment();

    void repairAugment();

    void setInstalledSlot(AugmentSlot augmentSlot);
}
