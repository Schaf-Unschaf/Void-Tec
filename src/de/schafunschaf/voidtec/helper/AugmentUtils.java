package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.BaseAugment;

import java.awt.*;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentUtils {
    public static CargoAPI getAugmentsInPlayerCargo() {
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        CargoAPI cargo = Global.getFactory().createCargo(true);

        for (CargoStackAPI itemData : playerCargo.getStacksCopy()) {
            SpecialItemData specialItemData = itemData.getSpecialDataIfSpecial();
            if (isNull(specialItemData))
                continue;

            if (specialItemData.getId().equals(VT_Items.AUGMENT_ITEM))
                cargo.addFromStack(itemData);
        }

        return cargo;
    }

    public static void adjustItemInCargo(CargoAPI sourceCargo, CargoAPI cargoToAdjust) {
        List<CargoStackAPI> sourceCargoStacks = sourceCargo.getStacksCopy();

        for (CargoStackAPI sourceCargoStack : sourceCargoStacks)
            cargoToAdjust.removeItems(CargoAPI.CargoItemType.SPECIAL, sourceCargoStack.getData(), sourceCargoStack.getSize());
    }

    public static BaseAugment getAugmentFromStack(CargoStackAPI cargoStack) {
        if (isNull(cargoStack))
            return null;

        return ((AugmentItemData) cargoStack.getData()).getAugment();
    }

    public static Color getManufacturerColor(String manufacturerString) {
        FactionAPI faction = Global.getSector().getFaction(manufacturerString);
        Color manufacturerColor = isNull(faction) ? Global.getSettings().getDesignTypeColor(manufacturerString) : faction.getColor();

        return isNull(manufacturerColor) ? Misc.getGrayColor() : manufacturerColor;
    }
}
