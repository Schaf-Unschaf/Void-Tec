package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.IntelUIAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class SelectAugmentButton extends EmptySlotButton {

    private final AugmentCargoWrapper augment;
    private final boolean isSelected;

    public SelectAugmentButton(AugmentCargoWrapper augment) {
        this.augment = augment;
        this.isSelected = isSelected();
    }

    private boolean isSelected() {
        if (isNull(AugmentManagerIntel.getSelectedAugmentInCargo())) {
            return false;
        }

        return augment.getAugment() == AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (isSelected) {
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
        } else {
            AugmentManagerIntel.setSelectedAugmentInCargo(augment);
        }
    }

    @Override
    public String getName() {
        return isSelected ? "Selected" : "Select";
    }
}
