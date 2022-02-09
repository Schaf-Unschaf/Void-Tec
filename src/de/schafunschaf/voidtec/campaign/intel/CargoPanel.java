package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.FilterByCategoryButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.SelectAugmentButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.SortOrderButton;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.RainbowString;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

@Getter
public class CargoPanel implements DisplayablePanel {

    public static boolean showDestroyedAugments = false;
    public static boolean showOnlyRepairable = false;

    private float panelWidth;
    private float panelHeight;

    @Override
    public void displayPanel(CustomPanelAPI panel, float width, float height, float padding) {
        float headerHeight = 21f;
        float sorterHeight = 68f;
        float buttonHeight = 30f;
        float filterButtonSize = 24f;
        float itemSpacing = 3f;
        float listSpacing = 64f;

        TooltipMakerAPI headerElement = panel.createUIElement(width - 3f, 0f, false);
        headerElement.addSectionHeading("Available Augments", Misc.getBrightPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        panel.addUIElement(headerElement).inTR(0f, headerHeight);

        final TooltipMakerAPI filterElement = panel.createUIElement(width - 3f, 0f, false);
        String selectedSlot = isNull(AugmentManagerIntel.getActiveCategoryFilter())
                              ? "ALL"
                              : AugmentManagerIntel.getActiveCategoryFilter().toString();
        Color selectedColor = isNull(AugmentManagerIntel.getActiveCategoryFilter())
                              ? Color.WHITE
                              : AugmentManagerIntel.getActiveCategoryFilter().getColor();

        ButtonAPI lastButton = null;
        for (final SlotCategory slotCategory : SlotCategory.values) {
            Color buttonColor = isNull(
                    AugmentManagerIntel.getActiveCategoryFilter()) || AugmentManagerIntel.getActiveCategoryFilter() == slotCategory
                                ? slotCategory.getColor()
                                : Misc.scaleColorOnly(slotCategory.getColor(), 0.3f);
            final String tooltipText = String.format("Display only %s slots", slotCategory);
            final float tooltipWidth = headerElement.computeStringWidth(tooltipText);

            ButtonAPI augmentButton = ButtonUtils.addAugmentButton(filterElement, filterButtonSize, 6f, buttonColor, buttonColor,
                                                                   new FilterByCategoryButton(slotCategory));
            if (!isNull(lastButton)) {
                augmentButton.getPosition().rightOfMid(lastButton, 4f);
            } else {
                augmentButton.getPosition().setXAlignOffset(4f);
            }

            filterElement.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return tooltipWidth;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(tooltipText, 0f, slotCategory.getColor(), slotCategory.toString());
                }
            }, TooltipMakerAPI.TooltipLocation.ABOVE);

            lastButton = augmentButton;
        }

        UIComponentAPI spacerComponent = filterElement.addSpacer(28f);
        spacerComponent.getPosition().inTL(5f, 0f);

        filterElement.setParaFont(Fonts.ORBITRON_20AA);
        filterElement.addPara("Filter:", Misc.getBasePlayerColor(), 10f);

        LabelAPI filterTextLabel = filterElement.addPara("%s", 6f, Misc.getGrayColor(), selectedColor, selectedSlot);
        UIComponentAPI filterTextElement = filterElement.getPrev();
        filterTextLabel.getPosition().setYAlignOffset(filterTextLabel.getPosition().getHeight());
        filterTextLabel.setAlignment(Alignment.MID);
        filterElement.setParaFont(Fonts.DEFAULT_SMALL);

        ButtonAPI sortOrderButton = new SortOrderButton().createButton(filterElement, 20f, 20f);
        sortOrderButton.getPosition().rightOfTop(filterTextElement, -sortOrderButton.getPosition().getWidth()).setYAlignOffset(1f);

        ButtonUtils.addSeparatorLine(filterElement, filterTextElement.getPosition().getWidth(), Misc.getDarkPlayerColor(), 0f)
                   .getPosition().belowLeft(filterTextElement, 2f);
        panel.addUIElement(filterElement).belowLeft(headerElement, 0f);

        TooltipMakerAPI uiElement = panel.createUIElement(width, height - padding - headerHeight - sorterHeight, true);
        List<CustomPanelAPI> panelList = new ArrayList<>();

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

            CustomPanelAPI cargoPanel = panel.createCustomPanel(width, buttonHeight, null);
            TooltipMakerAPI cargoElement = cargoPanel.createUIElement(width - 10f, buttonHeight, false);
            generateAugmentForPanel(cargoElement, width, augmentCargoWrapper);

            cargoPanel.addUIElement(cargoElement);
            panelList.add(cargoPanel);
        }

        for (int i = 0; i < panelList.size(); i++) {
            CustomPanelAPI customPanelAPI = panelList.get(i);
            uiElement.addCustom(customPanelAPI, i == 0 ? itemSpacing + 10f : itemSpacing);
        }

        panel.addUIElement(uiElement).inTR(0f, padding + headerHeight + listSpacing);
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
        float itemSpacing = 10f;
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
            imageWithText.addPara(rainbowString.getConvertedString(), 3f, rainbowString.getHlColors(), rainbowString.getHlStrings());
        } else {
            imageWithText.addPara(cargoElement.shortenString(augment.getName(), width - buttonWidth - iconSize - 2f - itemSpacing * 2),
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
