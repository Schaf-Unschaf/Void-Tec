package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.IntelUIAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import lombok.RequiredArgsConstructor;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class FilterByCategoryButton extends EmptySlotButton {
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
