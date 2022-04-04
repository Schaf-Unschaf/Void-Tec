package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class FilledSlotButton extends DefaultButton {

    private final AugmentApplier augment;
    private final FleetMemberAPI fleetMember;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.REPAIR && !augment.isRepairable()) {
            return;
        }

        if (isSelected()) {
            AugmentManagerIntel.setSelectedInstalledAugment(null);
        } else {
            AugmentManagerIntel.setSelectedInstalledAugment(augment);
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        SlotCategory slotCategory = augment.getInstalledSlot().getSlotCategory();
        float scaleFactor = getButtonScaleFactor();
        Color slotColor = Misc.scaleColorOnly(slotCategory.getColor(), scaleFactor);

        return ButtonUtils.addAugmentButton(tooltip, width, slotColor, slotColor, true, augment.isUniqueMod(), this);
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final SlotCategory slotCategory = augment.getInstalledSlot().getSlotCategory();

        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 300f;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                augment.generateTooltip(fleetMember.getStats(), VoidTecEngineeringSuite.HULL_MOD_ID, tooltip, getTooltipWidth(this),
                                        slotCategory, false, false, null);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private float getButtonScaleFactor() {
        SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
        float scaleFactor = 1f;

        boolean filterActive = !isNull(activeCategoryFilter);
        boolean augmentInCargoSelected = !isNull(AugmentManagerIntel.getSelectedAugmentInCargo());
        boolean augmentInSlotSelected = !isNull(AugmentManagerIntel.getSelectedInstalledAugment());
        boolean matchesFilter = activeCategoryFilter == augment.getInstalledSlot().getSlotCategory();
        boolean matchesSelection = AugmentManagerIntel.getSelectedAugment() == augment;

        switch (InfoPanel.getSelectedTab()) {
            case DETAILS:
                if (augmentInSlotSelected) {
                    if (filterActive && matchesFilter) {
                        return isSelected() ? 1f : 0.5f;
                    }
                    return isSelected() ? 1f : 0.1f;
                }
                if (augmentInCargoSelected) {
                    return 0.1f;
                }
                if (filterActive) {
                    return matchesFilter ? 1f : 0.1f;
                }
                break;
            case REPAIR:
                if (augmentInSlotSelected) {
                    if (matchesSelection) {
                        return augment.isRepairable() ? 1f : 0.1f;
                    }
                    return augment.isRepairable() ? 0.3f : 0.1f;
                }
                if (augmentInCargoSelected) {
                    return augment.isRepairable() ? 0.3f : 0.1f;
                }
                return augment.isRepairable() ? 1f : 0.1f;
            case DISMANTLE:
                break;
            case MANUFACTURE:
                break;
        }

        return scaleFactor;
    }

    private boolean isSelected() {
        return augment == AugmentManagerIntel.getSelectedInstalledAugment();
    }
}
