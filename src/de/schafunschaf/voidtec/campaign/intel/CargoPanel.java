package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.VT_Icons;
import de.schafunschaf.voidtec.campaign.intel.buttons.FilterByCategoryButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.SelectAugmentButton;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ButtonUtils;
import de.schafunschaf.voidtec.util.CargoUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

public class CargoPanel {

    public static void addAugmentsInCargoPanel(CustomPanelAPI panel, float width, float height, float padding) {
        List<AugmentCargoWrapper> augmentsInCargo = CargoUtils.getAugmentsInCargo();
        float headerHeight = 21f;
        float sorterHeight = 68f;
        float buttonHeight = 30f;
        float filterButtonSize = 24f;
        float itemSpacing = 3f;

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
        filterTextLabel.getPosition().setYAlignOffset(filterTextLabel.getPosition().getHeight());
        filterTextLabel.setAlignment(Alignment.MID);

        ButtonUtils.addSeparatorLine(filterElement, width - 8f, Misc.getDarkPlayerColor(), 2f);
        panel.addUIElement(filterElement).belowLeft(headerElement, 0f);

        TooltipMakerAPI uiElement = panel.createUIElement(width, height - padding - headerHeight - sorterHeight, true);
        List<CustomPanelAPI> panelList = new ArrayList<>();

        for (AugmentCargoWrapper augmentCargoWrapper : augmentsInCargo) {
            final AugmentApplier augmentInStack = augmentCargoWrapper.getAugment();
            if (!(isNull(AugmentManagerIntel.getActiveCategoryFilter()) || augmentValidForDisplay(augmentInStack))) {
                continue;
            }

            CustomPanelAPI cargoPanel = panel.createCustomPanel(width, buttonHeight, null);
            TooltipMakerAPI cargoElement = cargoPanel.createUIElement(width - 10f, buttonHeight, false);
            generateAugmentForPanel(cargoElement, width, augmentCargoWrapper, augmentInStack);

            cargoPanel.addUIElement(cargoElement);
            panelList.add(cargoPanel);
        }

        for (int i = 0; i < panelList.size(); i++) {
            CustomPanelAPI customPanelAPI = panelList.get(i);
            uiElement.addCustom(customPanelAPI, i == 0 ? itemSpacing + 10f : itemSpacing);
        }

        panel.addUIElement(uiElement).inTR(0f, padding + headerHeight + 60f - 6f);
    }

    private static boolean augmentValidForDisplay(AugmentApplier augment) {
        if (isNull(augment)) {
            return false;
        }

        boolean isNotDestroyed = augment.getAugmentQuality() != AugmentQuality.DESTROYED;
        boolean matchesPrimarySlot = !isNull(
                AugmentManagerIntel.getActiveCategoryFilter()) && AugmentManagerIntel.getActiveCategoryFilter() == augment.getPrimarySlot();
        boolean matchesSecondarySlot = !isNull(AugmentManagerIntel.getActiveCategoryFilter()) && !isNullOrEmpty(
                augment.getSecondarySlots()) && augment.getSecondarySlots().contains(AugmentManagerIntel.getActiveCategoryFilter());

        return isNotDestroyed && (matchesPrimarySlot || matchesSecondarySlot);
    }

    private static void generateAugmentForPanel(TooltipMakerAPI cargoElement, final float width,
                                                final AugmentCargoWrapper augmentCargoWrapper, AugmentApplier augment) {
        float buttonWidth = 80f;
        float buttonHeight = 24f;
        float iconSize = 24f;
        float itemSpacing = 10f;
        float itemPadding = 6f;
        Color baseColor = Misc.getBrightPlayerColor();
        Color bgColor = Misc.getDarkPlayerColor();

        if (!isNull(AugmentManagerIntel.getSelectedAugmentInCargo()) && augment == AugmentManagerIntel.getSelectedAugmentInCargo()
                                                                                                      .getAugment()) {
            baseColor = Misc.getHighlightColor();
            bgColor = Misc.getDarkHighlightColor();
        }

        ButtonUtils.addLabeledButton(cargoElement, buttonWidth, buttonHeight, 0f, baseColor, bgColor, CutStyle.TL_BR,
                                     new SelectAugmentButton(augmentCargoWrapper));
        UIComponentAPI buttonComponent = cargoElement.getPrev();

        TooltipMakerAPI imageWithText = cargoElement.beginImageWithText(VT_Icons.AUGMENT_ITEM_ICON, iconSize);
        imageWithText.addPara(cargoElement.shortenString(augment.getName(), width - buttonWidth - iconSize - 2f - itemSpacing * 2),
                              augment.getAugmentQuality().getColor(), 0f).getPosition().setXAlignOffset(-7f);
        cargoElement.addImageWithText(-itemSpacing);
        cargoElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                AugmentItemPlugin augmentItemPlugin = (AugmentItemPlugin) augmentCargoWrapper.getAugmentCargoStack().getPlugin();
                augmentItemPlugin.createTooltip(tooltip, true, null, AugmentManagerIntel.STACK_SOURCE);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
        UIComponentAPI cargoComponent = cargoElement.getPrev();
        cargoComponent.getPosition().rightOfMid(buttonComponent, itemPadding);

        ButtonUtils.addSeparatorLine(cargoElement, width, Misc.getDarkPlayerColor(), 0f);
        UIComponentAPI separatorComponent = cargoElement.getPrev();
        separatorComponent.getPosition().belowLeft(buttonComponent, 0f);
    }
}
