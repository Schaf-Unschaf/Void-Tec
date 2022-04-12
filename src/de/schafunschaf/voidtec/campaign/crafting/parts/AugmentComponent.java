package de.schafunschaf.voidtec.campaign.crafting.parts;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import lombok.Getter;

@Getter
public class AugmentComponent implements CraftingComponent {

    private final String name;
    private final SlotCategory partCategory;
    private final AugmentQuality partQuality;
    private int amount = 0;

    public AugmentComponent(String partName, AugmentQuality quality) {
        this.name = partName;
        this.partCategory = null;
        this.partQuality = quality;
    }

    public AugmentComponent(SlotCategory category, AugmentQuality quality) {
        this.name = category.getName();
        this.partCategory = category;
        this.partQuality = quality;
    }

    @Override
    public void addAmount(int amount) {
        this.amount += amount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AugmentComponent)) {
            return false;
        }

        AugmentComponent component = (AugmentComponent) obj;
        return this.name.equals(component.name)
                && this.partQuality == component.getPartQuality();

    }
}
