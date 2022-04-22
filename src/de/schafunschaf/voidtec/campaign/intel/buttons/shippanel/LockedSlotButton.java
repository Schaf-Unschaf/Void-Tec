package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
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
import de.schafunschaf.voidtec.util.MathUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.ids.VT_Settings.installCostSP;
import static de.schafunschaf.voidtec.ids.VT_Settings.maxNumSlotsForCreditUnlock;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class LockedSlotButton extends DefaultButton {

    private final AugmentSlot augmentSlot;
    private final int unlockedSlots;
    private final float installCost;
    private final boolean canUnlockWithCredits;
    private ButtonAPI button;

    public LockedSlotButton(AugmentSlot augmentSlot, FleetMemberAPI fleetMember) {
        this.augmentSlot = augmentSlot;
        this.unlockedSlots = augmentSlot.getHullModManager().getUnlockedSlots().size();
        this.installCost = MathUtils.roundWholeNumber(fleetMember.getHullSpec().getBaseValue() * 0.1f * unlockedSlots, 2);
        this.canUnlockWithCredits = unlockedSlots < VT_Settings.maxNumSlotsForCreditUnlock;
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (unlockedSlots >= maxNumSlotsForCreditUnlock) {
            Global.getSector().getPlayerStats().addStoryPoints(-installCostSP);
        }

        Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCost);

        augmentSlot.unlockSlot();
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        assert augmentSlot != null;
        assert button != null;
        button.setChecked(true);

        String installCostString = Misc.getDGSCredits(installCost);
        String spCostString = "";
        String spString = "";
        List<Color> hlColors = new ArrayList<>();
        hlColors.add(Misc.getHighlightColor());
        if (unlockedSlots >= maxNumSlotsForCreditUnlock) {
            spCostString = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
            spString = " and " + spCostString;
            hlColors.add(Misc.getStoryOptionColor());
        }

        tooltip.addPara("Do you want to unlock this slot?", 0f);
        tooltip.addPara(String.format("This will cost you %s%s", installCostString, spString), 3f, hlColors.toArray(new Color[0]),
                        installCostString, spCostString);
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
        button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, buttonTextColor, buttonColor, buttonColor, this);
        button.setChecked(true);

        button.setEnabled(canUnlockSlot());

        addTooltip(tooltip);

        return button;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final boolean playerDockedAtSpaceport = VoidTecUtils.isPlayerDockedAtSpaceport();

        final String unlockCostCredits = Misc.getDGSCredits(installCost);
        final String unlockCostSP = String.format("%s SP", VT_Settings.installCostSP);
        final String unlockCostSPString = canUnlockWithCredits ? "" : String.format(" and %s", unlockCostSP);
        final String unlockSlotText = String.format("Locked slot\n" + "Cost to unlock: %s%s", unlockCostCredits, unlockCostSPString);
        final String needSpaceportText = "Need Spaceport for modification";

        final List<Color> hlColor = new ArrayList<>();
        hlColor.add(Misc.getHighlightColor());
        hlColor.add(Misc.getStoryOptionColor());

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
                tooltip.addPara(unlockSlotText, 0f, hlColor.toArray(new Color[0]), unlockCostCredits, unlockCostSP);
                if (!playerDockedAtSpaceport) {
                    tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private boolean canUnlockSlot() {
        if (VoidTecUtils.isPlayerDockedAtSpaceport()) {
            boolean hasCredits = Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCost;
            boolean hasSP = Global.getSector().getPlayerStats().getStoryPoints() >= installCostSP;

            return canUnlockWithCredits ? hasCredits : hasSP && hasCredits;
        }

        return false;
    }
}
