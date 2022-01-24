package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.*;
import de.schafunschaf.voidtec.helper.ButtonUtils;
import de.schafunschaf.voidtec.helper.VoidTecUtils;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.*;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.schafunschaf.voidtec.Settings.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ShipPanel {
    public static float addShipListPanel(CustomPanelAPI panel, float height, float padding) {
        List<FleetMemberAPI> playerShips = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        float headerHeight = 21f;
        float itemWidth = 228f; // fits 6 slots (64 ship size + 6*24 slots + 5*4 padding)
        float itemPadding = 16f;
        float itemHeight = 104f;
        int numMaxColumns = 4;
        int numMaxRows = 5;
        float panelWidth = numMaxColumns * (itemWidth + itemPadding);
        float panelHeight = numMaxRows * itemHeight;
        int cols = (int) Math.floor(panelWidth / (itemWidth + itemPadding));
        float sumXPadding = 0f;

        int shipsDone = 0;
        TooltipMakerAPI headerElement = panel.createUIElement(panelWidth, height - headerHeight, false);
        headerElement.addSectionHeading("Fleet Overview", Misc.getBrightPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        panel.addUIElement(headerElement).inTL(0f, headerHeight);

        TooltipMakerAPI fleetDisplayElement = panel.createUIElement(panelWidth, panelHeight, true);
        CustomPanelAPI displayRowPanel = panel.createCustomPanel(panelWidth, itemHeight, null);
        List<CustomPanelAPI> panelList = new ArrayList<>();

        for (final FleetMemberAPI ship : playerShips) {
            if (shipsDone >= cols) {
                panelList.add(displayRowPanel);
                displayRowPanel = panel.createCustomPanel(panelWidth, itemHeight, null);
                shipsDone = 0;
                sumXPadding = 0;
            }

            TooltipMakerAPI shipElement = displayRowPanel.createUIElement(itemWidth, itemHeight, false);
            generateShipForPanel(shipElement, ship);
            displayRowPanel.addUIElement(shipElement).inTL(sumXPadding, 0f);
            sumXPadding += itemWidth + itemPadding;
            shipsDone++;
        }

        panelList.add(displayRowPanel);

        for (CustomPanelAPI customPanel : panelList)
            fleetDisplayElement.addCustom(customPanel, 0f);

        panel.addUIElement(fleetDisplayElement).inTL(0f, padding + headerHeight);
        return panelWidth;
    }

    private static void generateShipForPanel(TooltipMakerAPI shipElement, final FleetMemberAPI ship) {
        float shipIconSize = 64f;
        float itemWidth = 228f; // fits 6 slots (64 ship size + 6*24 slots + 5*4 padding)
        float buttonSize = 24f;
        float buttonPadding = 4f;
        float labelButtonWidth = buttonSize * MAX_SLOTS + buttonPadding * (MAX_SLOTS - 1);

        float hullSizeMult = Misc.getSizeNum(ship.getHullSpec().getHullSize());

        boolean hasVESAI = true;
        HullModManager hullmodManager = HullModDataStorage.getInstance().getHullModManager(ship.getId());
        if (isNull(hullmodManager))
            hasVESAI = false;

        shipElement.addPara(shipElement.shortenString(ship.getShipName(), itemWidth), Misc.getBasePlayerColor(), 0f);
        shipElement.addShipList(1, 1, shipIconSize, Misc.getDarkPlayerColor(), Collections.singletonList(ship), 6f);
        UIComponentAPI shipListComponent = shipElement.getPrev();

        if (hasVESAI)
            shipElement.addPara("VESAI present", Misc.getPositiveHighlightColor(), 3f);
        else
            shipElement.addPara("No VESAI detected", Misc.getNegativeHighlightColor(), 3f);

        UIComponentAPI textComponent = shipElement.getPrev();
        textComponent.getPosition().rightOfTop(shipListComponent, 0f);

        if (hasVESAI) {
            AugmentSlot specialSlot = hullmodManager.getSpecialSlot();
            ButtonAPI specialSlotButton = createAugmentSlotButton(shipElement, ship, specialSlot, buttonSize);
            specialSlotButton.getPosition().belowLeft(textComponent, 3f);
            String specialButtonText = specialSlot.isEmpty() ? "Special Slot Empty" : specialSlot.getSlottedAugment().getName();
            Color textColor = specialSlot.isEmpty() ? Misc.getGrayColor() : specialSlot.getSlottedAugment().getAugmentQuality().getColor();
            shipElement.addPara(specialButtonText, textColor, 0f).getPosition().rightOfMid(specialSlotButton, buttonPadding + 4f);

            UIComponentAPI lastCreatedButton = null;

            for (final AugmentSlot augmentSlot : hullmodManager.getSlotsForDisplay()) {
                ButtonAPI augmentSlotButton = createAugmentSlotButton(shipElement, ship, augmentSlot, buttonSize);

                if (isNull(lastCreatedButton))
                    augmentSlotButton.getPosition().belowLeft(specialSlotButton, 3f);
                else
                    augmentSlotButton.getPosition().rightOfTop(lastCreatedButton, buttonPadding);

                lastCreatedButton = augmentSlotButton;
            }

        } else {
            boolean spEnabled = hullmodInstallationWithSP;
            boolean canPerformInstallation = VoidTecUtils.isPlayerDockedAtSpaceport() && VoidTecUtils.canPayForInstallation(hullSizeMult);
            Color hlColor = spEnabled ? Misc.getStoryOptionColor() : Misc.getHighlightColor();
            String highlight = spEnabled ? String.format("%s SP", installCostSP) : Misc.getDGSCredits(installCostCredits * hullSizeMult);
            shipElement.addPara("Installation cost: %s", 3f, Misc.getGrayColor(), hlColor, highlight);

            Color base = spEnabled ? Misc.getStoryBrightColor() : Misc.getBrightPlayerColor();
            Color bg = spEnabled ? Misc.getStoryDarkColor() : Misc.getDarkPlayerColor();

            ButtonUtils.addLabeledButton(shipElement, labelButtonWidth, buttonSize, 6f, base, bg, CutStyle.C2_MENU, new InstallHullmodButton(ship)).setEnabled(canPerformInstallation);
        }

        shipElement.addButton("", null, itemWidth, 0f, 0f);
        UIComponentAPI separatorComponent = shipElement.getPrev();
        separatorComponent.getPosition().belowLeft(shipListComponent, 10f);
    }

    private static ButtonAPI createAugmentSlotButton(final TooltipMakerAPI uiElement, final FleetMemberAPI ship, final AugmentSlot augmentSlot, float buttonSize) {
        HullModManager hullmodManager = augmentSlot.getHullmodManager();
        final AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();
        final SlotCategory slotCategory = augmentSlot.getSlotCategory();
        int unlockedSlots = hullmodManager.getUnlockedSlotsNum();

        boolean isUnlocked = augmentSlot.isUnlocked();
        boolean hasAugmentSelected = !isNull(AugmentManagerIntel.selectedAugmentInCargo);
        final boolean isEmpty = isNull(slottedAugment);
        boolean canUnlockSlot = !isUnlocked;

        IntelButton intelButton = new EmptySlotButton();
        Color buttonColor = new Color(25, 25, 25);
        Color buttonTextColor = Misc.getGrayColor();

        int installCost = installCostCredits * unlockedSlots;
        final boolean canUnlockWithCredits = unlockedSlots < maxNumSlotsForCreditUnlock;

        if (canUnlockWithCredits) {
            if (VoidTecUtils.isPlayerDockedAtSpaceport() && Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCost) {
                buttonTextColor = Misc.getHighlightColor();
                buttonColor = Misc.scaleColor(Misc.getDarkHighlightColor(), 0.3f);
            }
        } else {
            if (VoidTecUtils.isPlayerDockedAtSpaceport() && Global.getSector().getPlayerStats().getStoryPoints() >= installCostSP) {
                buttonTextColor = Misc.getStoryOptionColor();
                buttonColor = Misc.scaleColor(Misc.getStoryDarkColor(), 0.5f);
            }
        }

        if (hasAugmentSelected) {
            if (isEmpty && isUnlocked && hullmodManager.isAugmentCompatible(augmentSlot, AugmentManagerIntel.selectedAugmentInCargo.getAugment())) {
                buttonColor = slotCategory.getColor();
                if (VoidTecUtils.isPlayerDockedAtSpaceport())
                    intelButton = new InstallAugmentButton(augmentSlot);
            } else if (isEmpty && isUnlocked) {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.4f);
            } else if (isUnlocked) {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.2f);
            } else {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.1f);
            }
        } else {
            if (!isUnlocked) {
                uiElement.setButtonFontVictor14();
                if (VoidTecUtils.isPlayerDockedAtSpaceport())
                    intelButton = new LockedSlotButton(augmentSlot);
            } else if (isEmpty) {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.5f);
                intelButton = new FilterByCategoryButton(slotCategory);
            } else {
                buttonColor = slotCategory.getColor();
                if (VoidTecUtils.isPlayerDockedAtSpaceport())
                    intelButton = new FilledSlotButton(augmentSlot);
            }
        }

        ButtonAPI augmentButton = ButtonUtils.addAugmentButton(uiElement, buttonSize, 0f, buttonTextColor, buttonColor, intelButton);

        if (!isEmpty) {
            uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    slottedAugment.generateTooltip(ship.getStats(), VoidTecEngineeringSuite.HULL_MOD_ID, tooltip, getTooltipWidth(this), slotCategory, augmentSlot.isPrimary(), false);
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);

        } else if (canUnlockSlot) {
            final String unlockCost = canUnlockWithCredits ? Misc.getDGSCredits(installCost) : String.format("%s SP", installCostSP);
            final String unlockSlotText = String.format("Locked slot\n" +
                    "Cost to unlock: %s", unlockCost);
            final String needSpaceportText = "Need Spaceport for modification";

            final Color hlColor = canUnlockWithCredits ? Misc.getHighlightColor() : Misc.getStoryOptionColor();

            final float stringWidth = VoidTecUtils.isPlayerDockedAtSpaceport() ? uiElement.computeStringWidth(unlockSlotText) : uiElement.computeStringWidth(needSpaceportText);
            uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return stringWidth;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(unlockSlotText, 0f, hlColor, unlockCost);
                    if (!VoidTecUtils.isPlayerDockedAtSpaceport())
                        tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);

        } else {
            final String emptySlotText = String.format("Empty %s slot", slotCategory);
            final String needSpaceportText = "Need Spaceport for modification";
            final float stringWidth = VoidTecUtils.isPlayerDockedAtSpaceport() ? uiElement.computeStringWidth(emptySlotText) : uiElement.computeStringWidth(needSpaceportText);
            uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return stringWidth;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(emptySlotText, 0f, slotCategory.getColor(), slotCategory.toString());
                    if (!VoidTecUtils.isPlayerDockedAtSpaceport())
                        tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);
        }

        return augmentButton;
    }
}
