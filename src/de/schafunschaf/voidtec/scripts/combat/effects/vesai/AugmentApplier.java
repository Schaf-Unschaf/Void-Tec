package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.util.TextWithHighlights;

import java.util.List;
import java.util.Random;

public interface AugmentApplier {
    void apply(MutableShipStatsAPI stats, String id, Random random, AugmentQuality quality, boolean isPrimary);

    void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory, boolean isPrimary, boolean isItemTooltip);

    void generateStatDescription(TooltipMakerAPI tooltip, boolean isPrimary, float padding);

    void runCombatScript(ShipAPI ship, float amount);

    String getAugmentID();

    String getName();

    TextWithHighlights getDescription();

    String getManufacturer();

    SlotCategory getPrimarySlot();

    List<SlotCategory> getSecondarySlots();

    List<BaseStatMod> getPrimaryStatMods();

    List<BaseStatMod> getSecondaryStatMods();

    AugmentQuality getAugmentQuality();

    AugmentQuality getInitialQuality();

    TextWithHighlights getCombatScriptDescription();

    AugmentApplier damageAugment(int numLevelsDamaged);

    AugmentApplier repairAugment(int numLevelsRepaired);

    void installAugment(AugmentSlot augmentSlot);

    void removeAugment();
}
