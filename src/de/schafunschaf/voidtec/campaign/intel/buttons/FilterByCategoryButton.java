package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import lombok.RequiredArgsConstructor;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class FilterByCategoryButton implements IntelButton {
    private final SlotCategory selectedSlot;

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {

    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (!isNull(AugmentManagerIntel.activeCategoryFilter) && AugmentManagerIntel.activeCategoryFilter == selectedSlot)
            AugmentManagerIntel.activeCategoryFilter = null;
        else
            AugmentManagerIntel.activeCategoryFilter = selectedSlot;
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
        return null;
    }

    @Override
    public int getShortcut() {
        return 0;
    }
}
