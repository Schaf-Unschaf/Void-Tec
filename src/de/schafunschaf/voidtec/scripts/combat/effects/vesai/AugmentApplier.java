package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;

import java.util.List;
import java.util.Random;

public interface AugmentApplier {

    void applyToShip(MutableShipStatsAPI stats, String id, Random random, boolean isPrimary);

    void applyToFighter(MutableShipStatsAPI stats, String id, boolean isPrimary);

    void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory,
                         boolean isPrimary, boolean isItemTooltip);

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

    AugmentSlot getInstalledSlot();

    void removeAugment();

    void updateFighterStatValue(String id, float value);

    Float getFighterStatValue(String id);
}
