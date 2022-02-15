package de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel;

import com.fs.starfarer.api.ui.IntelUIAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import lombok.RequiredArgsConstructor;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class SelectAugmentButton extends DefaultButton {

    private final AugmentCargoWrapper augment;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (isSelected()) {
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
        } else {
            AugmentManagerIntel.setSelectedAugmentInCargo(augment);
        }
    }

    @Override
    public String getName() {
        float numAugments = augment.getAugmentCargoStack().getSize();
        return String.valueOf(isSelected() ? "+" : ((int) numAugments));
    }

    private boolean isSelected() {
        if (isNull(AugmentManagerIntel.getSelectedAugmentInCargo())) {
            return false;
        }

        return augment.getAugment() == AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
    }
}
