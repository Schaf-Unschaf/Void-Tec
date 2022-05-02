package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class EmptySlotButton extends DefaultButton {

    private final AugmentSlot augmentSlot;
    private final boolean canInstallAugment;
    private final String needSpaceportText = VoidTecUtils.isPlayerDockedAtSpaceport() ? "" : "\n\nNeed Spaceport for modification";

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (InfoPanel.getSelectedTab() != InfoPanel.InfoTabs.DETAILS) {
            return;
        }

        if (canInstallAugment && VoidTecUtils.isPlayerDockedAtSpaceport()) {
            AugmentApplier augmentFromCargo = AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
            AugmentApplier augmentToInstall = augmentFromCargo.isStackable() ? AugmentDataManager.cloneAugment(augmentFromCargo) :
                                              augmentFromCargo;
            boolean success = augmentSlot.installAugment(augmentToInstall);
            if (success) {
                CargoUtils.removeAugmentFromCargo(AugmentManagerIntel.getSelectedAugmentInCargo());
            }

            AugmentManagerIntel.setSelectedInstalledAugment(success ? augmentToInstall : null);
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
            AugmentManagerIntel.setActiveCategoryFilter(null);
            AugmentManagerIntel.setSelectedSlot(null);
        } else {
            SlotCategory slotCategory = augmentSlot.getSlotCategory();
            if (AugmentManagerIntel.getSelectedSlot() == augmentSlot) {
                AugmentManagerIntel.setActiveCategoryFilter(null);
                AugmentManagerIntel.setSelectedSlot(null);
            } else {
                AugmentManagerIntel.setActiveCategoryFilter(slotCategory);
                AugmentManagerIntel.setSelectedSlot(augmentSlot);
                AugmentManagerIntel.setSelectedInstalledAugment(null);
            }
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();

        if (isNull(selectedAugmentInCargo)) {
            tooltip.addPara("No Augment selected", 0f);
            return;
        }

        AugmentApplier augment = selectedAugmentInCargo.getAugment();
        String bullet = VT_Strings.BULLET_CHAR + " ";

        tooltip.addPara("Install the augment in this slot (%s)?", 0f, augmentSlot.getSlotCategory().getColor(),
                        augmentSlot.getSlotCategory().getName());
        if (!isNull(augment)) {
            tooltip.addPara(bullet + augment.getName(), augment.getAugmentQuality().getColor(), 10f);
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.DETAILS && canInstallAugment && VoidTecUtils.isPlayerDockedAtSpaceport();
    }

    @Override
    public String getConfirmText() {
        return "Install";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        SlotCategory slotCategory = augmentSlot.getSlotCategory();
        float scaleFactor = getButtonScaleFactor();
        Color slotColor = Misc.scaleColor(slotCategory.getColor(), scaleFactor);

        ButtonAPI emptySlotButton = ButtonUtils.addAugmentButton(tooltip, width, slotColor, slotColor, false, false, this);
        if (AugmentManagerIntel.getSelectedSlot() == augmentSlot) {
            UIUtils.addBox(tooltip, "", null, null, width + 4, height + 4, 1, 0, null, augmentSlot.getSlotCategory().getColor(),
                           new Color(0, 0, 0, 0), null)
                   .getPosition().rightOfTop(emptySlotButton, -(width + 2)).setYAlignOffset(2);
        }

        return emptySlotButton;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final SlotCategory slotCategory = augmentSlot.getSlotCategory();
        String emptySlotText = String.format("Empty %s slot", slotCategory);
        final String tooltipText = emptySlotText + needSpaceportText;
        final float stringWidth = tooltip.computeStringWidth(tooltipText);

        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return stringWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f, new Color[]{slotCategory.getColor(), Misc.getGrayColor()},
                                slotCategory.toString(), needSpaceportText);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private float getButtonScaleFactor() {
        SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
        SlotCategory slotCategory = augmentSlot.getSlotCategory();
        float scaleFactor = 1f;

        boolean filterActive = !isNull(activeCategoryFilter);
        boolean augmentInCargoSelected = !isNull(AugmentManagerIntel.getSelectedAugmentInCargo());
        boolean augmentInSlotSelected = !isNull(AugmentManagerIntel.getSelectedInstalledAugment());
        boolean matchesFilter = activeCategoryFilter == slotCategory;

        switch (InfoPanel.getSelectedTab()) {
            case DETAILS:
                if (augmentInCargoSelected) {
                    return canInstallAugment ? 1f : 0.1f;
                }
                if (filterActive) {
                    return matchesFilter ? 1f : 0.1f;
                }
                if (augmentInSlotSelected) {
                    return 0.3f;
                }
                break;
            case REPAIR:
            case MANUFACTURE:
                return 0.1f;
        }

        return scaleFactor;
    }
}
