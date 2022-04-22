package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsUtility;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair.DismantleAugmentButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair.RepairAugmentButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair.ShowPrimaryButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair.ShowSecondaryButton;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import de.schafunschaf.voidtec.util.ui.plugins.GlowingBox;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class RepairTab {

    @Setter
    @Getter
    private static boolean showSecondary = false;
    @Setter
    @Getter
    private static boolean installedAsPrimary = true;


    private final TooltipMakerAPI tooltip;
    private final float parentHeight;
    private final float padding;
    private final float subHDAugmentInfoWidth;
    private final float augmentPanelWidth;
    private final float repairPanelWidth;
    private final float componentPanelWidth;
    private final float augmentPanelYOffset;
    private final AugmentApplier selectedAugment = AugmentManagerIntel.getSelectedAugment();
    private final float buttonPanelHeight = 25f;
    private final boolean isFHD;

    public RepairTab(TooltipMakerAPI tooltip, float width, float height, float padding, float subHDOffset, float buttonPanelWidth) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.subHDAugmentInfoWidth = width - buttonPanelWidth;
        this.augmentPanelWidth = width * 0.25f;
        this.componentPanelWidth = 120f;
        this.repairPanelWidth = width - augmentPanelWidth - componentPanelWidth;
        this.augmentPanelYOffset = subHDOffset;
        this.isFHD = augmentPanelWidth + repairPanelWidth + componentPanelWidth > 960;
    }

    public void render(CustomPanelAPI mainPanel) {
        if (isNull(selectedAugment)) {
            tooltip.setParaFont(Fonts.INSIGNIA_VERY_LARGE);
            tooltip.addPara("No Augment Selected", Misc.getBasePlayerColor(), padding).setAlignment(Alignment.MID);
            tooltip.setParaFontDefault();
            return;
        }

        float augmentPanelWidth = isFHD ? this.augmentPanelWidth : subHDAugmentInfoWidth + 15f;
        float repairPanelWidth = isFHD ? this.repairPanelWidth : this.augmentPanelWidth + this.repairPanelWidth;

        CustomPanelAPI augmentInfoPanel = mainPanel.createCustomPanel(augmentPanelWidth, parentHeight + augmentPanelYOffset - padding,
                                                                      null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth,
                                                                            parentHeight + augmentPanelYOffset - padding, true);
        buildAugmentPanel(mainPanel, augmentUIElement);

        CustomPanelAPI repairInfoPanel = mainPanel.createCustomPanel(repairPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI repairUIElement = repairInfoPanel.createUIElement(repairPanelWidth, parentHeight - padding, false);
        buildRepairPanel(mainPanel, repairUIElement);

        CustomPanelAPI componentInfoPanel = mainPanel.createCustomPanel(componentPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI componentUIElement = componentInfoPanel.createUIElement(componentPanelWidth, parentHeight - padding, false);
        buildComponentPanel(mainPanel, componentUIElement);

        augmentInfoPanel.addUIElement(augmentUIElement);
        repairInfoPanel.addUIElement(repairUIElement);
        componentInfoPanel.addUIElement(componentUIElement);

        tooltip.addCustom(augmentInfoPanel, 0f).getPosition()
               .setSize(augmentPanelWidth, parentHeight + augmentPanelYOffset - padding)
               .setYAlignOffset(augmentPanelYOffset);
        tooltip.addCustom(repairInfoPanel, 0f).getPosition()
               .setSize(repairPanelWidth, parentHeight - padding)
               .rightOfTop(augmentInfoPanel, isFHD ? 0f : -augmentPanelWidth)
               .setYAlignOffset(-augmentPanelYOffset);
        tooltip.addCustom(componentInfoPanel, 0f).getPosition()
               .setSize(componentPanelWidth, parentHeight - padding)
               .rightOfTop(repairInfoPanel, 0f);
    }

    private void buildAugmentPanel(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        if (isFHD) {
            CustomPanelAPI bodyPanel = mainPanel.createCustomPanel(augmentPanelWidth, parentHeight - padding, null);
            TooltipMakerAPI panelUIElement = bodyPanel.createUIElement(augmentPanelWidth, parentHeight - padding, true);

            panelUIElement.setParaFont(Fonts.INSIGNIA_LARGE);
            panelUIElement.setParaFontColor(Misc.getBasePlayerColor());
            panelUIElement.addPara(selectedAugment.getName(), 0f);
            panelUIElement.setParaFontDefault();
            panelUIElement.setParaFontColor(Misc.getTextColor());

            AugmentItemPlugin.addTechInfo(panelUIElement, selectedAugment, 6f, 0f);
            TextWithHighlights augmentDescription = selectedAugment.getDescription();
            panelUIElement.addPara(augmentDescription.getDisplayString(), 6f, Misc.getHighlightColor(), augmentDescription.getHighlights());
            bodyPanel.addUIElement(panelUIElement);

            tooltip.addCustom(bodyPanel, 0f).getPosition()
                   .setXAlignOffset(0f)
                   .setYAlignOffset(-(parentHeight - padding - tooltip.getPrev().getPosition().getHeight()));
        } else {
            tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
            tooltip.addPara(tooltip.shortenString(selectedAugment.getName(), subHDAugmentInfoWidth), Misc.getBasePlayerColor(), 0f)
                   .getPosition().setYAlignOffset(-2f);
            tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return 300f;
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
                    tooltip.setParaFontColor(Misc.getBasePlayerColor());
                    tooltip.addPara(selectedAugment.getName(), 0f);
                    tooltip.setParaFontDefault();
                    tooltip.setParaFontColor(Misc.getTextColor());

                    AugmentItemPlugin.addTechInfo(tooltip, selectedAugment, 6f, 0f);
                    TextWithHighlights augmentDescription = selectedAugment.getDescription();
                    tooltip.addPara(augmentDescription.getDisplayString(), 6f, Misc.getHighlightColor(),
                                    augmentDescription.getHighlights());
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);
        }
    }

    private void buildRepairPanel(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        addButtonHeader(mainPanel, tooltip);
        addStatTable(mainPanel, tooltip);
    }

    private void addButtonHeader(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        float repairPanelWidth = isFHD ? this.repairPanelWidth : this.augmentPanelWidth + this.repairPanelWidth;

        CustomPanelAPI buttonPanel = mainPanel.createCustomPanel(repairPanelWidth - 12f, buttonPanelHeight, null);
        TooltipMakerAPI buttonUIElement = buttonPanel.createUIElement(repairPanelWidth - 12f, buttonPanelHeight, false);

        ButtonAPI primaryButton = new ShowPrimaryButton(selectedAugment).addButton(buttonUIElement, 150f, 20f);
        primaryButton.getPosition().inTL(0f, 0f);

        UIUtils.addBox(buttonUIElement, "", null, null, 15, 20, 1, 0, null,
                       Misc.getBasePlayerColor(), new Color(0, 0, 0, 0), new GlowingBox(selectedAugment.getPrimarySlot().getColor()))
               .getPosition()
               .inTMid(0f);
        IntelButton repairAugmentButton = new RepairAugmentButton(selectedAugment);
        ButtonAPI repairButton = repairAugmentButton.addButton(buttonUIElement, 0f, 20f);
        repairAugmentButton.addTooltip(buttonUIElement);
        repairButton.getPosition().inTMid(0f).setXAlignOffset(-(repairButton.getPosition().getWidth() / 2));

        AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
        IntelButton dismantleAugmentButton = isNull(selectedAugmentInCargo) ? new DismantleAugmentButton(selectedAugment) :
                                             new DismantleAugmentButton(selectedAugmentInCargo);
        dismantleAugmentButton.addTooltip(buttonUIElement);
        ButtonAPI dismantleButton = dismantleAugmentButton.addButton(buttonUIElement, 0f, 20f);
        dismantleButton.getPosition().inTMid(0f).setXAlignOffset((dismantleButton.getPosition().getWidth() / 2));

        ButtonAPI secondaryButton = new ShowSecondaryButton(selectedAugment).addButton(buttonUIElement, 150f, 20f);
        secondaryButton.getPosition().inTR(0f, 0f);

        ButtonUtils.addSeparatorLine(buttonUIElement, repairPanelWidth - 12f, Misc.getDarkPlayerColor(), 0f).getPosition().inTMid(19f);

        buttonPanel.addUIElement(buttonUIElement).inTL(-5f, 1f);
        tooltip.addCustom(buttonPanel, 0f);
    }

    private void addStatTable(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        if (selectedAugment.getPrimaryStatMods().isEmpty() && selectedAugment.getSecondaryStatMods().isEmpty()) {
            tooltip.setParaFont(Fonts.ORBITRON_24AABOLD);
            tooltip.addPara("No Stat-Modifications", Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
            tooltip.getPrev().getPosition().setYAlignOffset(-40f);
            tooltip.setParaFontDefault();
            return;
        }

        float repairPanelWidth = isFHD ? this.repairPanelWidth : this.augmentPanelWidth + this.repairPanelWidth;

        List<StatApplier> statAppliers = showSecondary ? selectedAugment.getSecondaryStatMods() : selectedAugment.getPrimaryStatMods();
        List<StatModValue<Float, Float, Boolean, Boolean>> statModValues = showSecondary
                                                                           ? selectedAugment.getSecondaryStatValues()
                                                                           : selectedAugment.getPrimaryStatValues();

        CustomPanelAPI tablePanel = mainPanel.createCustomPanel(repairPanelWidth - 12, parentHeight - padding - buttonPanelHeight, null);
        TooltipMakerAPI tableUIElement = tablePanel.createUIElement(repairPanelWidth - 12, parentHeight - padding - buttonPanelHeight,
                                                                    true);

        float statDescriptionWidth = isFHD ? 285f : 250f;
        tableUIElement.beginTable(Misc.getBasePlayerColor(), Color.BLACK, Color.BLACK, 15f,
                                  "", statDescriptionWidth, "Current", 78f, "Next", 78f, "Repaired", 78f);

        for (int i = 0; i < statAppliers.size(); i++) {
            addRepairStatElement(tableUIElement, statModValues.get(i), statAppliers.get(i), selectedAugment);
        }

        tableUIElement.addTable("", 0, 0);
        tableUIElement.getPrev().getPosition().setXAlignOffset(-5f).setYAlignOffset(3f);
        tableUIElement.setParaFont(Fonts.ORBITRON_12);
        tableUIElement.addPara("Augment Stats", Misc.getBasePlayerColor(), 0f).getPosition().inTL(7f, 0);
        ButtonUtils.addSeparatorLine(tableUIElement, repairPanelWidth - 20f, Misc.getDarkPlayerColor(), 0f).getPosition()
                   .setYAlignOffset(-3f).setXAlignOffset(-3f);
        tablePanel.addUIElement(tableUIElement);
        tooltip.addCustom(tablePanel, 0f).getPosition().inBL(0f, 0f);
    }

    private void addRepairStatElement(TooltipMakerAPI tooltip, StatModValue<Float, Float, Boolean, Boolean> statModValue,
                                      StatApplier statApplier, AugmentApplier augment) {
        AugmentQuality curQuality = augment.getAugmentQuality();
        AugmentQuality maxQuality = augment.getInitialQuality();
        String fighterString = VoidTecUtils.checkIfFighterStat(augment) ? "(Fighter) " : "";
        String displayName = String.format("%s %s%s", VT_Strings.BULLET_CHAR, fighterString, statApplier.getDisplayName());
        String percentageSign = statApplier.isMult() ? "%" : "";

        float mult = 1f;
        if (statModValue.getsModified) {
            mult = statModValue.invertModifier
                   ? AugmentQuality.getHighestQuality().getModifier() + 1 - curQuality.getModifier()
                   : curQuality.getModifier();
        }

        int curValMin = Math.round(statModValue.minValue * mult);
        int curValMax = Math.round(statModValue.maxValue * mult);
        String curValString;

        if (curValMin != curValMax) {
            curValString = String.format("%s - %s", Math.abs(curValMin) + percentageSign, Math.abs(curValMax) + percentageSign);
        } else {
            curValString = String.format("%s", Math.abs(curValMin) + percentageSign);
        }

        mult = 1f;
        if (statModValue.getsModified) {
            mult = statModValue.invertModifier
                   ? AugmentQuality.getHighestQuality().getModifier() + 1 - curQuality.getHigherQuality().getModifier()
                   : curQuality.getHigherQuality().getModifier();
        }

        int nextValMin = Math.round(statModValue.minValue * mult);
        int nextValMax = Math.round(statModValue.maxValue * mult);
        String nextValString;

        if (nextValMin != nextValMax) {
            nextValString = String.format("%s - %s", Math.abs(nextValMin) + percentageSign, Math.abs(nextValMax) + percentageSign);
        } else {
            nextValString = String.format("%s", Math.abs(nextValMin) + percentageSign);
        }

        mult = 1f;
        if (statModValue.getsModified) {
            mult = statModValue.invertModifier
                   ? AugmentQuality.getHighestQuality().getModifier() + 1 - maxQuality.getModifier()
                   : maxQuality.getModifier();
        }
        int repValMin = Math.round(statModValue.minValue * mult);
        int repValMax = Math.round(statModValue.maxValue * mult);
        String repValString;

        if (repValMin != repValMax) {
            repValString = String.format("%s - %s", Math.abs(repValMin) + percentageSign, Math.abs(repValMax) + percentageSign);
        } else {
            repValString = String.format("%s", Math.abs(repValMin) + percentageSign);
        }

        boolean hasNegativeEffect = statApplier.hasNegativeValueAsBenefit() && curValMin >= 0
                || !statApplier.hasNegativeValueAsBenefit() && curValMin < 0;
        boolean noRepairsNeeded = curQuality.isGreaterOrEqualThen(maxQuality);

        Color curValColor;
        Color negativeColor = Misc.getNegativeHighlightColor();
        Color positiveColor = Misc.getPositiveHighlightColor();

        if (hasNegativeEffect) {
            curValColor = noRepairsNeeded ? negativeColor : negativeColor.darker();
        } else {
            curValColor = noRepairsNeeded ? positiveColor : positiveColor.darker();
        }

        Color nextValColor;
        if (noRepairsNeeded || curValMin == nextValMin && curValMax == nextValMax) {
            nextValColor = Misc.getGrayColor();
            nextValString = "---";
        } else if (hasNegativeEffect) {
            nextValColor = negativeColor;
        } else {
            nextValColor = positiveColor;
        }

        Color repValColor;
        if (noRepairsNeeded) {
            repValColor = Misc.getGrayColor();
            repValString = "---";
        } else if (hasNegativeEffect) {
            repValColor = negativeColor;
        } else {
            repValColor = positiveColor;
        }

        tooltip.addRow(Alignment.LMID, Misc.getTextColor(), displayName,
                       Alignment.MID, curValColor, curValString,
                       Alignment.MID, nextValColor, nextValString,
                       Alignment.MID, repValColor, repValString);
    }

    private void buildComponentPanel(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        addComponentHeader(mainPanel, tooltip);
        addComponentList(mainPanel, tooltip);
    }

    private void addComponentHeader(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        CustomPanelAPI headerPanel = mainPanel.createCustomPanel(componentPanelWidth, buttonPanelHeight, null);
        TooltipMakerAPI headerUIElement = headerPanel.createUIElement(componentPanelWidth, buttonPanelHeight, false);

        headerUIElement.addSectionHeading("Repair Costs", Alignment.MID, 0f).getPosition().setSize(componentPanelWidth, 20f);

        headerPanel.addUIElement(headerUIElement).inTL(0f, 0f);
        tooltip.addCustom(headerPanel, 0f).getPosition().inTL(-3f, 1f);
    }

    private void addComponentList(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        CustomPanelAPI listPanel = mainPanel.createCustomPanel(componentPanelWidth + 11f, parentHeight - padding - buttonPanelHeight, null);
        TooltipMakerAPI listUIElement = listPanel.createUIElement(componentPanelWidth + 11f, parentHeight - padding - buttonPanelHeight,
                                                                  true);
        listUIElement.beginTable(Misc.getBasePlayerColor(), Color.BLACK, Color.BLACK, 15f, "", 40f, "", 86f);
        List<CraftingComponent> componentsForRepair = AugmentPartsUtility.getComponentsForRepair(selectedAugment);
        for (CraftingComponent craftingComponent : componentsForRepair) {
            AugmentQuality partQuality = craftingComponent.getPartQuality();
            Color amountColor = AugmentPartsUtility.hasEnough(craftingComponent) ? Misc.getHighlightColor() : Misc.getGrayColor();
            Color qualityColor = isNull(partQuality) ? Misc.getTextColor() : partQuality.getColor();

            listUIElement.addRow(Alignment.RMID, amountColor, craftingComponent.getAmount() + Strings.X,
                                 Alignment.LMID, qualityColor, craftingComponent.getName());
        }

        listUIElement.addTable("", 0, -5f);
        listUIElement.addPara("Credits: %s", -9f, Misc.getHighlightColor(),
                              Misc.getDGSCredits(VoidTecUtils.calcNeededCreditsForRepair(selectedAugment))).getPosition().inTL(15f, 0f);

        listPanel.addUIElement(listUIElement).inTL(-5f, 0f);
        tooltip.addCustom(listPanel, 0f).getPosition().inBL(-8f, 0f);
    }
}
