package de.schafunschaf.voidtec.combat.vesai.augments;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.helper.TextWithHighlights;

import java.util.List;
import java.util.Random;

public interface AugmentApplier {

    void applyToShip(MutableShipStatsAPI stats, String id, Random random);

    void applyToFighter(MutableShipStatsAPI stats, String id);

    void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory,
                         boolean isItemTooltip);

    void generateStatDescription(TooltipMakerAPI tooltip, float padding, Boolean isPrimary);

    void runCustomScript(ShipAPI ship, float amount);

    String getAugmentID();

    String getName();

    TextWithHighlights getDescription();

    String getManufacturer();

    SlotCategory getPrimarySlot();

    List<SlotCategory> getSecondarySlots();

    List<StatApplier> getPrimaryStatMods();

    List<StatApplier> getSecondaryStatMods();

    AugmentQuality getAugmentQuality();

    AugmentQuality getInitialQuality();

    TextWithHighlights getCombatScriptDescription();

    List<StatApplier> getActiveStatMods();

    AugmentApplier damageAugment(int numLevelsDamaged);

    AugmentApplier repairAugment(int numLevelsRepaired);

    void installAugment(AugmentSlot augmentSlot);

    AugmentSlot getInstalledSlot();

    void removeAugment();

    void updateFighterStatValue(String id, float value);

    Float getFighterStatValue(String id);

    boolean isInPrimarySlot();
}
