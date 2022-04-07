package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
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
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
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

        if (canInstallAugment) {
            AugmentApplier augmentFromCargo = AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
            AugmentApplier augmentToInstall = augmentFromCargo.isStackable() ? AugmentDataManager.cloneAugment(augmentFromCargo) :
                                              augmentFromCargo;
            boolean success = augmentSlot.installAugment(augmentToInstall);
            if (success) {
                removeAugmentFromCargo();
            }

            AugmentManagerIntel.setSelectedInstalledAugment(success ? augmentToInstall : null);
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
            AugmentManagerIntel.setActiveCategoryFilter(null);
            AugmentManagerIntel.setSelectedSlot(null);
        } else {
            SlotCategory slotCategory = augmentSlot.getSlotCategory();
            SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
            if (!isNull(activeCategoryFilter) && activeCategoryFilter == slotCategory) {
                AugmentManagerIntel.setActiveCategoryFilter(null);
                AugmentManagerIntel.setSelectedSlot(null);
            } else {
                AugmentManagerIntel.setActiveCategoryFilter(slotCategory);
                AugmentManagerIntel.setSelectedSlot(augmentSlot);
            }
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentApplier augment = AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
        String bullet = VT_Strings.BULLET_CHAR + " ";

        tooltip.addPara("Install the augment in this slot (%s)?", 0f, augmentSlot.getSlotCategory().getColor(),
                        augmentSlot.getSlotCategory().getName());
        if (!isNull(augment)) {
            tooltip.addPara(bullet + augment.getName(), augment.getAugmentQuality().getColor(), 10f);
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return canInstallAugment;
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

        return ButtonUtils.addAugmentButton(tooltip, width, slotColor, slotColor, false, false, this);
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
            case DISMANTLE:
            case MANUFACTURE:
                return 0.1f;
        }

        return scaleFactor;
    }

    private void removeAugmentFromCargo() {
        CargoAPI cargo = AugmentManagerIntel.getSelectedAugmentInCargo().getSourceCargo();
        for (CargoStackAPI cargoStackAPI : cargo.getStacksCopy()) {
            if (cargoStackAPI.getData() == AugmentManagerIntel.getSelectedAugmentInCargo().getAugmentCargoStack().getData()) {
                cargoStackAPI.setSize(cargoStackAPI.getSize() - 1);
                cargo.removeEmptyStacks();
                return;
            }
        }
    }
}
