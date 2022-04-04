package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.FilterByCategoryButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.SelectAugmentButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.SortOrderButton;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.RainbowString;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

@Getter
@AllArgsConstructor
public class CargoPanel {

    public static boolean showDestroyedAugments = false;
    public static boolean showOnlyRepairable = false;

    private final float panelWidth;
    private final float panelHeight;
    private final float padding;

    private final float headerHeight = 20f;
    private final float sorterHeight = 68f;
    private final float elementHeight = 24f;
    private final float filterButtonSize = 24f;
    private final float itemSpacing = 3f;
    private final float listSpacing = 64f;

    public void render(CustomPanelAPI mainPanel) {
        addHeader(mainPanel);
        addAugmentList(mainPanel);
    }

    private void addHeader(CustomPanelAPI mainPanel) {
        TooltipMakerAPI headerElement = mainPanel.createUIElement(panelWidth, 0f, false);
        String headerText = showOnlyRepairable ? "Damaged Augments" : "Available Augments";
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
        List<CustomPanelAPI> panelList = new ArrayList<>();

        boolean hasSelectedAugmentInList = false;
        for (AugmentCargoWrapper augmentCargoWrapper : AugmentManagerIntel.getAugmentsInCargo()) {
            final AugmentApplier augmentInStack = augmentCargoWrapper.getAugment();
            if (!(isNull(AugmentManagerIntel.getActiveCategoryFilter()) || matchesFilter(augmentInStack))) {
                continue;
            }

            if (showOnlyRepairable && !augmentInStack.isRepairable()) {
                continue;
            }

            if (!showDestroyedAugments && augmentInStack.getAugmentQuality() == AugmentQuality.DESTROYED) {
                continue;
            }

            CustomPanelAPI cargoPanel = mainPanel.createCustomPanel(panelWidth, elementHeight, null);
            TooltipMakerAPI cargoElement = cargoPanel.createUIElement(panelWidth, elementHeight, false);
            generateAugmentForPanel(cargoElement, panelWidth, augmentCargoWrapper);

            cargoPanel.addUIElement(cargoElement);
            panelList.add(cargoPanel);

            if (!hasSelectedAugmentInList && !isNull(AugmentManagerIntel.getSelectedAugmentInCargo())) {
                hasSelectedAugmentInList = augmentInStack == AugmentManagerIntel.getSelectedAugmentInCargo().getAugment();
            }
        }

        for (int i = 0; i < panelList.size(); i++) {
            CustomPanelAPI customPanelAPI = panelList.get(i);
            uiElement.addCustom(customPanelAPI, i == 0 ? itemSpacing + 10f : itemSpacing).getPosition().setXAlignOffset(0f);
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

        boolean matchesPrimarySlot = !isNull(
                AugmentManagerIntel.getActiveCategoryFilter()) && AugmentManagerIntel.getActiveCategoryFilter() == augment.getPrimarySlot();
        boolean matchesSecondarySlot = !isNull(AugmentManagerIntel.getActiveCategoryFilter()) && !isNullOrEmpty(
                augment.getSecondarySlots()) && augment.getSecondarySlots().contains(AugmentManagerIntel.getActiveCategoryFilter());

        return matchesPrimarySlot || matchesSecondarySlot;
    }

    private void generateAugmentForPanel(final TooltipMakerAPI cargoElement, final float width,
                                         final AugmentCargoWrapper augmentCargoWrapper) {
        float buttonWidth = 24f;
        float buttonHeight = 24f;
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

        cargoElement.setButtonFontVictor14();
        ButtonUtils.addCheckboxButton(cargoElement, buttonWidth, buttonHeight, 0f, baseColor, bgColor,
                                      augment.getAugmentQuality().getColor(), new SelectAugmentButton(augmentCargoWrapper))
                   .setChecked(true);
        UIComponentAPI buttonComponent = cargoElement.getPrev();
        cargoElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return cargoElement.computeStringWidth(selectButtonTooltipText);
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(selectButtonTooltipText, 0f);
            }
        }, TooltipMakerAPI.TooltipLocation.LEFT);

        TooltipMakerAPI imageWithText = cargoElement.beginImageWithText(VT_Icons.AUGMENT_ITEM_ICON, iconSize);
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
        cargoElement.addImageWithText(-itemSpacing);
        cargoElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                AugmentItemPlugin augmentItemPlugin = (AugmentItemPlugin) augmentCargoWrapper.getAugmentCargoStack().getPlugin();
                augmentItemPlugin.createTooltip(tooltip, true, null, AugmentManagerIntel.STACK_SOURCE);
            }
        }, TooltipMakerAPI.TooltipLocation.LEFT);
        UIComponentAPI imageWithTextComponent = cargoElement.getPrev();
        imageWithTextComponent.getPosition().rightOfMid(buttonComponent, itemPadding).setYAlignOffset(3f);

        List<SlotCategory> secondarySlots = augment.getSecondarySlots();
        int numSecondarySlots = secondarySlots.size();

        float slotPadding = 1f;
        float primarySlotWidth = (float) Math.ceil(slotIndicatorWidth / (numSecondarySlots + 2));
        float secondarySlotWidths = (float) Math.ceil(numSecondarySlots > 0 ? primarySlotWidth - slotPadding : 0);

        float totalIndicatorWidth = primarySlotWidth * 2 + (secondarySlotWidths + 1f) * numSecondarySlots;
        float overhang = slotIndicatorWidth - totalIndicatorWidth;
        primarySlotWidth = primarySlotWidth * 2 + overhang;

        ButtonAPI primarySlotLine = ButtonUtils.addSeparatorLine(cargoElement, primarySlotWidth,
                                                                 augment.getPrimarySlot().getColor(),
                                                                 0f);
        primarySlotLine.getPosition().belowLeft(buttonComponent, -5f).setXAlignOffset(buttonWidth + itemPadding / 2);

        ButtonAPI lastLine = primarySlotLine;
        for (SlotCategory slot : secondarySlots) {
            ButtonAPI secondarySlotLine = ButtonUtils.addSeparatorLine(cargoElement, secondarySlotWidths, slot.getColor(), 0f);
            UIComponentAPI uiComponent = cargoElement.getPrev();
            uiComponent.getPosition().rightOfMid(lastLine, 1f);
            lastLine = secondarySlotLine;
        }

        ButtonAPI separatorLine = ButtonUtils.addSeparatorLine(cargoElement, totalLineWidth, Misc.getDarkPlayerColor(), 0f);
        separatorLine.getPosition().belowLeft(buttonComponent, -1f).setXAlignOffset(buttonWidth);
    }
}
