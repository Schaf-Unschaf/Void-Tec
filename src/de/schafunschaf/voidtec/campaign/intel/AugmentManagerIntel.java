package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.Settings;
import de.schafunschaf.voidtec.VT_Colors;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.*;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.BaseAugment;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;

import java.awt.*;
import java.util.List;
import java.util.*;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentManagerIntel extends BaseIntelPlugin {
    protected enum ButtonOption {
        INSTALL_VES,
        INSTALL_AUGMENT,
        AUGMENT_SELECTED,
        ADD_SLOTS,
        REMOVE_AUGMENT
    }

    private static CargoStackAPI selectedAugmentInCargo;
    private float titleSize = 0f;
    private float shipListSize = 0f;
    private float shipListWidth = 0f;
    private float cargoListSize = 0f;

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        addTitlePanel(panel, width, height);
        addWelcomeText(panel, width, height);
        addTabs(panel, width, height);
        addShipList(panel, width, height);
        addAugmentsInCargo(panel, width, height);
    }

    private void addTitlePanel(CustomPanelAPI panel, float width, float height) {
        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);
        String title = "VESAI - VoidTec Engineering Suite Augmentation Interface";
        LabelAPI sectionHeading = uiElement.addSectionHeading("", Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        titleSize = sectionHeading.getPosition().getHeight();
        uiElement.addPara("VESAI - VoidTec Engineering Suite Augmentation Interface", 0f, Misc.getHighlightColor(), "VESAI", "V", "E", "S", "A", "I");
        PositionAPI textPosition = uiElement.getPrev().getPosition();
        textPosition.setYAlignOffset(textPosition.getHeight() + 2f);
        textPosition.setXAlignOffset((width / 2) - (uiElement.computeStringWidth(title) / 2));

        panel.addUIElement(uiElement);
    }

    private void addWelcomeText(CustomPanelAPI panel, float width, float height) {

    }

    private void addTabs(CustomPanelAPI panel, float width, float height) {

    }

    private void addShipList(CustomPanelAPI panel, float width, float height) {
        List<FleetMemberAPI> playerShips = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        float shipIconSize = 64f;
        float itemWidth = 228f; // fits 6 slots (64 ship size + 6*24 slots + 5*4 padding)
        float itemPadding = 16f;
        float itemHeight = 104f;
        float slotSize = 24f;
        float slotPadding = 4f;
        float slotSpacing = 6f;
        int numColumns = 4;
        int numRows = 3;
        float panelWidth = numColumns * (itemWidth + itemPadding);
        float panelHeight = numRows * itemHeight;
        int cols = (int) Math.floor(panelWidth / (itemWidth + itemPadding));
        float sumXPadding = 0f;
        float sumYPadding = 0f;

        int shipsDone = 0;
        TooltipMakerAPI uiElement = panel.createUIElement(panelWidth, panelHeight, true);
        CustomPanelAPI shipPanel = panel.createCustomPanel(panelWidth, itemHeight, null);

        for (final FleetMemberAPI ship : playerShips) {
            if (shipsDone >= cols) {
                shipsDone = 0;
                sumXPadding = 0f;
                sumYPadding += itemHeight;
            }
            TooltipMakerAPI shipElement = shipPanel.createUIElement(panelWidth, 0f, false);
            shipElement.getPosition().setXAlignOffset(sumXPadding);
            shipElement.getPosition().setYAlignOffset(-sumYPadding);

            boolean hasVES = true;
            SlotManager slotManager = HullModDataStorage.getInstance().getSlotManager(ship);
            if (isNull(slotManager))
                hasVES = false;

            shipElement.addPara(shipElement.shortenString(ship.getShipName(), itemWidth), Misc.getBasePlayerColor(), 0f);
            UIComponentAPI shipNameComponent = shipElement.getPrev();
            shipElement.addShipList(1, 1, shipIconSize, Misc.getDarkPlayerColor(), Collections.singletonList(ship), 6f);
            UIComponentAPI shipListComponent = shipElement.getPrev();

            sumXPadding += itemWidth + itemPadding;

            if (hasVES)
                shipElement.addPara("VESAI present", Misc.getPositiveHighlightColor(), 3f);
            else
                shipElement.addPara("No VESAI detected", Misc.getNegativeHighlightColor(), 3f);

            UIComponentAPI textComponent = shipElement.getPrev();
            textComponent.getPosition().rightOfTop(shipListComponent, 0f);

            int numSlots = 0;
            if (hasVES) {
                shipElement.addPara("%s/%s slots are in use", 3f, Misc.getHighlightColor(), String.valueOf(slotManager.getUsedSlots()), String.valueOf(slotManager.getMaxSlots()));

                UIComponentAPI lastAugmentButton = null;
                for (final AugmentSlot augmentSlot : slotManager.getShipAugmentSlots()) {
                    final AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();
                    final SlotCategory slotCategory = augmentSlot.getSlotCategory();
                    boolean isSlotEmpty = isNull(slottedAugment);
                    boolean isSelectable = !isNull(selectedAugmentInCargo) && slotManager.isAugmentCompatible(augmentSlot, getAugmentFromStack(selectedAugmentInCargo));
                    Color buttonColor = isSlotEmpty ? slotCategory.getDarkColor() : slotCategory.getColor();

                    Map<ButtonOption, AugmentSlot> buttonData = new HashMap<>();
                    buttonData.put(isSlotEmpty ? ButtonOption.INSTALL_AUGMENT : ButtonOption.REMOVE_AUGMENT, augmentSlot);

                    ButtonAPI augmentButton = shipElement.addButton("", isSlotEmpty && isSelectable ? buttonData : null, Color.BLACK, buttonColor, Alignment.MID, CutStyle.ALL, slotSize, slotSize, 6f);

                    UIComponentAPI augmentButtonComponent = shipElement.getPrev();
                    if (!isNull(lastAugmentButton))
                        augmentButtonComponent.getPosition().rightOfTop(lastAugmentButton, slotPadding);

                    lastAugmentButton = augmentButton;

                    if (isSelectable) {
                        augmentButton.setEnabled(true);
                        augmentButton.highlight();
                    } else if (!isNull(selectedAugmentInCargo))
                        augmentButton.unhighlight();

                    if (!isSlotEmpty) {
                        shipElement.addTooltipToPrevious(new BaseTooltipCreator() {
                            @Override
                            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                                slottedAugment.generateTooltip(ship.getStats(), VoidTecEngineeringSuite.HULL_MOD_ID, tooltip, getTooltipWidth(this), slotCategory, augmentSlot.isPrimary());
                            }
                        }, TooltipMakerAPI.TooltipLocation.BELOW);
                    } else {
                        shipElement.addTooltipToPrevious(new BaseTooltipCreator() {
                            @Override
                            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                                tooltip.addPara("Empty %s slot", 0f, slotCategory.getColor(), slotCategory.toString());
                            }
                        }, TooltipMakerAPI.TooltipLocation.BELOW);
                    }

                    numSlots++;
                }
            } else {
                boolean spEnabled = Settings.vesInstallationWithSP;
                Color hlColor = spEnabled ? Misc.getStoryOptionColor() : Misc.getHighlightColor();
                String highlight = spEnabled ? "1 SP" : Misc.getDGSCredits(100_000);
                shipElement.addPara("Installation cost: %s", 3f, Misc.getGrayColor(), hlColor, highlight);

                Color base = spEnabled ? Misc.getStoryBrightColor() : Misc.getBrightPlayerColor();
                Color bg = spEnabled ? Misc.getStoryDarkColor() : Misc.getDarkPlayerColor();

                Map<ButtonOption, FleetMemberAPI> buttonData = new HashMap<>();
                buttonData.put(ButtonOption.INSTALL_VES, ship);

                boolean canPerformInstallation = canPerformInstallation();
                String buttonText = canPerformInstallation ? "Install VESAI" : "Need Spaceport";
                shipElement.addButton(buttonText, buttonData, base, bg, Alignment.MID, CutStyle.C2_MENU, slotSize * 6 + slotPadding * 5, slotSize, 6f).setEnabled(canPerformInstallation);
                numSlots = 1;
            }

            shipElement.addButton("", null, itemWidth, 0f, 10f);
            UIComponentAPI separatorComponent = shipElement.getPrev();
            separatorComponent.getPosition().setXAlignOffset(-(shipIconSize + ((numSlots - 1) * slotSize) + ((numSlots - 1) * slotPadding)));

            float elementHeight = (6 * slotSize + 5 * slotSpacing) - (numSlots * slotSize + numSlots * slotSpacing);
            shipNameComponent.getPosition().setYAlignOffset(elementHeight);

            shipPanel.addUIElement(shipElement);
            shipsDone++;
        }

        uiElement.addCustom(shipPanel, panelHeight + ((2 - numRows) * itemHeight));
        panel.addUIElement(uiElement).inTL(0f, titleSize + 10f);
        shipListWidth = panelWidth;
    }

    private boolean canPerformInstallation() {
        //Not working!
//        SectorEntityToken interactionTarget = Global.getSector().getPlayerFleet().getInteractionTarget()
//        if (isNull(interactionTarget))
//            return false;
//
//        if (!isNull(interactionTarget.getMarket()) && !interactionTarget.getMarket().hasSpaceport()) {
//            return false;
//        }
//
//        RepLevel level = interactionTarget.getFaction().getRelationshipLevel(Factions.PLAYER);
//
//        return level.isAtWorst(RepLevel.SUSPICIOUS);

        return true;
    }

    private void addAugmentsInCargo(CustomPanelAPI panel, float width, float height) {
        List<CargoAPI> augmentsInCargo = getAugmentsInCargo();
        float panelWidth = width - shipListWidth;
        float panelPadding = titleSize + 10f;
        float buttonWidth = 80f;
        float buttonHeight = 30f;
        float itemSpacing = 10f;
        float itemPadding = 6f;
        float elementHeight = augmentsInCargo.size() * (buttonHeight + itemSpacing);
        float sumYPadding = 0f;

        TooltipMakerAPI uiElement = panel.createUIElement(panelWidth, height - panelPadding, true);
        CustomPanelAPI cargoPanel = panel.createCustomPanel(panelWidth, 0f, null);
        for (CargoAPI cargo : augmentsInCargo) {
            TooltipMakerAPI cargoElement = cargoPanel.createUIElement(width / 2 - 10f, elementHeight - sumYPadding, false);

            CargoStackAPI augmentInCargo = cargo.getStacksCopy().get(0);

            Map<ButtonOption, CargoStackAPI> buttonData = new HashMap<>();
            buttonData.put(ButtonOption.AUGMENT_SELECTED, augmentInCargo);
            ButtonAPI button = cargoElement.addButton("Select", buttonData, Misc.getBrightPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, CutStyle.TL_BR, buttonWidth, buttonHeight, 0f);
            UIComponentAPI buttonComponent = cargoElement.getPrev();

//            button.setEnabled(!(selectedAugment.getUpgradeQuality() == augment.getUpgradeQuality()));

            cargoElement.showCargo(cargo, 1, false, -itemSpacing);
            UIComponentAPI cargoComponent = cargoElement.getPrev();
            cargoComponent.getPosition().rightOfMid(buttonComponent, itemPadding);

            cargoElement.addButton("", null, width / 2, 0f, 0f);
            UIComponentAPI separatorComponent = cargoElement.getPrev();
            separatorComponent.getPosition().belowLeft(buttonComponent, 0f);

            sumYPadding += buttonHeight + itemSpacing;
            cargoPanel.addUIElement(cargoElement);
        }

        uiElement.addCustom(cargoPanel, elementHeight);
        panel.addUIElement(uiElement).inTR(0f, panelPadding);
    }

    private List<CargoAPI> getAugmentsInCargo() {
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        List<CargoAPI> augmentsInCargo = new ArrayList<>();

        for (CargoStackAPI cargoStackAPI : playerCargo.getStacksCopy()) {
            SpecialItemData specialItemData = cargoStackAPI.getSpecialDataIfSpecial();
            if (isNull(specialItemData))
                continue;

            if (specialItemData.getId().equals(VT_Items.AUGMENT_ITEM)) {
                CargoAPI cargo = Global.getFactory().createCargo(false);
                cargo.addFromStack(cargoStackAPI);
                augmentsInCargo.add(cargo);
            }
        }
        return augmentsInCargo;
    }

    @Override
    public Color getTitleColor(ListInfoMode mode) {
        return VT_Colors.VT_COLOR_MAIN;
    }

    @Override
    protected String getName() {
        return "VESAI";
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "vt_vesai_icon");
    }

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        return !((HashMap<?, ?>) buttonId).containsKey(ButtonOption.AUGMENT_SELECTED);
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        if (!(buttonId instanceof HashMap))
            return;

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_VES)) {
            prompt.addPara("Do you want to install the VoidTec Engineering Suite on your ship?", 0f);
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_AUGMENT)) {
            prompt.addPara("Install the augment in this slot?", 0f);
        }
    }

    @Override
    public String getConfirmText(Object buttonId) {
        return "OK";
    }

    @Override
    public String getCancelText(Object buttonId) {
        return "Cancel";
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (!(buttonId instanceof HashMap))
            return;

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_VES)) {
            FleetMemberAPI fleetMember = (FleetMemberAPI) ((HashMap<?, ?>) buttonId).get(ButtonOption.INSTALL_VES);
            fleetMember.getVariant().addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_AUGMENT)) {
            AugmentSlot augmentSlot = (AugmentSlot) ((HashMap<?, ?>) buttonId).get(ButtonOption.INSTALL_AUGMENT);
            boolean success = augmentSlot.installAugment(getAugmentFromStack(selectedAugmentInCargo));
            if (success)
                removeAugmentFromCargo();

            selectedAugmentInCargo = null;
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.AUGMENT_SELECTED)) {
            selectedAugmentInCargo = (CargoStackAPI) ((HashMap<?, ?>) buttonId).get(ButtonOption.AUGMENT_SELECTED);
        }

        Global.getSector().getPlayerFleet().getFleetData().setSyncNeeded();
        Global.getSector().getPlayerFleet().getFleetData().syncMemberLists();
        ui.updateUIForItem(this);
    }

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {
    }

    private BaseAugment getAugmentFromStack(CargoStackAPI cargoStack) {
        return ((AugmentItemData) cargoStack.getData()).getAugment();
    }

    private void removeAugmentFromCargo() {
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        for (CargoStackAPI playerCargoStack : playerCargo.getStacksCopy()) {
            if (playerCargoStack.getData() == selectedAugmentInCargo.getData()) {
                playerCargoStack.setSize(playerCargoStack.getSize() - 1);
                playerCargo.removeEmptyStacks();
            }
        }
    }

    @Override
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    @Override
    public boolean isImportant() {
        return true;
    }

    @Override
    public boolean canTurnImportantOff() {
        return false;
    }

    @Override
    public boolean hasImportantButton() {
        return false;
    }
}
