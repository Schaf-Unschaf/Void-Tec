package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import lombok.Getter;

import java.util.Map;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;


@Getter
public class AugmentSlot {

    private final HullModManager hullmodManager;
    private final SlotCategory slotCategory;
    private AugmentApplier slottedAugment;
    private boolean isUnlocked;

    public AugmentSlot(HullModManager hullmodManager, Random random, boolean unlocked) {
        this.hullmodManager = hullmodManager;
        this.slotCategory = SlotCategory.getRandomCategory(random);
        this.isUnlocked = unlocked;
    }

    public AugmentSlot(HullModManager hullmodManager, SlotCategory slotCategory, boolean unlocked) {
        this.hullmodManager = hullmodManager;
        this.slotCategory = slotCategory;
        this.isUnlocked = unlocked;
    }

    public AugmentSlot(HullModManager hullmodManager, Map<SlotCategory, Integer> allowedCategories, Random random, boolean unlocked) {
        this.hullmodManager = hullmodManager;
        this.slotCategory = SlotCategory.getRandomCategory(random, allowedCategories);
        this.isUnlocked = unlocked;
    }

    public void applyAfterCreation(ShipAPI ship, String id) {
        if (isNull(slottedAugment)) {
            return;
        }

        slottedAugment.applyAfterCreation(ship, id);
    }

    public void applyToShip(MutableShipStatsAPI stats, String id, int slotIndex) {
        if (isNull(slottedAugment)) {
            return;
        }

        hullmodManager.getShipStatEffectManager().addAll(slottedAugment.getActiveStatMods());

        slottedAugment.applyToShip(stats, id, slotIndex);
    }

    public void applyToFighter(MutableShipStatsAPI stats, String id) {
        if (isNull(slottedAugment)) {
            return;
        }

        slottedAugment.applyToFighter(stats, id);
    }

    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, boolean isItemTooltip) {
        if (isNull(slottedAugment)) {
            return;
        }

        slottedAugment.generateTooltip(stats, id, tooltip, width, slotCategory, isItemTooltip, false, null);
    }

    public void unlockSlot() {
        isUnlocked = true;
    }

    public boolean installAugment(AugmentApplier augment) {
        return hullmodManager.installAugment(this, augment);
    }

    protected boolean insertAugment(AugmentApplier augment) {
        if (isEmpty()) {
            slottedAugment = augment;
            augment.installAugment(this);
            return true;
        }
        return false;
    }

    public void removeAugment() {
        slottedAugment.removeAugment();
        slottedAugment = null;
    }

    public boolean isEmpty() {
        return isNull(slottedAugment);
    }

    public void collectAppliedStats(MutableShipStatsAPI stats, String id) {
        slottedAugment.collectAppliedStats(stats, id);
    }
}
