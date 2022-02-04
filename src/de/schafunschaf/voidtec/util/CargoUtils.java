package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestData;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class CargoUtils {

    public static boolean sortDescending = true;

    public static List<AugmentCargoWrapper> getAugmentsInCargo() {
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        CargoAPI localCargo = Global.getFactory().createCargo(true);

        List<AugmentChestData> chestsInStorage = new ArrayList<>();
        List<AugmentCargoWrapper> augmentCargoWrappers = new ArrayList<>();

        for (EveryFrameScript transientScript : Global.getSector().getTransientScripts()) {
            if (transientScript instanceof VT_DockedAtSpaceportHelper) {
                localCargo = ((VT_DockedAtSpaceportHelper) transientScript).getMarket()
                                                                           .getSubmarket(Submarkets.SUBMARKET_STORAGE)
                                                                           .getCargo();
                break;
            }
        }

        searchCargoForItems(localCargo, chestsInStorage, augmentCargoWrappers, AugmentCargoWrapper.CargoSource.LOCAL_STORAGE);
        searchCargoForItems(playerCargo, chestsInStorage, augmentCargoWrappers, AugmentCargoWrapper.CargoSource.PLAYER_FLEET);
        for (AugmentChestData augmentChestData : chestsInStorage) {
            searchCargoForItems(augmentChestData.getChestStorage(), chestsInStorage, augmentCargoWrappers,
                                AugmentCargoWrapper.CargoSource.CARGO_CHEST);
        }

        Collections.sort(augmentCargoWrappers, new Comparator<AugmentCargoWrapper>() {
            @Override
            public int compare(AugmentCargoWrapper o1, AugmentCargoWrapper o2) {
                int compareQualityResult = sortDescending
                                           ? o2.getAugment().getAugmentQuality().compareTo(o1.getAugment().getAugmentQuality())
                                           : o1.getAugment().getAugmentQuality().compareTo(o2.getAugment().getAugmentQuality());
                if (compareQualityResult == 0) {
                    return sortDescending
                           ? o1.getAugment().getName().compareTo(o2.getAugment().getName())
                           : o2.getAugment().getName().compareTo(o1.getAugment().getName());
                } else {
                    return compareQualityResult;
                }
            }
        });

        return augmentCargoWrappers;
    }

    public static void searchCargoForItems(CargoAPI storageCargo, List<AugmentChestData> chestsInStorage,
                                           List<AugmentCargoWrapper> augmentCargoWrappers, AugmentCargoWrapper.CargoSource cargoSource) {
        for (CargoStackAPI cargoStackAPI : storageCargo.getStacksCopy()) {
            SpecialItemData specialItemData = cargoStackAPI.getSpecialDataIfSpecial();
            if (isNull(specialItemData)) {
                continue;
            }

            if (specialItemData.getId().equals(VT_Items.STORAGE_CHEST)) {
                chestsInStorage.add(((AugmentChestData) specialItemData));
                continue;
            }

            if (specialItemData.getId().equals(VT_Items.AUGMENT_ITEM)) {
                CargoAPI cargo = Global.getFactory().createCargo(false);
                cargo.addFromStack(cargoStackAPI);
                augmentCargoWrappers.add(new AugmentCargoWrapper(cargoStackAPI, cargoSource, storageCargo));
            }
        }
    }
}
