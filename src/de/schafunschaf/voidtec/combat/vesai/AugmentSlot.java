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

    private final HullModManager hullModManager;
    private final SlotCategory slotCategory;
    private AugmentApplier slottedAugment;
    private boolean isUnlocked;

    public AugmentSlot(HullModManager hullModManager, Random random, boolean unlocked) {
        this.hullModManager = hullModManager;
        this.slotCategory = SlotCategory.getRandomCategory(random, false);
        this.isUnlocked = unlocked;
    }

    public AugmentSlot(HullModManager hullModManager, SlotCategory slotCategory, boolean unlocked) {
        this.hullModManager = hullModManager;
        this.slotCategory = slotCategory;
        this.isUnlocked = unlocked;
    }

    public AugmentSlot(HullModManager hullModManager, Map<SlotCategory, Integer> allowedCategories, Random random, boolean unlocked) {
        this.hullModManager = hullModManager;
        this.slotCategory = SlotCategory.getRandomCategory(random, allowedCategories);
        this.isUnlocked = unlocked;
    }

    public void applyBeforeCreation(MutableShipStatsAPI stats, String id) {
        if (isNull(slottedAugment)) {
            return;
        }

        slottedAugment.applyBeforeCreation(stats, id);
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

        hullModManager.getShipStatEffectManager().addAll(slottedAugment.getActiveStatMods());

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

        slottedAugment.generateTooltip(stats, id, tooltip, width, slotCategory, null);
    }

    public void unlockSlot() {
        isUnlocked = true;
    }

    public boolean installAugment(AugmentApplier augment) {
        return hullModManager.installAugment(this, augment);
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
        slottedAugment.uninstall();
        slottedAugment = null;
    }

    public boolean isEmpty() {
        return isNull(slottedAugment);
    }

    public void collectAppliedStats(MutableShipStatsAPI stats, String id) {
        slottedAugment.collectAppliedStats(stats, id);
    }
}
