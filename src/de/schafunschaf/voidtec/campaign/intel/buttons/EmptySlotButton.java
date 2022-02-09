package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class EmptySlotButton extends DefaultButton {

    private final AugmentSlot augmentSlot;
    private final boolean canInstallAugment;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (canInstallAugment) {
            boolean success = augmentSlot.installAugment(AugmentManagerIntel.getSelectedAugmentInCargo().getAugment());
            if (success) {
                removeAugmentFromCargo();
            }

            AugmentManagerIntel.setSelectedAugmentInCargo(null);
            AugmentManagerIntel.setActiveCategoryFilter(null);
        } else {
            SlotCategory slotCategory = augmentSlot.getSlotCategory();
            SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
            if (!isNull(activeCategoryFilter) && activeCategoryFilter == slotCategory) {
                AugmentManagerIntel.setActiveCategoryFilter(null);
            } else {
                AugmentManagerIntel.setActiveCategoryFilter(slotCategory);
            }
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentApplier augment = AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
        String bullet = "• ";

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
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        SlotCategory slotCategory = augmentSlot.getSlotCategory();
        Color slotColor = slotCategory.getColor();
        float scaleFactor;

        SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
        boolean isFiltered = activeCategoryFilter == slotCategory;

        if (canInstallAugment) {
            if (isNull(activeCategoryFilter)) {
                scaleFactor = 1f; // Augment selected without filter
            } else {
                scaleFactor = isFiltered ? 1f : 0.7f; // Augment selected with filter. Will highlight prim and sec slots
            }
        } else if (isNull(activeCategoryFilter)) {
            scaleFactor = 0.5f; // No Augment selected and no filter
        } else {
            scaleFactor = isFiltered ? 1f : 0.1f; // No Augment selected but with filter. Will highlight prim and sec slots
        }

        Color buttonColor = Misc.scaleColor(slotColor, scaleFactor);

        ButtonAPI button = ButtonUtils.addAugmentButton(uiElement, height, 0f, buttonColor, buttonColor,
                                                        this);

        addTooltip(uiElement, slotCategory);

        return button;
    }

    @Override
    protected void addTooltip(TooltipMakerAPI uiElement, final SlotCategory slotCategory) {
        final String emptySlotText = String.format("Empty %s slot", slotCategory);
        final String needSpaceportText = "Need Spaceport for modification";
        final float stringWidth = VoidTecUtils.isPlayerDockedAtSpaceport()
                                  ? uiElement.computeStringWidth(emptySlotText)
                                  : uiElement.computeStringWidth(needSpaceportText);

        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return stringWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(emptySlotText, 0f, slotCategory.getColor(), slotCategory.toString());
                if (!VoidTecUtils.isPlayerDockedAtSpaceport()) {
                    tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
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
