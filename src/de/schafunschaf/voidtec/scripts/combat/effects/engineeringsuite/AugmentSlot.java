package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.BaseAugment;
import lombok.Getter;

import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;


@Getter
public class AugmentSlot {
    private final SlotManager slotManager;
    private AugmentApplier slottedAugment;
    private final SlotCategory slotCategory;
    private boolean isPrimary;

    public AugmentSlot(SlotManager slotManager, Random random) {
        this.slotManager = slotManager;
        slotCategory = SlotCategory.getRandomCategory(random);
    }

    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        if (isNull(slottedAugment))
            return;

        slottedAugment.apply(stats, id, random, quality, isPrimary);
    }

    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width) {
        if (isNull(slottedAugment))
            return;

        slottedAugment.generateTooltip(stats, id, tooltip, width, slotCategory, isPrimary);
    }

    public boolean installAugment(BaseAugment augment) {
        return slotManager.installAugment(this, augment);
    }

    protected boolean insertAugment(BaseAugment augment) {
        if (isEmpty()) {
            slottedAugment = augment;
            isPrimary = slotCategory.equals(augment.getPrimarySlot());
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return isNull(slottedAugment);
    }
}
