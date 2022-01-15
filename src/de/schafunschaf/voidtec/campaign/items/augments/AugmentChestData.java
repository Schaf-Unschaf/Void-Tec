package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import lombok.Getter;

@Getter
public class AugmentChestData extends SpecialItemData {
    private final CargoAPI chestStorage;

    public AugmentChestData(String id, String data) {
        super(id, data);
        this.chestStorage = Global.getFactory().createCargo(true);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((chestStorage == null) ? 0 : chestStorage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
