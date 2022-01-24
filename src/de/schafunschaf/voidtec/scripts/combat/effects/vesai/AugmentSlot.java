package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import lombok.Getter;

import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;


@Getter
public class AugmentSlot {
    private final HullModManager hullmodManager;
    private final SlotCategory slotCategory;
    private AugmentApplier slottedAugment;
    private boolean isPrimary;
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

    public AugmentSlot(HullModManager hullmodManager, List<SlotCategory> excludeList, Random random, boolean unlocked) {
        this.hullmodManager = hullmodManager;
        this.slotCategory = SlotCategory.getRandomCategory(random, excludeList);
        this.isUnlocked = unlocked;
    }

    public void apply(MutableShipStatsAPI stats, String id, Random random, AugmentQuality quality) {
        if (isNull(slottedAugment)) {
            return;
        }

        slottedAugment.apply(stats, id, random, quality, isPrimary);
    }

    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width,
                                boolean isItemTooltip) {
        if (isNull(slottedAugment)) {
            return;
        }

        slottedAugment.generateTooltip(stats, id, tooltip, width, slotCategory, isPrimary, isItemTooltip);
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
            isPrimary = slotCategory.equals(augment.getPrimarySlot());
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
}
