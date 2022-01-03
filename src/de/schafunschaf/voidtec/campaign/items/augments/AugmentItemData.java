package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.campaign.SpecialItemData;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.BaseAugment;
import lombok.Getter;

@Getter
public class AugmentItemData extends SpecialItemData {
    private final BaseAugment augment;

    public AugmentItemData(String id, String data, BaseAugment augment) {
        super(id, data);
        this.augment = augment;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((augment == null) ? 0 : augment.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AugmentItemData) {
            BaseAugment otherAugment = ((AugmentItemData) obj).augment;
            boolean isSameAugment = otherAugment.getAugmentID().equals(this.augment.getAugmentID());
            boolean isSameQuality = otherAugment.getUpgradeQuality() == this.augment.getUpgradeQuality();

            return isSameAugment && isSameQuality;
        }

        return false;
    }
}
