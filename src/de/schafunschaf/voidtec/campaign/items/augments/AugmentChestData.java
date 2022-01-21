package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import de.schafunschaf.voidtec.util.ColorShifter;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class AugmentChestData extends SpecialItemData {
    private final CargoAPI chestStorage;
    private final int maxSize;
    private int currentSize;
    private final ColorShifter colorShifter;

    public AugmentChestData(String id, String data, int size) {
        super(id, data);
        this.chestStorage = Global.getFactory().createCargo(true);
        this.colorShifter = new ColorShifter(Color.RED);
        this.maxSize = 100;
        this.currentSize = 0;
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
