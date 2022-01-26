package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.campaign.SpecialItemData;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.MalfunctionEffect;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import lombok.Getter;

@Getter
public class AugmentItemData extends SpecialItemData {

    private final AugmentApplier augment;
    private MalfunctionEffect malfunctionEffect;
    private ColorShifter colorShifter;

    public AugmentItemData(String id, String data, AugmentApplier augment) {
        super(id, data);
        this.augment = augment;
        float modifier = augment.getAugmentQuality().getModifier();
        float breathingLength = 300f;
        if (augment.getAugmentQuality() == AugmentQuality.UNIQUE) {
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
            boolean isSameAugment = otherAugment.getAugmentID().equals(this.augment.getAugmentID());
            boolean isSameQuality = otherAugment.getAugmentQuality() == this.augment.getAugmentQuality();

            return isSameAugment && isSameQuality;
        }

        return false;
    }
}
