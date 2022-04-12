package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.shippanel.*;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.RequiredArgsConstructor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class ShipPanel {

    public static boolean displayWithHullmod = true;
    public static boolean displayWithoutHullmod = true;
    public static List<ShipAPI.HullSize> displayHullSizes = new ArrayList<>(
            Arrays.asList(ShipAPI.HullSize.FRIGATE, ShipAPI.HullSize.DESTROYER, ShipAPI.HullSize.CRUISER, ShipAPI.HullSize.CAPITAL_SHIP));

    private final float panelWidth;
    private final float panelHeight;
    private final float padding;

    private final float panelHeaderHeight = 21f;

    private final float shipElementWidth = 228f;
    private final float shipElementHeight = 104f;
    private final float shipIconSize = 64f;
    private final float shipElementMinPadding = 10f;

    private final float augmentButtonSize = 24f;
    private final float augmentButtonPadding = 4f;

    public void render(CustomPanelAPI mainPanel) {
        TooltipMakerAPI mainPanelUIElement = mainPanel.createUIElement(panelWidth, panelHeight, false);

        addHeader(mainPanel, mainPanelUIElement);
        addFleetOverview(mainPanel, mainPanelUIElement);

        mainPanel.addUIElement(mainPanelUIElement).inTL(0f, 0);
    }

    private void addHeader(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        List<FleetMemberAPI> playerShips = filterShips();
        int currentFleetSize = Global.getSector().getPlayerFleet().getNumShips();
        int hiddenShips = currentFleetSize - playerShips.size();
        int maxShipsInFleet = Global.getSettings().getMaxShipsInFleet();
        String fleetSizeString = String.format("%s/%s Ships", currentFleetSize, maxShipsInFleet);
        String filteredString = String.format(" (%s filtered out)", hiddenShips);

        if (hiddenShips > 0) {
            fleetSizeString += filteredString;
        }

        CustomPanelAPI headerPanel = mainPanel.createCustomPanel(panelWidth, panelHeaderHeight, null);
        TooltipMakerAPI headerElement = headerPanel.createUIElement(panelWidth, panelHeaderHeight, false);
        headerElement.addSectionHeading("", Misc.getBrightPlayerColor(), Misc.getDarkPlayerColor(), Alignment.LMID, 0f);

        float filterWidth = addFilterButtons(headerElement);
        UIComponentAPI lastFilter = headerElement.getPrev();

        Color numColor = Misc.getHighlightColor();
        Color filteredColor = Misc.getNegativeHighlightColor();

        headerElement.setParaFont(Fonts.ORBITRON_12);
        headerElement.setParaFontColor(Misc.getBrightPlayerColor());
        headerElement.addPara(String.format("Fleet Overview - %s", fleetSizeString), 0f, new Color[]{numColor, numColor, filteredColor},
                              String.valueOf(currentFleetSize), String.valueOf(maxShipsInFleet), String.valueOf(hiddenShips))
                     .setAlignment(Alignment.MID);
        UIComponentAPI textElement = headerElement.getPrev();
        textElement.getPosition().setSize(panelWidth - filterWidth, textElement.getPosition().getHeight());
        textElement.getPosition().leftOfTop(lastFilter, 0f);

        headerPanel.addUIElement(headerElement).inTL(0, 0);
        tooltip.addCustom(headerPanel, padding).getPosition().setXAlignOffset(0f);
    }

    private float addFilterButtons(TooltipMakerAPI tooltip) {
        float filterButtonWidth = 40f;
        float hmButtonWidth = 55f;
        float buttonHeight = 18f;
        float buttonSpacing = 5f;

        UIComponentAPI prev = tooltip.getPrev();

        ButtonAPI lastButton = new FilterWithHullmodButton().addButton(tooltip, hmButtonWidth, buttonHeight);
        lastButton.getPosition().rightOfTop(prev, -lastButton.getPosition().getWidth() + buttonSpacing);

        ButtonAPI nextButton = new FilterWithoutHullmodButton().addButton(tooltip, hmButtonWidth, buttonHeight);
        nextButton.getPosition().rightOfTop(lastButton, -nextButton.getPosition().getWidth() - hmButtonWidth - buttonSpacing);
        lastButton = nextButton;

        ShipAPI.HullSize[] hullSizeFilters = {ShipAPI.HullSize.CAPITAL_SHIP, ShipAPI.HullSize.CRUISER,
                                              ShipAPI.HullSize.DESTROYER, ShipAPI.HullSize.FRIGATE};

        boolean isFirst = true;
        for (ShipAPI.HullSize hullSize : hullSizeFilters) {
            float prevButtonWidth = isFirst ? hmButtonWidth : filterButtonWidth;

            nextButton = new FilterHullSizeButton(hullSize).addButton(tooltip, filterButtonWidth,
                                                                      buttonHeight);
            nextButton.getPosition().rightOfTop(lastButton, -nextButton.getPosition().getWidth() - prevButtonWidth - buttonSpacing);
            lastButton = nextButton;
            isFirst = false;
        }

        return filterButtonWidth * 4 + hmButtonWidth * 2 + buttonSpacing * 7;
    }

    private void addFleetOverview(CustomPanelAPI mainPanel, TooltipMakerAPI mainPanelUIElement) {
        CustomPanelAPI fleetOverviewPanel = mainPanel.createCustomPanel(panelWidth, panelHeight - panelHeaderHeight - 2f, null);
        TooltipMakerAPI fleetOverviewElement = fleetOverviewPanel.createUIElement(panelWidth, panelHeight - panelHeaderHeight - 2f, true);

        CustomPanelAPI rowPanel = mainPanel.createCustomPanel(panelWidth, shipElementHeight, null);
        List<CustomPanelAPI> panelList = new ArrayList<>();

        List<FleetMemberAPI> playerShips = filterShips();
        int shipsDone = 0;
        int cols = (int) Math.floor((panelWidth - shipElementMinPadding) / (shipElementWidth + shipElementMinPadding));
        float blockLayoutPadding = (panelWidth - cols * (shipElementWidth + shipElementMinPadding)) / (cols + 1);
        float sumXPadding = (blockLayoutPadding + shipElementMinPadding) / 2;
        float yPadding = 8f;
        for (final FleetMemberAPI ship : playerShips) {
            if (shipsDone >= cols) {
                panelList.add(rowPanel);
                shipsDone = 0;
                sumXPadding = (blockLayoutPadding + shipElementMinPadding) / 2;
                rowPanel = mainPanel.createCustomPanel(panelWidth, shipElementHeight, null);
            }

            TooltipMakerAPI shipElement = rowPanel.createUIElement(shipElementWidth, shipElementHeight, false);

            generateShipForPanel(shipElement, ship);

            rowPanel.addUIElement(shipElement).inTL(sumXPadding, -3f);
            sumXPadding += blockLayoutPadding + shipElementMinPadding + shipElementWidth;
            shipsDone++;
        }

        panelList.add(rowPanel);

        for (CustomPanelAPI customPanel : panelList) {
            fleetOverviewElement.addCustom(customPanel, yPadding).getPosition().setXAlignOffset(0f);
        }
        fleetOverviewElement.addSpacer(yPadding * 2);

        fleetOverviewPanel.addUIElement(fleetOverviewElement);
        mainPanelUIElement.addCustom(fleetOverviewPanel, 0f).getPosition().setXAlignOffset(0f);
    }

    private void generateShipForPanel(TooltipMakerAPI shipElement, final FleetMemberAPI ship) {
        float labelButtonWidth = augmentButtonSize * VT_Settings.MAX_SLOTS + augmentButtonPadding * (VT_Settings.MAX_SLOTS - 1);

        boolean hasVESAI = true;
        boolean isSelected = false;
        HullModManager hullModManager = HullModDataStorage.getInstance().getHullModManager(ship.getId());
        if (isNull(hullModManager)) {
            hasVESAI = false;
        } else {
            for (AugmentSlot augmentSlot : hullModManager.getUnlockedSlots()) {
                AugmentApplier selectedAugment = AugmentManagerIntel.getSelectedInstalledAugment();
                AugmentSlot selectedSlot = AugmentManagerIntel.getSelectedSlot();
                AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();

                boolean matchesInstalledAugment = !isNull(slottedAugment) && slottedAugment == selectedAugment;
                boolean matchesSlot = !isNull(selectedSlot) && augmentSlot == selectedSlot;

                if ((matchesSlot || matchesInstalledAugment)) {
                    isSelected = true;
                    break;
                }
            }

            if (isSelected) {
                UIComponentAPI box = UIUtils.addBox(shipElement, "", null, null, shipElementWidth + 8, shipElementHeight + 2, 1f, 0f,
                                                    null, Misc.getHighlightColor(), Misc.setAlpha(Misc.getHighlightColor(), 15), null);
                box.getPosition().inTL(1f, -2f);
            }
        }

        shipElement.addPara(shipElement.shortenString(ship.getShipName(), shipElementWidth), Misc.getBasePlayerColor(), 0f)
                   .getPosition()
                   .inTL(5f, 0);
        shipElement.addShipList(1, 1, shipIconSize, Misc.getDarkPlayerColor(), Collections.singletonList(ship), 10f);
        UIComponentAPI shipListComponent = shipElement.getPrev();

        if (!hasVESAI) {
            shipElement.addPara("No VESAI detected", Misc.getNegativeHighlightColor(), 3f).getPosition().rightOfTop(shipListComponent, 0f);
        }

        if (hasVESAI) {
            ButtonAPI lastScriptRunnerButton = null;
            for (AugmentSlot augmentSlot : hullModManager.getUniqueSlots()) {
                ButtonAPI specialSlotButton = createAugmentSlotButton(shipElement, augmentSlot, ship);

                if (isNull(lastScriptRunnerButton)) {
                    specialSlotButton.getPosition().rightOfTop(shipListComponent, 0f);
                    specialSlotButton.getPosition().setYAlignOffset(6f);
                } else {
                    specialSlotButton.getPosition().belowLeft(lastScriptRunnerButton, 3f);
                }

                lastScriptRunnerButton = specialSlotButton;

                float maxStringWidth = 5 * augmentButtonSize + 4 * augmentButtonPadding;
                String specialButtonText = augmentSlot.isEmpty()
                                           ? shipElement.shortenString(
                        String.format("Empty %s Slot", augmentSlot.getSlotCategory().getName()), maxStringWidth)
                                           : shipElement.shortenString(augmentSlot.getSlottedAugment().getName(), maxStringWidth);
                Color textColor = augmentSlot.isEmpty()
                                  ? Misc.getGrayColor()
                                  : augmentSlot.getSlottedAugment().getAugmentQuality().getColor();
                shipElement.addPara(specialButtonText, textColor, 0f)
                           .getPosition()
                           .rightOfMid(specialSlotButton, augmentButtonPadding + 4f);
            }

            ButtonAPI lastAugmentButton = null;
            for (final AugmentSlot augmentSlot : hullModManager.getSlotsForDisplay()) {
                ButtonAPI augmentSlotButton = createAugmentSlotButton(shipElement, augmentSlot, ship);

                if (isNull(lastAugmentButton)) {
                    augmentSlotButton.getPosition().belowLeft(lastScriptRunnerButton, 3f);
                } else {
                    augmentSlotButton.getPosition().rightOfTop(lastAugmentButton, augmentButtonPadding);
                }

                lastAugmentButton = augmentSlotButton;
            }
        } else {
            new InstallHullmodButton(ship).addButton(shipElement, labelButtonWidth, augmentButtonSize)
                                          .getPosition()
                                          .setYAlignOffset(-8f);
        }

        if (!isSelected) {
            shipElement.addButton("", null, shipElementWidth, 0f, 0f);
            UIComponentAPI separatorComponent = shipElement.getPrev();
            separatorComponent.getPosition().belowLeft(shipListComponent, 13f);
        }
    }

    private ButtonAPI createAugmentSlotButton(final TooltipMakerAPI uiElement, final AugmentSlot augmentSlot,
                                              FleetMemberAPI ship) {
        AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
        AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();
        HullModManager hullModManager = augmentSlot.getHullModManager();

        boolean isCompatible = false;
        if (!isNull(selectedAugmentInCargo)) {
            isCompatible = hullModManager.isAugmentCompatible(augmentSlot, selectedAugmentInCargo.getAugment());
        }

        IntelButton intelButton;

        if (!augmentSlot.isUnlocked()) {
            intelButton = new LockedSlotButton(augmentSlot);
        } else if (augmentSlot.isEmpty()) {
            intelButton = new EmptySlotButton(augmentSlot, isCompatible);
        } else {
            intelButton = new FilledSlotButton(slottedAugment, ship);
        }

        return intelButton.addButton(uiElement, augmentButtonSize, augmentButtonSize);
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
