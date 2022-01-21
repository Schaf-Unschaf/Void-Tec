package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.VT_Colors;
import de.schafunschaf.voidtec.VT_Icons;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestData;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.*;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.util.FormattingTools;

import java.awt.*;
import java.util.List;
import java.util.*;

import static de.schafunschaf.voidtec.Settings.*;
import static de.schafunschaf.voidtec.helper.AugmentCargoWrapper.CargoSource;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

public class AugmentManagerIntel extends BaseIntelPlugin {
    protected enum ButtonOption {
        INSTALL_VESAI,
        INSTALL_AUGMENT,
        AUGMENT_SELECTED,
        UNLOCK_SLOT,
        REMOVE_AUGMENT,
        REPAIR_AUGMENT,
        SORT_BY_SLOT
    }

    public static final String STACK_SOURCE = "augmentManagerIntel";

    private static AugmentCargoWrapper selectedAugmentInCargo;
    private static SlotCategory activeCategoryFilter;
    private float titleSize = 0f;
    private float shipListSize = 0f;
    private float shipListWidth = 0f;
    private float cargoListSize = 0f;

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        addTitlePanel(panel, width, height);
        addWelcomeText(panel, width, height);
        addTabs(panel, width, height);
        addShipListPanel(panel, width, height);
        addAugmentsInCargoPanel(panel, width, height);
    }

    private void addTitlePanel(CustomPanelAPI panel, float width, float height) {
        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);
        LabelAPI sectionHeading = uiElement.addSectionHeading("", Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        titleSize = sectionHeading.getPosition().getHeight();
        uiElement.addPara("VESAI - VoidTec Engineering Suite Augmentation Interface", 0f, Misc.getBrightPlayerColor(), Misc.getHighlightColor(), "VESAI", "V", "E", "S", "A", "I").setAlignment(Alignment.MID);
        PositionAPI textPosition = uiElement.getPrev().getPosition();
        textPosition.setYAlignOffset(textPosition.getHeight() + 2f);

        panel.addUIElement(uiElement);
    }

    private void addWelcomeText(CustomPanelAPI panel, float width, float height) {

    }

    private void addTabs(CustomPanelAPI panel, float width, float height) {

    }

    private void addShipListPanel(CustomPanelAPI panel, float width, float height) {
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

        panel.addUIElement(fleetDisplayElement).inTL(0f, titleSize + headerHeight + 10f);
        shipListWidth = panelWidth;
    }

    private void generateShipForPanel(TooltipMakerAPI shipElement, final FleetMemberAPI ship) {
        float shipIconSize = 64f;
        float itemWidth = 228f; // fits 6 slots (64 ship size + 6*24 slots + 5*4 padding)
        float buttonSize = 24f;
        float buttonPadding = 4f;

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
            Color hlColor = spEnabled ? Misc.getStoryOptionColor() : Misc.getHighlightColor();
            String highlight = spEnabled ? String.format("%s SP", installCostSP) : Misc.getDGSCredits(installCostCredits * hullSizeMult);
            shipElement.addPara("Installation cost: %s", 3f, Misc.getGrayColor(), hlColor, highlight);

            Color base = spEnabled ? Misc.getStoryBrightColor() : Misc.getBrightPlayerColor();
            Color bg = spEnabled ? Misc.getStoryDarkColor() : Misc.getDarkPlayerColor();

            Map<ButtonOption, FleetMemberAPI> buttonData = new HashMap<>();
            buttonData.put(ButtonOption.INSTALL_VESAI, ship);

            boolean canPerformInstallation = isDockedAtSpaceport() && canPayForInstallation(hullSizeMult);
            String buttonText;
            if (isDockedAtSpaceport())
                if (canPayForInstallation(hullSizeMult))
                    buttonText = "Install VESAI";
                else
                    buttonText = "Not enough " + (hullmodInstallationWithSP ? "SP" : "credits");
            else
                buttonText = "Need Spaceport";
            shipElement.addButton(buttonText, buttonData, base, bg, Alignment.MID, CutStyle.C2_MENU, buttonSize * MAX_SLOTS + buttonPadding * (MAX_SLOTS - 1), buttonSize, 6f).setEnabled(canPerformInstallation);
        }

        shipElement.addButton("", null, itemWidth, 0f, 0f);
        UIComponentAPI separatorComponent = shipElement.getPrev();
        separatorComponent.getPosition().belowLeft(shipListComponent, 10f);
    }

    private ButtonAPI createAugmentSlotButton(final TooltipMakerAPI uiElement, final FleetMemberAPI ship, final AugmentSlot augmentSlot, float buttonSize) {
        HullModManager hullmodManager = augmentSlot.getHullmodManager();
        final AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();
        final SlotCategory slotCategory = augmentSlot.getSlotCategory();
        int unlockedSlots = hullmodManager.getUnlockedSlotsNum();

        boolean isUnlocked = augmentSlot.isUnlocked();
        boolean hasAugmentSelected = !isNull(selectedAugmentInCargo);
        final boolean isEmpty = isNull(slottedAugment);
        boolean canUnlockSlot = !isUnlocked;

        Map<ButtonOption, AugmentSlot> buttonData = new HashMap<>();
        Color buttonColor = new Color(25, 25, 25);
        Color buttonTextColor = Misc.getGrayColor();
        String buttonText = "";

        int installCost = installCostCredits * unlockedSlots;
        final boolean canUnlockWithCredits = unlockedSlots < maxNumSlotsForCreditUnlock;

        if (canUnlockWithCredits) {
            if (isDockedAtSpaceport() && Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCost) {
                buttonTextColor = Misc.getHighlightColor();
                buttonColor = Misc.scaleColor(Misc.getDarkHighlightColor(), 0.3f);
            }
        } else {
            if (isDockedAtSpaceport() && Global.getSector().getPlayerStats().getStoryPoints() >= installCostSP) {
                buttonTextColor = Misc.getStoryOptionColor();
                buttonColor = Misc.scaleColor(Misc.getStoryDarkColor(), 0.5f);
            }
        }

        if (hasAugmentSelected) {
            if (isEmpty && isUnlocked && hullmodManager.isAugmentCompatible(augmentSlot, selectedAugmentInCargo.getAugment())) {
                buttonColor = slotCategory.getColor();
                if (isDockedAtSpaceport())
                    buttonData.put(ButtonOption.INSTALL_AUGMENT, augmentSlot);
            } else if (isEmpty && isUnlocked) {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.4f);
                buttonData = null;
            } else if (isUnlocked) {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.2f);
                buttonData = null;
            } else {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.1f);
                buttonData = null;
            }
        } else {
            if (!isUnlocked) {
                uiElement.setButtonFontVictor14();
                buttonText = "+";
                if (isDockedAtSpaceport())
                    buttonData.put(ButtonOption.UNLOCK_SLOT, augmentSlot);
            } else if (isEmpty) {
                buttonColor = Misc.scaleColor(slotCategory.getColor(), 0.5f);
                buttonData.put(ButtonOption.SORT_BY_SLOT, augmentSlot);
            } else {
                buttonColor = slotCategory.getColor();
                if (isDockedAtSpaceport())
                    buttonData.put(ButtonOption.REMOVE_AUGMENT, augmentSlot);
            }
        }

        ButtonAPI augmentButton = uiElement.addButton(buttonText, buttonData, buttonTextColor, buttonColor, Alignment.MID, CutStyle.ALL, buttonSize, buttonSize, 0f);

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

            if (isDockedAtSpaceport()) {
                buttonData = new HashMap<>();
                buttonData.put(ButtonOption.UNLOCK_SLOT, augmentSlot);
            }

            final float stringWidth = isDockedAtSpaceport() ? uiElement.computeStringWidth(unlockSlotText) : uiElement.computeStringWidth(needSpaceportText);
            uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return stringWidth;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(unlockSlotText, 0f, hlColor, unlockCost);
                    if (!isDockedAtSpaceport())
                        tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);

        } else {
            final String emptySlotText = String.format("Empty %s slot", slotCategory);
            final String needSpaceportText = "Need Spaceport for modification";
            final float stringWidth = isDockedAtSpaceport() ? uiElement.computeStringWidth(emptySlotText) : uiElement.computeStringWidth(needSpaceportText);
            uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return stringWidth;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(emptySlotText, 0f, slotCategory.getColor(), slotCategory.toString());
                    if (!isDockedAtSpaceport())
                        tooltip.addPara(needSpaceportText, Misc.getGrayColor(), 3f);
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);
        }

        return augmentButton;
    }

    private boolean canPayForInstallation(float hullSizeMult) {
        if (hullmodInstallationWithSP)
            return Global.getSector().getPlayerStats().getStoryPoints() >= installCostSP;

        return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCostCredits * hullSizeMult;
    }

    private boolean isDockedAtSpaceport() {
        return Global.getSector().hasTransientScript(VT_DockedAtSpaceportHelper.class);
    }

    private void addAugmentsInCargoPanel(CustomPanelAPI panel, float width, float height) {
        List<AugmentCargoWrapper> augmentsInCargo = getAugmentsInCargo();
        final float panelWidth = width - shipListWidth;
        float headerHeight = 21f;
        float sorterHeight = 68f;
        float panelPadding = titleSize + 10f;
        float buttonHeight = 30f;
        float itemSpacing = 3f;

        TooltipMakerAPI headerElement = panel.createUIElement(panelWidth - 3f, 0f, false);
        headerElement.addSectionHeading("Available Augments", Misc.getBrightPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        panel.addUIElement(headerElement).inTR(0f, headerHeight);

        final TooltipMakerAPI sorterElement = panel.createUIElement(panelWidth - 3f, 0f, false);
        String selectedSlot = isNull(activeCategoryFilter) ? "ALL" : activeCategoryFilter.name();
        Color selectedColor = isNull(activeCategoryFilter) ? Color.WHITE : activeCategoryFilter.getColor();
        sorterElement.setParaFont(Fonts.ORBITRON_20AA);

        sorterElement.addPara("Filter:", Misc.getBasePlayerColor(), 10f);
        UIComponentAPI sortingTextComp = sorterElement.getPrev();

        ButtonAPI lastButton = null;
        for (final SlotCategory slotCategory : SlotCategory.values) {
            Color buttonColor = isNull(activeCategoryFilter) || activeCategoryFilter == slotCategory ? slotCategory.getColor() : Misc.scaleColorOnly(slotCategory.getColor(), 0.3f);
            final String tooltipText = String.format("Display only %s slots", slotCategory.name());
            final float tooltipWidth = headerElement.computeStringWidth(tooltipText);

            HashMap<ButtonOption, SlotCategory> buttonData = new HashMap<>();
            buttonData.put(ButtonOption.SORT_BY_SLOT, slotCategory);

            ButtonAPI augmentButton = sorterElement.addButton("", buttonData, slotCategory.getColor(), buttonColor, Alignment.MID, CutStyle.ALL, 20, 20, 0f);
            if (!isNull(lastButton))
                augmentButton.getPosition().rightOfMid(lastButton, 4f);
            else
                augmentButton.getPosition().leftOfMid(sortingTextComp, -74f);

            sorterElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return tooltipWidth;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(tooltipText, 0f, slotCategory.getColor(), slotCategory.name());
                }
            }, TooltipMakerAPI.TooltipLocation.ABOVE);

            lastButton = augmentButton;
        }

        sorterElement.addPara("%s", 6f, Misc.getGrayColor(), selectedColor, selectedSlot).setAlignment(Alignment.MID);
        sorterElement.getPrev().getPosition().belowLeft(sortingTextComp, 6f);
        sorterElement.addButton("", null, panelWidth - 8f, 0f, 6f);
        panel.addUIElement(sorterElement).belowLeft(headerElement, 0f);

        TooltipMakerAPI uiElement = panel.createUIElement(panelWidth, height - titleSize - headerHeight - sorterHeight, true);
        List<CustomPanelAPI> panelList = new ArrayList<>();

        for (AugmentCargoWrapper augmentCargoWrapper : augmentsInCargo) {
            final AugmentApplier augmentInStack = augmentCargoWrapper.getAugment();
            if (!(isNull(activeCategoryFilter) || augmentValidForDisplay(augmentInStack)))
                continue;

            CustomPanelAPI cargoPanel = panel.createCustomPanel(panelWidth, buttonHeight, null);
            TooltipMakerAPI cargoElement = cargoPanel.createUIElement(width / 2 - 10f, buttonHeight, false);
            generateAugmentForPanel(cargoElement, width, augmentCargoWrapper, augmentInStack);

            cargoPanel.addUIElement(cargoElement);
            panelList.add(cargoPanel);
        }

        for (int i = 0; i < panelList.size(); i++) {
            CustomPanelAPI customPanelAPI = panelList.get(i);
            uiElement.addCustom(customPanelAPI, i == 0 ? itemSpacing + 10f : itemSpacing);
        }

        panel.addUIElement(uiElement).inTR(0f, panelPadding + headerHeight + 60f - 6f);
    }

    private boolean augmentValidForDisplay(AugmentApplier augment) {
        if (isNull(augment))
            return false;

        boolean hasPrimarySlot = !isNull(augment.getPrimarySlot());
        boolean hasSecondarySlots = !isNullOrEmpty(augment.getSecondarySlots());
        boolean isNotDestroyed = augment.getAugmentQuality() != AugmentQuality.DESTROYED;
        boolean matchesPrimarySlot = !isNull(activeCategoryFilter) && activeCategoryFilter == augment.getPrimarySlot();
        boolean matchesSecondarySlot = !isNull(activeCategoryFilter) && augment.getSecondarySlots().contains(activeCategoryFilter);

        return (hasPrimarySlot || hasSecondarySlots) && isNotDestroyed && (matchesPrimarySlot || matchesSecondarySlot);
    }

    private List<AugmentCargoWrapper> getAugmentsInCargo() {
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        CargoAPI localCargo = Global.getFactory().createCargo(true);

        List<AugmentChestData> chestsInStorage = new ArrayList<>();
        List<AugmentCargoWrapper> augmentCargoWrappers = new ArrayList<>();

        for (EveryFrameScript transientScript : Global.getSector().getTransientScripts()) {
            if (transientScript instanceof VT_DockedAtSpaceportHelper) {
                localCargo = ((VT_DockedAtSpaceportHelper) transientScript).getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE).getCargo();
                break;
            }
        }

        searchCargoForItems(localCargo, chestsInStorage, augmentCargoWrappers, CargoSource.LOCAL_STORAGE);
        searchCargoForItems(playerCargo, chestsInStorage, augmentCargoWrappers, CargoSource.PLAYER_FLEET);
        for (AugmentChestData augmentChestData : chestsInStorage)
            searchCargoForItems(augmentChestData.getChestStorage(), chestsInStorage, augmentCargoWrappers, CargoSource.CARGO_CHEST);

        return augmentCargoWrappers;
    }

    private void searchCargoForItems(CargoAPI storageCargo, List<AugmentChestData> chestsInStorage, List<AugmentCargoWrapper> augmentCargoWrappers, CargoSource cargoSource) {
        for (CargoStackAPI cargoStackAPI : storageCargo.getStacksCopy()) {
            SpecialItemData specialItemData = cargoStackAPI.getSpecialDataIfSpecial();
            if (isNull(specialItemData))
                continue;

            if (specialItemData.getId().equals(VT_Items.STORAGE_CHEST)) {
                chestsInStorage.add(((AugmentChestData) specialItemData));
                continue;
            }

            if (specialItemData.getId().equals(VT_Items.AUGMENT_ITEM)) {
                CargoAPI cargo = Global.getFactory().createCargo(false);
                cargo.addFromStack(cargoStackAPI);
                augmentCargoWrappers.add(new AugmentCargoWrapper(cargoStackAPI, cargoSource, storageCargo));
            }
        }
    }

    private void generateAugmentForPanel(TooltipMakerAPI cargoElement, float width, final AugmentCargoWrapper augmentCargoWrapper, AugmentApplier augment) {
        final float panelWidth = width - shipListWidth;
        float buttonWidth = 80f;
        float buttonHeight = 24f;
        float iconSize = 24f;
        float itemSpacing = 10f;
        float itemPadding = 6f;
        String buttonText = "Select";
        Color baseColor = Misc.getBrightPlayerColor();
        Color bgColor = Misc.getDarkPlayerColor();

        if (!isNull(selectedAugmentInCargo) && augment == selectedAugmentInCargo.getAugment()) {
            buttonText = "Selected";
            baseColor = Misc.getHighlightColor();
            bgColor = Misc.getDarkHighlightColor();
        }

        Map<ButtonOption, AugmentCargoWrapper> buttonData = new HashMap<>();
        buttonData.put(ButtonOption.AUGMENT_SELECTED, augmentCargoWrapper);
        cargoElement.addButton(buttonText, buttonData, baseColor, bgColor, Alignment.MID, CutStyle.TL_BR, buttonWidth, buttonHeight, 0f);
        UIComponentAPI buttonComponent = cargoElement.getPrev();

        TooltipMakerAPI imageWithText = cargoElement.beginImageWithText(VT_Icons.AUGMENT_ITEM_ICON, iconSize);
        imageWithText.addPara(cargoElement.shortenString(augment.getName(), panelWidth - buttonWidth - iconSize - 2 * itemSpacing), augment.getAugmentQuality().getColor(), 0f).getPosition().setXAlignOffset(-7f);
        cargoElement.addImageWithText(-itemSpacing);
        cargoElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                augmentCargoWrapper.getAugmentCargoStack().getPlugin().createTooltip(tooltip, true, null, STACK_SOURCE);
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return panelWidth;
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
        UIComponentAPI cargoComponent = cargoElement.getPrev();
        cargoComponent.getPosition().rightOfMid(buttonComponent, itemPadding);

        cargoElement.addButton("", null, width / 2, 0f, 0f);
        UIComponentAPI separatorComponent = cargoElement.getPrev();
        separatorComponent.getPosition().belowLeft(buttonComponent, 0f);
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
        boolean isEmptyMap = ((HashMap<?, ?>) buttonId).isEmpty();
        boolean isNotSelectAugment = !((HashMap<?, ?>) buttonId).containsKey(ButtonOption.AUGMENT_SELECTED);
        boolean isNotSortAugment = !((HashMap<?, ?>) buttonId).containsKey(ButtonOption.SORT_BY_SLOT);
        return !isEmptyMap && isNotSelectAugment && isNotSortAugment;
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        if (!(buttonId instanceof HashMap))
            return;

        String bullet = "• ";

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_VESAI)) {
            float hullSizeMult = Misc.getSizeNum(((FleetMemberAPI) ((HashMap<?, ?>) buttonId).get(ButtonOption.INSTALL_VESAI)).getHullSpec().getHullSize());
            String installCost = Misc.getDGSCredits(installCostCredits * hullSizeMult);
            Color hlColor = Misc.getHighlightColor();
            if (hullmodInstallationWithSP) {
                installCost = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
                hlColor = Misc.getStoryOptionColor();
            }

            prompt.addPara("Do you want to install the VoidTec Engineering Suite on your ship?", 0f);
            prompt.addPara(String.format("This will cost you %s and remove all installed permanent hullmods.", installCost), 3f, hlColor, installCost);
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.UNLOCK_SLOT)) {
            AugmentSlot augmentSlot = (AugmentSlot) ((HashMap<?, ?>) buttonId).get(ButtonOption.UNLOCK_SLOT);
            int unlockedSlots = augmentSlot.getHullmodManager().getUnlockedSlotsNum();
            int installCost = installCostCredits * unlockedSlots;

            String installCostString = Misc.getDGSCredits(installCost);
            Color hlColor = Misc.getHighlightColor();
            if (unlockedSlots >= maxNumSlotsForCreditUnlock) {
                installCostString = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
                hlColor = Misc.getStoryOptionColor();
            }

            prompt.addPara("Do you want to unlock this slot?", 0f);
            prompt.addPara(String.format("This will cost you %s", installCostString), 3f, hlColor, installCostString);
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_AUGMENT)) {
            AugmentSlot augmentSlot = (AugmentSlot) ((HashMap<?, ?>) buttonId).get(ButtonOption.INSTALL_AUGMENT);
            AugmentApplier augment = selectedAugmentInCargo.getAugment();
            prompt.addPara("Install the augment in this slot (%s)?", 0f, augmentSlot.getSlotCategory().getColor(), augmentSlot.getSlotCategory().getName());
            if (!isNull(augment))
                prompt.addPara(bullet + augment.getName(), augment.getAugmentQuality().getColor(), 10f);
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.REMOVE_AUGMENT)) {
            AugmentSlot augmentSlot = (AugmentSlot) ((HashMap<?, ?>) buttonId).get(ButtonOption.REMOVE_AUGMENT);
            AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();

            String removalCost = String.format("%s Story " + FormattingTools.singularOrPlural(removalCostSP, "Point"), removalCostSP);
            Color hlColor = Misc.getStoryOptionColor();
            prompt.addPara("Do you want to remove this augment?", 0f);
            prompt.addPara(bullet + slottedAugment.getName(), slottedAugment.getAugmentQuality().getColor(), 10f);
            prompt.addPara(String.format("This will cost you %s", removalCost), 10f, hlColor, removalCost);
        }
    }

    @Override
    public String getConfirmText(Object buttonId) {
        return "Confirm";
    }

    @Override
    public String getCancelText(Object buttonId) {
        return "Cancel";
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (!(buttonId instanceof HashMap))
            return;

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_VESAI)) {
            FleetMemberAPI fleetMember = (FleetMemberAPI) ((HashMap<?, ?>) buttonId).get(ButtonOption.INSTALL_VESAI);
            float hullSizeMult = Misc.getSizeNum(fleetMember.getHullSpec().getHullSize());
            ShipVariantAPI memberVariant = fleetMember.getVariant();
            memberVariant.clearPermaMods();

            memberVariant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);

            if (hullmodInstallationWithSP)
                Global.getSector().getPlayerStats().addStoryPoints(-installCostSP);
            else
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCostCredits * hullSizeMult);
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.INSTALL_AUGMENT)) {
            AugmentSlot augmentSlot = (AugmentSlot) ((HashMap<?, ?>) buttonId).get(ButtonOption.INSTALL_AUGMENT);
            boolean success = augmentSlot.installAugment(selectedAugmentInCargo.getAugment());
            if (success)
                removeAugmentFromCargo();

            selectedAugmentInCargo = null;
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.UNLOCK_SLOT)) {
            AugmentSlot augmentSlot = (AugmentSlot) ((HashMap<?, ?>) buttonId).get(ButtonOption.UNLOCK_SLOT);
            int unlockedSlots = augmentSlot.getHullmodManager().getUnlockedSlotsNum();
            int installCost = installCostCredits * unlockedSlots;

            if (unlockedSlots <= maxNumSlotsForCreditUnlock)
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCost);
            else
                Global.getSector().getPlayerStats().addStoryPoints(-installCostSP);

            augmentSlot.unlockSlot();
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.AUGMENT_SELECTED)) {
            AugmentCargoWrapper selectedAugment = (AugmentCargoWrapper) ((HashMap<?, ?>) buttonId).get(ButtonOption.AUGMENT_SELECTED);

            if (!isNull(selectedAugmentInCargo) && selectedAugment.getAugment() == selectedAugmentInCargo.getAugment())
                selectedAugmentInCargo = null;
            else
                selectedAugmentInCargo = selectedAugment;
        }

        if (((HashMap<?, ?>) buttonId).containsKey(ButtonOption.SORT_BY_SLOT)) {
            Object buttonData = ((HashMap<?, ?>) buttonId).get(ButtonOption.SORT_BY_SLOT);

            SlotCategory selectedSlot = buttonData instanceof AugmentSlot
                    ? ((AugmentSlot) buttonData).getSlotCategory()
                    : ((SlotCategory) buttonData);

            if (!isNull(activeCategoryFilter) && activeCategoryFilter == selectedSlot)
                activeCategoryFilter = null;
            else
                activeCategoryFilter = selectedSlot;
        }

        Global.getSector().getPlayerFleet().getFleetData().setSyncNeeded();
        Global.getSector().getPlayerFleet().getFleetData().syncMemberLists();
        ui.updateUIForItem(this);
    }

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {
    }

    private void removeAugmentFromCargo() {
        CargoAPI cargo = selectedAugmentInCargo.getSourceCargo();
        for (CargoStackAPI cargoStackAPI : cargo.getStacksCopy()) {
            if (cargoStackAPI.getData() == selectedAugmentInCargo.getAugmentCargoStack().getData()) {
                cargoStackAPI.setSize(cargoStackAPI.getSize() - 1);
                cargo.removeEmptyStacks();
                return;
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
