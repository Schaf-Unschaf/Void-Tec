package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.FilterByCategoryButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.SelectAugmentButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.SortOrderButton;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.RainbowString;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

@Getter
@AllArgsConstructor
public class CargoPanel {

    private final float panelWidth;
    private final float panelHeight;
    private final float padding;

    private final float headerHeight = 20f;
    private final float sorterHeight = 68f;
    private final float elementHeight = 28f;
    private final float filterButtonSize = 24f;
    private final float itemSpacing = 3f;
    private final float listSpacing = 64f;

    public void render(CustomPanelAPI mainPanel) {
        addHeader(mainPanel);
        addAugmentList(mainPanel);
    }

    private void addHeader(CustomPanelAPI mainPanel) {
        TooltipMakerAPI headerElement = mainPanel.createUIElement(panelWidth, 0f, false);
        String headerText = "Available Augments";
        headerElement.addSectionHeading(headerText, Misc.getBrightPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        mainPanel.addUIElement(headerElement).inTR(0f, headerHeight);

        final TooltipMakerAPI filterElement = mainPanel.createUIElement(panelWidth, 0f, false);
        String selectedSlot = isNull(AugmentManagerIntel.getActiveCategoryFilter())
                              ? "ALL"
                              : AugmentManagerIntel.getActiveCategoryFilter().toString();
        Color selectedColor = isNull(AugmentManagerIntel.getActiveCategoryFilter())
                              ? Color.WHITE
                              : AugmentManagerIntel.getActiveCategoryFilter().getColor();

        ButtonAPI lastButton = null;
        for (final SlotCategory slotCategory : SlotCategory.values) {
            ButtonAPI augmentButton = new FilterByCategoryButton(slotCategory).addButton(filterElement, filterButtonSize, filterButtonSize);

            if (!isNull(lastButton)) {
                augmentButton.getPosition().rightOfMid(lastButton, 4f);
            } else {
                augmentButton.getPosition().setXAlignOffset(4f).setYAlignOffset(-6f);
            }

            lastButton = augmentButton;
        }

        UIComponentAPI spacerComponent = filterElement.addSpacer(28f);
        spacerComponent.getPosition().inTL(5f, 0f);

        filterElement.setParaFont(Fonts.ORBITRON_20AA);
        filterElement.addPara("Filter:", Misc.getBasePlayerColor(), 7f);

        LabelAPI filterTextLabel = filterElement.addPara("%s", 6f, Misc.getGrayColor(), selectedColor, selectedSlot);
        UIComponentAPI filterTextElement = filterElement.getPrev();
        filterTextLabel.getPosition().setYAlignOffset(filterTextLabel.getPosition().getHeight());
        filterTextLabel.setAlignment(Alignment.MID);
        filterElement.setParaFont(Fonts.DEFAULT_SMALL);

        ButtonAPI sortOrderButton = new SortOrderButton().addButton(filterElement, 45f, 20f);
        sortOrderButton.getPosition().rightOfTop(filterTextElement, -sortOrderButton.getPosition().getWidth()).setYAlignOffset(-1f);

        ButtonUtils.addSeparatorLine(filterElement, filterTextElement.getPosition().getWidth(), Misc.getDarkPlayerColor(), 0f)
                   .getPosition().belowLeft(filterTextElement, 1f);
        mainPanel.addUIElement(filterElement).belowLeft(headerElement, 0f);
    }

    private void addAugmentList(CustomPanelAPI mainPanel) {
        TooltipMakerAPI uiElement = mainPanel.createUIElement(panelWidth, panelHeight - headerHeight - sorterHeight + 7f, true);

        uiElement.addSpacer(5f);
        boolean hasSelectedAugmentInList = false;
        for (AugmentCargoWrapper augmentCargoWrapper : AugmentManagerIntel.getAugmentsInCargo()) {
            final AugmentApplier augmentInStack = augmentCargoWrapper.getAugment();
            if (!(isNull(AugmentManagerIntel.getActiveCategoryFilter()) || matchesFilter(augmentInStack))) {
                continue;
            }

            if (augmentInStack.getAugmentQuality() == AugmentQuality.DESTROYED) {
                continue;
            }

            CustomPanelAPI cargoPanel = mainPanel.createCustomPanel(panelWidth, elementHeight, null);
            TooltipMakerAPI cargoElement = cargoPanel.createUIElement(panelWidth, elementHeight, false);
            generateAugmentForPanel(mainPanel, cargoElement, panelWidth, augmentCargoWrapper);

            cargoPanel.addUIElement(cargoElement).setXAlignOffset(-10f);
            uiElement.addCustom(cargoPanel, 0f);

            if (!hasSelectedAugmentInList && !isNull(AugmentManagerIntel.getSelectedAugmentInCargo())) {
                hasSelectedAugmentInList = augmentInStack == AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
            }
        }

        if (!hasSelectedAugmentInList) {
            AugmentManagerIntel.setSelectedAugmentInCargo(null);
        }

        mainPanel.addUIElement(uiElement).inBR(0f, 0f);
    }

    private boolean matchesFilter(AugmentApplier augment) {
        if (isNull(augment)) {
            return false;
        }

        SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
        AugmentSlot selectedSlot = AugmentManagerIntel.getSelectedSlot();
        boolean matchesPrimarySlot = !isNull(activeCategoryFilter) && activeCategoryFilter == augment.getPrimarySlot();
        boolean matchesSecondarySlot = !isNull(activeCategoryFilter)
                && !isNullOrEmpty(augment.getSecondarySlots()) && augment.getSecondarySlots().contains(activeCategoryFilter);
        boolean isAlreadyInstalled = !isNull(selectedSlot) && selectedSlot.getHullModManager().hasSameAugmentSlotted(augment);

        return (matchesPrimarySlot || matchesSecondarySlot) && !isAlreadyInstalled;
    }

    private void generateAugmentForPanel(CustomPanelAPI mainPanel, final TooltipMakerAPI cargoElement,
                                         final float width, final AugmentCargoWrapper augmentCargoWrapper) {
        float buttonWidth = 24f;
        float buttonHeight = 24f;
        float selectionBoxHeight = 30f;
        float iconSize = 24f;
        float itemSpacing = 12f;
        float itemPadding = 4f;
        float slotIndicatorWidth = iconSize + itemPadding;
        float totalLineWidth = width - buttonWidth - itemSpacing;
        Color baseColor = Misc.getBasePlayerColor();
        Color bgColor = Misc.getDarkPlayerColor();
        final String selectButtonTooltipText;

        AugmentApplier augment = augmentCargoWrapper.getAugment();

        if (!isNull(AugmentManagerIntel.getSelectedAugmentInCargo())
                && augment == AugmentManagerIntel.getSelectedAugmentInCargo().getAugment()) {
            baseColor = Misc.getHighlightColor();
            bgColor = Misc.getDarkHighlightColor();
            selectButtonTooltipText = "Augment selected.\n" +
                    "To install, click on one of the\n" +
                    "highlighted slots in the Fleet Overview.";
        } else {
            selectButtonTooltipText = "Press the button to select\n" +
                    "this Augment for installation.";
        }

        CustomPanelAPI elementPanel = mainPanel.createCustomPanel(width, selectionBoxHeight, null);
        final TooltipMakerAPI uiElement = elementPanel.createUIElement(width, selectionBoxHeight, false);

        uiElement.setButtonFontVictor14();

        // Select Checkbox
        ButtonUtils.addCheckboxButton(uiElement, buttonWidth, buttonHeight, 0f, baseColor, bgColor,
                                      augment.getAugmentQuality().getColor(), new SelectAugmentButton(augmentCargoWrapper))
                   .setChecked(true);
        UIComponentAPI buttonComponent = uiElement.getPrev();

        // Selection Box when Augment is selected
        if (!isNull(AugmentManagerIntel.getSelectedAugmentInCargo())) {
            if (augment == AugmentManagerIntel.getSelectedAugmentInCargo().getAugment()) {
                UIUtils.addBox(uiElement, "", null, null, width - 8, buttonHeight + itemPadding, 1, 0, null, Misc.getHighlightColor(),
                               Misc.scaleAlpha(Misc.getHighlightColor(), 0.15f), null)
                       .getPosition()
                       .rightOfTop(buttonComponent, -buttonWidth - 2)
                       .setYAlignOffset(2f);
            }
        }

        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return uiElement.computeStringWidth(selectButtonTooltipText);
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(selectButtonTooltipText, 0f);
            }
        }, TooltipMakerAPI.TooltipLocation.LEFT);

        // Image with Name
        TooltipMakerAPI imageWithText = uiElement.beginImageWithText(VT_Icons.AUGMENT_ITEM_ICON, iconSize);
        if (augment.getName().toLowerCase().contains("rainbow")) {
            RainbowString rainbowString = new RainbowString(augment.getName(), Color.RED, 20);
            imageWithText.addPara(
                    imageWithText.shortenString(rainbowString.getConvertedString(), width - buttonWidth - iconSize - 2f - itemSpacing * 2),
                    3f, rainbowString.getHlColors(),
                    rainbowString.getHlStrings());
        } else {
            imageWithText.addPara(imageWithText.shortenString(augment.getName(), width - buttonWidth - iconSize - 2f - itemSpacing * 2),
                                  augment.getAugmentQuality().getColor(), 3f);
        }
        imageWithText.getPrev().getPosition().setXAlignOffset(-6f);
        uiElement.addImageWithText(-itemSpacing);
        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                AugmentItemPlugin augmentItemPlugin = (AugmentItemPlugin) augmentCargoWrapper.getAugmentCargoStack().getPlugin();
                augmentItemPlugin.createTooltip(tooltip, true, null, AugmentManagerIntel.STACK_SOURCE);
            }
        }, TooltipMakerAPI.TooltipLocation.LEFT);
        UIComponentAPI imageWithTextComponent = uiElement.getPrev();
        imageWithTextComponent.getPosition().rightOfMid(buttonComponent, itemPadding).setYAlignOffset(3f);

        // Damage indicators
        int damageAmount = augment.getInitialQuality().ordinal() - augment.getAugmentQuality().ordinal();
        if (damageAmount > 0) {
            UIUtils.addIndicatorBars(uiElement, damageAmount, 2, 5, Misc.getNegativeHighlightColor())
                   .getPosition()
                   .leftOfTop(imageWithTextComponent, -iconSize + itemPadding)
                   .setYAlignOffset(-5f);
        }

        // Slot indicators
        List<SlotCategory> secondarySlots = augment.getSecondarySlots();
        int numSecondarySlots = secondarySlots.size();

        float slotPadding = 1f;
        float primarySlotWidth = (float) Math.ceil(slotIndicatorWidth / (numSecondarySlots + 2));
        float secondarySlotWidths = (float) Math.ceil(numSecondarySlots > 0 ? primarySlotWidth - slotPadding : 0);

        float totalIndicatorWidth = primarySlotWidth * 2 + (secondarySlotWidths + 1f) * numSecondarySlots;
        float overhang = slotIndicatorWidth - totalIndicatorWidth;
        primarySlotWidth = primarySlotWidth * 2 + overhang;

        ButtonAPI primarySlotLine = ButtonUtils.addSeparatorLine(uiElement, primarySlotWidth,
                                                                 augment.getPrimarySlot().getColor(),
                                                                 0f);
        primarySlotLine.getPosition().belowLeft(buttonComponent, -5f).setXAlignOffset(buttonWidth + itemPadding / 2);

        ButtonAPI lastLine = primarySlotLine;
        for (SlotCategory slot : secondarySlots) {
            ButtonAPI secondarySlotLine = ButtonUtils.addSeparatorLine(uiElement, secondarySlotWidths, slot.getColor(), 0f);
            UIComponentAPI uiComponent = uiElement.getPrev();
            uiComponent.getPosition().rightOfMid(lastLine, 1f);
            lastLine = secondarySlotLine;
        }

        // Bottom line
        ButtonAPI separatorLine = ButtonUtils.addSeparatorLine(uiElement, totalLineWidth, Misc.getDarkPlayerColor(), 0f);
        separatorLine.getPosition().belowLeft(buttonComponent, -1f).setXAlignOffset(buttonWidth);

        elementPanel.addUIElement(uiElement).inTL(0f, 0f);
        cargoElement.addCustom(elementPanel, 0f);
    }
}
