package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentSlot;
import de.schafunschaf.voidtec.util.FormattingTools;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.Settings.*;

@RequiredArgsConstructor
public class LockedSlotButton extends EmptySlotButton {
    private final AugmentSlot augmentSlot;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        int unlockedSlots = augmentSlot.getHullmodManager().getUnlockedSlotsNum();
        int installCost = installCostCredits * unlockedSlots;

        if (unlockedSlots <= maxNumSlotsForCreditUnlock) {
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCost);
        } else {
            Global.getSector().getPlayerStats().addStoryPoints(-installCostSP);
        }

        augmentSlot.unlockSlot();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        int unlockedSlots = augmentSlot.getHullmodManager().getUnlockedSlotsNum();
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
        return true;
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
}
