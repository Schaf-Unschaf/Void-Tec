package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.*;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Settings;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ShipPanel {

    public static boolean displayWithHullmod = true;
    public static boolean displayWithoutHullmod = true;
    public static List<ShipAPI.HullSize> displayHullSizes = new ArrayList<>(
            Arrays.asList(ShipAPI.HullSize.FRIGATE, ShipAPI.HullSize.DESTROYER, ShipAPI.HullSize.CRUISER, ShipAPI.HullSize.CAPITAL_SHIP));

    private final float panelHeaderHeight = 21f;
    private final int numMaxColumns = 4;
    private final int numMaxRows = 5;
    private final float shipElementPadding = 16f;
    private final float shipElementHeight = 104f;
    @Getter
    private final float panelHeight = numMaxRows * shipElementHeight;
    private final float shipIconSize = 64f;
    private final float augmentButtonSize = 24f;
    private final float augmentButtonPadding = 4f;
    private final float shipElementWidth = calculateShipElementWidth();
    @Getter
    private final float panelWidth = numMaxColumns * (shipElementWidth + shipElementPadding);
    private final int cols = (int) Math.floor(panelWidth / (shipElementWidth + shipElementPadding));

    private float calculateShipElementWidth() {
        // fits 6 slots (64 ship size + 6*24 slots + 5*4 padding)
        return shipIconSize + 6 * augmentButtonSize + 5 * augmentButtonPadding;
    }

    public void displayPanel(CustomPanelAPI panel, float width, float height, float padding) {
        List<FleetMemberAPI> playerShips = filterShips();
        int currentFleetSize = Global.getSector().getPlayerFleet().getNumShips();
        int hiddenShips = currentFleetSize - playerShips.size();
        int maxShipsInFleet = Global.getSettings().getMaxShipsInFleet();
        String fleetSizeString = String.format("%s/%s Ships", currentFleetSize, maxShipsInFleet);
        String filteredString = String.format(" (%s filtered out)", hiddenShips);

        if (hiddenShips > 0) {
            fleetSizeString += filteredString;
        }

        float sumXPadding = 0f;
        float yPadding = 0f;

        int shipsDone = 0;
        TooltipMakerAPI headerElement = panel.createUIElement(panelWidth, height - panelHeaderHeight, false);
        headerElement.addSectionHeading(String.format("Fleet Overview - %s", fleetSizeString), Misc.getBrightPlayerColor(),
                                        Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        addFilterButtons(headerElement);
        panel.addUIElement(headerElement).inTL(0f, panelHeaderHeight);

        TooltipMakerAPI fleetDisplayElement = panel.createUIElement(panelWidth, panelHeight, true);
        CustomPanelAPI displayRowPanel = panel.createCustomPanel(panelWidth, shipElementHeight, null);
        List<CustomPanelAPI> panelList = new ArrayList<>();

        for (final FleetMemberAPI ship : playerShips) {
            if (shipsDone >= cols) {
                panelList.add(displayRowPanel);
                displayRowPanel = panel.createCustomPanel(panelWidth, shipElementHeight, null);
                shipsDone = 0;
                sumXPadding = 0;
                yPadding = 6f;
            }

            TooltipMakerAPI shipElement = displayRowPanel.createUIElement(shipElementWidth, shipElementHeight, false);
            generateShipForPanel(shipElement, ship);
            displayRowPanel.addUIElement(shipElement).inTL(sumXPadding, yPadding);
            sumXPadding += shipElementWidth + shipElementPadding;
            shipsDone++;
        }

        panelList.add(displayRowPanel);

        for (CustomPanelAPI customPanel : panelList) {
            fleetDisplayElement.addCustom(customPanel, 0f);
        }

        panel.addUIElement(fleetDisplayElement).inTL(0f, padding + panelHeaderHeight);

    }

    private void addFilterButtons(TooltipMakerAPI tooltip) {
        float filterButtonWidth = 40f;
        float hmButtonWidth = 55f;
        float buttonHeight = 18f;
        float buttonSpacing = 5f;

        UIComponentAPI prev = tooltip.getPrev();

        ButtonAPI lastButton = new FilterWithHullmodButton().createButton(tooltip, hmButtonWidth, buttonHeight);
        lastButton.getPosition().rightOfTop(prev, -lastButton.getPosition().getWidth() + buttonSpacing);

        ButtonAPI nextButton = new FilterWithoutHullmodButton().createButton(tooltip, hmButtonWidth, buttonHeight);
        nextButton.getPosition().rightOfTop(lastButton, -nextButton.getPosition().getWidth() - hmButtonWidth - buttonSpacing);
        lastButton = nextButton;

        ShipAPI.HullSize[] hullSizeFilters = {ShipAPI.HullSize.CAPITAL_SHIP, ShipAPI.HullSize.CRUISER,
                                              ShipAPI.HullSize.DESTROYER, ShipAPI.HullSize.FRIGATE};

        boolean isFirst = true;
        for (ShipAPI.HullSize hullSize : hullSizeFilters) {
            float prevButtonWidth = isFirst ? hmButtonWidth : filterButtonWidth;

            nextButton = new FilterHullSizeButton(hullSize).createButton(tooltip, filterButtonWidth,
                                                                         buttonHeight);
            nextButton.getPosition().rightOfTop(lastButton, -nextButton.getPosition().getWidth() - prevButtonWidth - buttonSpacing);
            lastButton = nextButton;
            isFirst = false;
        }
    }

    private void generateShipForPanel(TooltipMakerAPI shipElement, final FleetMemberAPI ship) {
        float labelButtonWidth = augmentButtonSize * VT_Settings.MAX_SLOTS + augmentButtonPadding * (VT_Settings.MAX_SLOTS - 1);

        boolean hasVESAI = true;
        HullModManager hullmodManager = HullModDataStorage.getInstance().getHullModManager(ship.getId());
        if (isNull(hullmodManager)) {
            hasVESAI = false;
        }

        shipElement.addPara(shipElement.shortenString(ship.getShipName(), shipElementWidth), Misc.getBasePlayerColor(), 0f);
        shipElement.addShipList(1, 1, shipIconSize, Misc.getDarkPlayerColor(), Collections.singletonList(ship), 10f);
        UIComponentAPI shipListComponent = shipElement.getPrev();

        if (!hasVESAI) {
            shipElement.addPara("No VESAI detected", Misc.getNegativeHighlightColor(), 3f).getPosition().rightOfTop(shipListComponent, 0f);
        }

        if (hasVESAI) {
            ButtonAPI lastScriptRunnerButton = null;
            for (AugmentSlot scriptRunnerSlot : hullmodManager.getUniqueSlots()) {
                ButtonAPI specialSlotButton = createAugmentSlotButton(shipElement, scriptRunnerSlot, ship);

                if (isNull(lastScriptRunnerButton)) {
                    specialSlotButton.getPosition().rightOfTop(shipListComponent, 0f);
                    specialSlotButton.getPosition().setYAlignOffset(6f);
                } else {
                    specialSlotButton.getPosition().belowLeft(lastScriptRunnerButton, 3f);
                }

                lastScriptRunnerButton = specialSlotButton;

                float maxStringWidth = 5 * augmentButtonSize + 4 * augmentButtonPadding;
                String specialButtonText = scriptRunnerSlot.isEmpty()
                                           ? shipElement.shortenString(
                        String.format("%s Slot Empty", scriptRunnerSlot.getSlotCategory().getName()), maxStringWidth)
                                           : shipElement.shortenString(scriptRunnerSlot.getSlottedAugment().getName(), maxStringWidth);
                Color textColor = scriptRunnerSlot.isEmpty()
                                  ? Misc.getGrayColor()
                                  : scriptRunnerSlot.getSlottedAugment().getAugmentQuality().getColor();
                shipElement.addPara(specialButtonText, textColor, 0f)
                           .getPosition()
                           .rightOfMid(specialSlotButton, augmentButtonPadding + 4f);
            }

            ButtonAPI lastAugmentButton = null;
            for (final AugmentSlot augmentSlot : hullmodManager.getSlotsForDisplay()) {
                ButtonAPI augmentSlotButton = createAugmentSlotButton(shipElement, augmentSlot, ship);

                if (isNull(lastAugmentButton)) {
                    augmentSlotButton.getPosition().belowLeft(lastScriptRunnerButton, 3f);
                } else {
                    augmentSlotButton.getPosition().rightOfTop(lastAugmentButton, augmentButtonPadding);
                }

                lastAugmentButton = augmentSlotButton;
            }

        } else {
            new InstallHullmodButton(ship).createButton(shipElement, labelButtonWidth, augmentButtonSize)
                                          .getPosition()
                                          .setYAlignOffset(-8f);
        }

        shipElement.addButton("", null, shipElementWidth, 0f, 0f);
        UIComponentAPI separatorComponent = shipElement.getPrev();
        separatorComponent.getPosition().belowLeft(shipListComponent, 13f);
    }

    private ButtonAPI createAugmentSlotButton(final TooltipMakerAPI uiElement, final AugmentSlot augmentSlot,
                                              FleetMemberAPI ship) {
        AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
        AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();
        HullModManager hullmodManager = augmentSlot.getHullmodManager();

        boolean isCompatible = false;
        if (!isNull(selectedAugmentInCargo)) {
            isCompatible = hullmodManager.isAugmentCompatible(augmentSlot, selectedAugmentInCargo.getAugment());
        }

        IntelButton intelButton;

        if (!augmentSlot.isUnlocked()) {
            intelButton = new LockedSlotButton(augmentSlot);
        } else if (augmentSlot.isEmpty()) {
            intelButton = new EmptySlotButton(augmentSlot, isCompatible);
        } else {
            intelButton = new FilledSlotButton(slottedAugment, ship);
        }

        return intelButton.createButton(uiElement, augmentButtonSize, augmentButtonSize);
    }

    private List<FleetMemberAPI> filterShips() {
        List<FleetMemberAPI> playerShips = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        List<FleetMemberAPI> filteredList = new ArrayList<>();

        for (FleetMemberAPI fleetMember : playerShips) {
            if (displayHullSizes.contains(fleetMember.getHullSpec().getHullSize())) {
                boolean hasHullmod = fleetMember.getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID);
                if (displayWithHullmod && hasHullmod || displayWithoutHullmod && !hasHullmod) {
                    filteredList.add(fleetMember);
                }
            }
        }

        return filteredList;
    }
}
