package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import lombok.Getter;

import java.awt.Color;

@Getter
public class DamagedAugmentData {

    private final String augmentName;
    private final Color slotColor;

    public DamagedAugmentData(AugmentApplier augment) {
        this.augmentName = augment.getName();
        this.slotColor = augment.getInstalledSlot().getSlotCategory().getColor();
    }
}
