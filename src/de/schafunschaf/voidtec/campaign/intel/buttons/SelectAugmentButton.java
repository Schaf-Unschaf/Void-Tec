package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class SelectAugmentButton implements IntelButton {
    private final AugmentCargoWrapper augment;
    private final boolean isSelected;

    public SelectAugmentButton(AugmentCargoWrapper augment) {
        this.augment = augment;
        this.isSelected = !isNull(AugmentManagerIntel.selectedAugmentInCargo) && augment.getAugment() == AugmentManagerIntel.selectedAugmentInCargo.getAugment();
    }

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {

    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (isSelected)
            AugmentManagerIntel.selectedAugmentInCargo = null;
        else
            AugmentManagerIntel.selectedAugmentInCargo = augment;
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {

    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getConfirmText() {
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public String getName() {
        return isSelected ? "Selected" : "Select";
    }

    @Override
    public int getShortcut() {
        return 0;
    }
}
