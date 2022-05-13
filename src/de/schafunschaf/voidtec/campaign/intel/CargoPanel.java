package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel.*;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.MalfunctionEffect;
import de.schafunschaf.voidtec.helper.RainbowString;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import de.schafunschaf.voidtec.util.ui.plugins.BasePanelPlugin;
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
    private final float sorterHeight = 100f;
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
        Color selectedColor = isNull(AugmentManagerIntel.getActiveQualityFilter())
                              ? Color.WHITE
                              : AugmentManagerIntel.getActiveQualityFilter().getColor();

        filterElement.addSpacer(3f).getPosition().setXAlignOffset(4f);

        ButtonAPI lastButton = null;
        for (final SlotCategory slotCategory : SlotCategory.values) {
            ButtonAPI augmentButton = new FilterByCategoryButton(slotCategory).addButton(filterElement, filterButtonSize, filterButtonSize);

            if (!isNull(lastButton)) {
                augmentButton.getPosition().rightOfMid(lastButton, 4f);
            }

            lastButton = augmentButton;
        }

        filterElement.addSpacer(30f).getPosition().inTL(5f, 0f);
        filterElement.setParaFont(Fonts.VICTOR_10);
        float slotBorderWidth = 227f;
        float slotBorderHeight = 15f;
        float qualityBorderWidth = 212f;
        float qualityBorderHeight = 10f;
        UIUtils.addHorizontalSeparator(filterElement, slotBorderWidth, 1f, Misc.getBasePlayerColor(), 1f)
               .getPosition()
               .setXAlignOffset(23f);
        UIUtils.addVerticalSeparator(filterElement, 1f, slotBorderHeight, Misc.getBasePlayerColor())
               .getPosition()
               .setXAlignOffset(slotBorderWidth)
               .setYAlignOffset(
                       slotBorderHeight);
        filterElement.addPara("Slot", -4f);
        filterElement.getPrev().getPosition().setXAlignOffset(-25f - slotBorderWidth);
        filterElement.addPara("Quality", 0f).setAlignment(Alignment.RMID);
        filterElement.getPrev().getPosition().setXAlignOffset(0f);
        UIUtils.addHorizontalSeparator(filterElement, qualityBorderWidth, 1f, Misc.getBasePlayerColor(), -1f)
               .getPosition()
               .setXAlignOffset(-2f);
        UIUtils.addVerticalSeparator(filterElement, 1f, qualityBorderHeight, Misc.getBasePlayerColor());
        filterElement.addSpacer(3f).getPosition().setXAlignOffset(4f).setYAlignOffset(qualityBorderHeight);

        UIComponentAPI lastBox = null;
        for (AugmentQuality quality : AugmentQuality.values) {
            if (quality == AugmentQuality.DESTROYED) {
                continue;
            }

            UIComponentAPI qualityFilter = new FilterByQualityButton(quality).addButton(filterElement, 30f, 15f);

            if (!isNull(lastBox)) {
                qualityFilter.getPosition().rightOfTop(lastBox, 6f);
            }

            lastBox = qualityFilter;
        }

        filterElement.addSpacer(62f).getPosition().inTL(5f, 0f);

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
            if (!(isNull(AugmentManagerIntel.getActiveCategoryFilter()) || matchesCategoryFilter(augmentInStack))) {
                continue;
            }

            if (!(isNull(AugmentManagerIntel.getActiveQualityFilter()) || matchesQualityFilter(augmentInStack))) {
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

    private boolean matchesCategoryFilter(AugmentApplier augment) {
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

    private boolean matchesQualityFilter(AugmentApplier augment) {
        if (isNull(augment)) {
            return false;
        }

        AugmentQuality activeQualityFilter = AugmentManagerIntel.getActiveQualityFilter();
        return (!isNull(activeQualityFilter) && activeQualityFilter == augment.getAugmentQuality());
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

        CustomPanelAPI elementPanel = mainPanel.createCustomPanel(width, selectionBoxHeight, new BasePanelPlugin() {
            // Render animated icon
            @Override
            public void renderBelow(float alphaMult) {
                AugmentItemData augmentItemData = ((AugmentItemPlugin) augmentCargoWrapper.getAugmentCargoStack()
                                                                                          .getPlugin()).getAugmentItemData();
                ColorShifter colorShifter = augmentItemData.getColorShifter();
                MalfunctionEffect malfunctionEffect = augmentItemData.getMalfunctionEffect();
                Color glowColor = augmentCargoWrapper.getAugment().getPrimarySlot().getColor();

                if (VT_Settings.iconFlicker) {
                    if (!isNull(colorShifter)) {
                        glowColor = colorShifter.shiftColor(0.5f);
                    } else if (!isNull(malfunctionEffect)) {
                        glowColor = malfunctionEffect.renderFlicker(augmentCargoWrapper.getAugment().getPrimarySlot().getColor());
                    }
                }

                SpriteAPI spriteCover = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_ICON_COVER);
                SpriteAPI spriteGlow = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_ICON_GLOW);
                spriteGlow.setColor(glowColor);
                spriteGlow.setSize(30, 30);
                spriteGlow.render(p.getX() + 30, p.getY() + 4);
                spriteCover.setSize(30, 30);
                spriteCover.render(p.getX() + 30, p.getY() + 4);
            }
        });
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

        // Blank Image with Name
        TooltipMakerAPI imageWithText = uiElement.beginImageWithText(VT_Icons.BLANK_ICON, iconSize);
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

        // Dismantle Stack Button
        if (augment.getAugmentQuality() != AugmentQuality.CUSTOMISED) {
            ButtonAPI dismantleStackButton = new DismantleStackButton(augmentCargoWrapper).addButton(uiElement, 10, 10);
            dismantleStackButton.getPosition().inTR(7, 0);
        }

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
