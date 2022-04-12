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
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class FilledSlotButton extends DefaultButton {

    private final AugmentApplier augment;
    private final FleetMemberAPI fleetMember;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (isSelected()) {
            AugmentManagerIntel.setSelectedInstalledAugment(null);
        } else {
            AugmentManagerIntel.setSelectedInstalledAugment(augment);
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
            AugmentManagerIntel.setSelectedSlot(null);
            AugmentManagerIntel.setActiveCategoryFilter(null);
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

        ButtonAPI augmentButton = ButtonUtils.addAugmentButton(tooltip, width, slotColor, slotColor, true, augment.isUniqueMod(), this);

        int damageAmount = augment.getInitialQuality().ordinal() - augment.getAugmentQuality().ordinal();
        if (InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.REPAIR && damageAmount > 0) {
            UIUtils.addIndicatorBars(tooltip, damageAmount, 2, 5, Misc.scaleColorOnly(Misc.getNegativeHighlightColor(), scaleFactor))
                   .getPosition()
                   .leftOfTop(augmentButton, -width + 8f)
                   .setYAlignOffset(-3f);
        }

        return augmentButton;
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
                tooltip.addPara(augment.getName(), augment.getAugmentQuality().getColor(), 0f);
                tooltip.addSpacer(3f);
                augment.generateTooltip(fleetMember.getStats(), VoidTecEngineeringSuite.HULL_MOD_ID, tooltip, getTooltipWidth(this),
                                        slotCategory, null);
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

        if (augmentInSlotSelected) {
            if ((filterActive && matchesFilter) || InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.REPAIR) {
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

        return scaleFactor;
    }

    private boolean isSelected() {
        return augment == AugmentManagerIntel.getSelectedInstalledAugment();
    }
}
