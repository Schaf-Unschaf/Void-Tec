package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.ids.VT_Settings.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class LockedSlotButton extends DefaultButton {

    private final AugmentSlot augmentSlot;
    private final int unlockedSlots;
    private final boolean canUnlockWithCredits;

    public LockedSlotButton(AugmentSlot augmentSlot) {
        this.augmentSlot = augmentSlot;
        this.unlockedSlots = augmentSlot.getHullmodManager().getUnlockedSlots().size();
        this.canUnlockWithCredits = unlockedSlots < VT_Settings.maxNumSlotsForCreditUnlock;
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (unlockedSlots >= maxNumSlotsForCreditUnlock) {
            Global.getSector().getPlayerStats().addStoryPoints(-installCostSP);
        } else {
            int installCost = installCostCredits * unlockedSlots;
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCost);
        }

        augmentSlot.unlockSlot();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        assert augmentSlot != null;
        int installCost = installCostCredits * unlockedSlots;

        String installCostString = Misc.getDGSCredits(installCost);
        Color hlColor = Misc.getHighlightColor();
        if (unlockedSlots >= maxNumSlotsForCreditUnlock) {
            installCostString = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
            hlColor = Misc.getStoryOptionColor();
        }

        tooltip.addPara("Do you want to unlock this slot?", 0f);
        tooltip.addPara(String.format("This will cost you %s", installCostString), 3f, hlColor, installCostString);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return canUnlockSlot();
    }

    @Override
    public String getConfirmText() {
        return "Unlock";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        return "+";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color buttonTextColor = Misc.getHighlightColor();
        Color buttonColor = Misc.scaleColor(Misc.getDarkHighlightColor(), 0.7f);

        if (!canUnlockWithCredits) {
            buttonTextColor = Misc.getStoryOptionColor();
            buttonColor = Misc.getStoryDarkColor();
        }

        AugmentCargoWrapper selectedAugment = AugmentManagerIntel.getSelectedAugmentInCargo();
        SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();

        if (!isNull(selectedAugment) || !isNull(activeCategoryFilter)) {
            buttonTextColor.darker();
            buttonColor.darker();
        }

        tooltip.setButtonFontVictor14();
        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, buttonTextColor, buttonColor, buttonColor, this);
        button.setChecked(true);

        button.setEnabled(canUnlockSlot());

        addTooltip(tooltip);

        return button;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final boolean playerDockedAtSpaceport = VoidTecUtils.isPlayerDockedAtSpaceport();
        boolean canUnlockWithCredits = unlockedSlots < VT_Settings.maxNumSlotsForCreditUnlock;
        int installCost = VT_Settings.installCostCredits * unlockedSlots;

        final String unlockCost = canUnlockWithCredits
                                  ? Misc.getDGSCredits(installCost)
                                  : String.format("%s SP", VT_Settings.installCostSP);
        final String unlockSlotText = String.format("Locked slot\n" + "Cost to unlock: %s", unlockCost);
        final String needSpaceportText = "Need Spaceport for modification";

        final Color hlColor = canUnlockWithCredits ? Misc.getHighlightColor() : Misc.getStoryOptionColor();

        final float stringWidth = playerDockedAtSpaceport
                                  ? tooltip.computeStringWidth(unlockSlotText)
                                  : tooltip.computeStringWidth(needSpaceportText);
        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return stringWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(unlockSlotText, 0f, hlColor, unlockCost);
                if (!playerDockedAtSpaceport) {
                    tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private boolean canUnlockSlot() {
        boolean canUnlockWithCredits = unlockedSlots < VT_Settings.maxNumSlotsForCreditUnlock;

        if (VoidTecUtils.isPlayerDockedAtSpaceport()) {
            if (canUnlockWithCredits) {
                int installCost = VT_Settings.installCostCredits * unlockedSlots;
                return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCost;
            }

            return Global.getSector().getPlayerStats().getStoryPoints() >= VT_Settings.installCostSP;
        }

        return false;
    }
}
