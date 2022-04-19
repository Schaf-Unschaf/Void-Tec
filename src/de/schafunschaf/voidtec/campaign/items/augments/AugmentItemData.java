package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.MalfunctionEffect;
import lombok.Getter;

import java.util.Objects;

@Getter
public class AugmentItemData extends SpecialItemData {

    private final AugmentApplier augment;
    private MalfunctionEffect malfunctionEffect;
    private ColorShifter colorShifter;

    public AugmentItemData(String id, String data, AugmentApplier augment) {
        super(id, data);
        this.augment = augment;
        if (!augment.isStackable()) {
            setData(Misc.genUID());
        }
        float modifier = augment.getAugmentQuality().getModifier();
        float breathingLength = 300f;
        if (augment.getAugmentQuality() == AugmentQuality.CUSTOMISED) {
            colorShifter = new ColorShifter(null);
        } else if (!(augment.getAugmentQuality() == AugmentQuality.DESTROYED || augment.getAugmentQuality() == AugmentQuality.DOMAIN)) {
            float maxTimeAtFull = (1 / (3f - modifier)) * breathingLength * 3;
            int flickerChance = (int) ((2f - modifier) / (modifier * modifier) * 50);
            int maxNumFlickers = Math.max(Math.round(6 - modifier * 3), 1);

            malfunctionEffect = new MalfunctionEffect(breathingLength, maxTimeAtFull, flickerChance, maxNumFlickers, modifier);
        }
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
            AugmentApplier otherAugment = ((AugmentItemData) obj).augment;
            if (this.getAugment().isDestroyed() && otherAugment.isDestroyed()) {
                return true;
            }
            boolean isStackable = Objects.equals(this.getData(), ((AugmentItemData) obj).getData());
            boolean isSameAugment = otherAugment.getAugmentID().equals(this.augment.getAugmentID());
            boolean hasSameInitQuality = otherAugment.getInitialQuality() == this.augment.getInitialQuality();
            boolean hasSameCurQuality = otherAugment.getAugmentQuality() == this.augment.getAugmentQuality();

            return isStackable && isSameAugment && hasSameInitQuality && hasSameCurQuality;
        }

        return false;
    }
}
