package de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel;

import com.fs.starfarer.api.ui.IntelUIAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import lombok.RequiredArgsConstructor;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class FilterByCategoryButton extends DefaultButton {

    private final SlotCategory selectedSlot;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (!isNull(AugmentManagerIntel.getActiveCategoryFilter()) && AugmentManagerIntel.getActiveCategoryFilter() == selectedSlot) {
            AugmentManagerIntel.setActiveCategoryFilter(null);
        } else {
            AugmentManagerIntel.setActiveCategoryFilter(selectedSlot);
        }
    }
}
