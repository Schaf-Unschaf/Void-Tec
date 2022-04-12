package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class DetailsTab {

    private final TooltipMakerAPI tooltip;
    private final float parentHeight;
    private final float padding;
    private final float augmentPanelWidth;
    private final float primaryPanelWidth;
    private final float secondaryPanelWidth;
    private final float augmentPanelYOffset;
    private final float panelHeaderHeight = 20f;

    private final AugmentApplier selectedAugment = AugmentManagerIntel.getSelectedAugment();
    private final AugmentApplier selectedInstalledAugment = AugmentManagerIntel.getSelectedInstalledAugment();

    public DetailsTab(TooltipMakerAPI tooltip, float width, float height, float padding, float subHDOffset) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.augmentPanelWidth = width * 0.25f;
        this.primaryPanelWidth = width * 0.375f;
        this.secondaryPanelWidth = width * 0.375f;
        this.augmentPanelYOffset = subHDOffset;
    }

    public void render(CustomPanelAPI mainPanel) {
        boolean hasAugmentSelected = !isNull(selectedAugment);

        if (!hasAugmentSelected) {
            tooltip.setParaFont(Fonts.INSIGNIA_VERY_LARGE);
            tooltip.addPara("No Augment Selected", Misc.getBasePlayerColor(), padding).setAlignment(Alignment.MID);
            tooltip.setParaFontDefault();
            return;
        }

        CustomPanelAPI augmentInfoPanel = mainPanel.createCustomPanel(augmentPanelWidth, parentHeight + augmentPanelYOffset - padding,
                                                                      null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth, parentHeight + augmentPanelYOffset - padding,
                                                                            false);

        CustomPanelAPI primaryStatPanel = mainPanel.createCustomPanel(primaryPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI primaryStatUIElement = primaryStatPanel.createUIElement(primaryPanelWidth, parentHeight - padding, false);

        CustomPanelAPI secondaryStatPanel = mainPanel.createCustomPanel(secondaryPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI secondaryStatUIElement = secondaryStatPanel.createUIElement(secondaryPanelWidth, parentHeight - padding, false);

        buildAugmentPanel(mainPanel, augmentUIElement);
        augmentInfoPanel.addUIElement(augmentUIElement);
        tooltip.addCustom(augmentInfoPanel, 0f).getPosition()
               .setSize(augmentPanelWidth, parentHeight + augmentPanelYOffset - padding);

        if (!isNull(selectedInstalledAugment)) {
            buildStatPanel(primaryStatUIElement, primaryPanelWidth + secondaryPanelWidth, true);
            primaryStatPanel.addUIElement(primaryStatUIElement);
            tooltip.addCustom(primaryStatPanel, 0f).getPosition()
                   .setSize(primaryPanelWidth, parentHeight - padding)
                   .rightOfTop(augmentInfoPanel, 0f);
        } else {
            buildStatPanel(primaryStatUIElement, primaryPanelWidth, true);
            buildStatPanel(secondaryStatUIElement, secondaryPanelWidth, false);
            primaryStatPanel.addUIElement(primaryStatUIElement);
            secondaryStatPanel.addUIElement(secondaryStatUIElement);
            tooltip.addCustom(primaryStatPanel, 0f).getPosition()
                   .setSize(primaryPanelWidth, parentHeight - padding)
                   .rightOfTop(augmentInfoPanel, 0f);
            tooltip.addCustom(secondaryStatPanel, 0f).getPosition()
                   .setSize(secondaryPanelWidth, parentHeight - padding)
                   .rightOfTop(primaryStatPanel, 0f);
        }
    }

    private void buildAugmentPanel(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        CustomPanelAPI bodyPanel = mainPanel.createCustomPanel(augmentPanelWidth, panelHeaderHeight, null);
        TooltipMakerAPI panelUIElement = bodyPanel.createUIElement(augmentPanelWidth, parentHeight + augmentPanelYOffset - padding, true);

        panelUIElement.setParaFont(Fonts.INSIGNIA_LARGE);
        panelUIElement.setParaFontColor(Misc.getBasePlayerColor());
        panelUIElement.addPara(selectedAugment.getName(), 0f);
        panelUIElement.setParaFontDefault();
        panelUIElement.setParaFontColor(Misc.getTextColor());

        AugmentItemPlugin.addTechInfo(panelUIElement, selectedAugment, 6f, 0f);
        TextWithHighlights augmentDescription = selectedAugment.getDescription();
        if (!isNull(selectedAugment.getRightClickAction()) && selectedAugment.getRightClickAction().getActionObject() instanceof Color) {
            augmentDescription.setHlColor(((Color) selectedAugment.getRightClickAction().getActionObject()));
        }
        panelUIElement.addPara(augmentDescription.getDisplayString(), 6f, augmentDescription.getHlColor(),
                               augmentDescription.getHighlights());
        bodyPanel.addUIElement(panelUIElement);

        tooltip.addCustom(bodyPanel, 0f).getPosition()
               .setXAlignOffset(0f)
               .setYAlignOffset(-(parentHeight - padding - tooltip.getPrev().getPosition().getHeight()));
    }

    private void buildStatPanel(TooltipMakerAPI tooltip, float panelWidth, boolean isPrimary) {
        buildStatHeader(tooltip, panelWidth, isPrimary);

        if (!isNull(selectedAugment)) {
            CustomPanelAPI bodyPanel = Global.getSettings().createCustom(panelWidth, panelHeaderHeight, null);
            TooltipMakerAPI bodyUIElement = bodyPanel.createUIElement(panelWidth, parentHeight - padding - panelHeaderHeight, true);

            if (isNull(selectedInstalledAugment)) {
                selectedAugment.generateStatDescription(bodyUIElement, 0, isPrimary, Misc.getBasePlayerColor());
            } else {
                for (FleetMemberAPI memberAPI : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
                    if (memberAPI.getId().equals(selectedAugment.getInstalledSlot().getHullModManager().getFleetMemberID())) {
                        SlotCategory slotCategory = selectedAugment.getInstalledSlot().getSlotCategory();
                        selectedAugment.generateTooltip(memberAPI.getStats(), VoidTecEngineeringSuite.HULL_MOD_ID, bodyUIElement,
                                                        panelWidth, slotCategory, slotCategory.getColor());
                        break;
                    }
                }
            }
            bodyPanel.addUIElement(bodyUIElement);
            tooltip.addCustom(bodyPanel, 0f).getPosition()
                   .setYAlignOffset(-(parentHeight - padding - tooltip.getPrev().getPosition().getHeight() - panelHeaderHeight));
        }
    }

    private void buildStatHeader(TooltipMakerAPI tooltip, float width, boolean isPrimary) {
        CustomPanelAPI headerPanel = Global.getSettings().createCustom(width, panelHeaderHeight, null);
        TooltipMakerAPI headerUIElement = headerPanel.createUIElement(width, panelHeaderHeight, false);

        headerUIElement.setParaFont(Fonts.ORBITRON_12);
        headerUIElement.setParaFontColor(Misc.getBasePlayerColor());

        if (isNull(selectedInstalledAugment) && !isNull(selectedAugment)) {
            String headerText;
            StringBuilder hlTokens = new StringBuilder();
            List<Color> hlColors = new ArrayList<>();
            List<String> hlStrings = new ArrayList<>();

            if (isPrimary) {
                hlColors.add(selectedAugment.getPrimarySlot().getColor());
                hlStrings.add(selectedAugment.getPrimarySlot().getName());
                headerText = "Slot:";
                hlTokens = new StringBuilder(" %s");
            } else {
                List<SlotCategory> secondarySlots = selectedAugment.getSecondarySlots();

                if (secondarySlots.isEmpty()) {
                    headerText = "-";
                } else {
                    headerText = FormattingTools.singularOrPlural(secondarySlots.size(), "Slot") + ":";

                    for (Iterator<SlotCategory> iterator = secondarySlots.iterator(); iterator.hasNext(); ) {
                        SlotCategory slot = iterator.next();
                        hlColors.add(slot.getColor());
                        hlStrings.add(slot.getName());
                        hlTokens.append(" %s");
                        if (iterator.hasNext()) {
                            hlTokens.append(",");
                        }
                    }
                }
            }

            String outputString = headerText + hlTokens;

            headerUIElement.addPara(outputString, 0f, hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]))
                           .setAlignment(Alignment.MID);

            ButtonUtils.addSeparatorLine(headerUIElement, width - 5f, Misc.getDarkPlayerColor(), 2f)
                       .getPosition()
                       .setXAlignOffset(-5f);
        } else if (!isNull(selectedInstalledAugment)) {
            SlotCategory slotCategory = selectedInstalledAugment.getInstalledSlot().getSlotCategory();
            headerUIElement.addPara(String.format("Installed in Slot: %s", slotCategory.getName()), 0f, slotCategory.getColor(),
                                    slotCategory.getName()).setAlignment(Alignment.MID);

            ButtonUtils.addSeparatorLine(headerUIElement, width - 5f, Misc.getDarkPlayerColor(), 2f)
                       .getPosition()
                       .setXAlignOffset(-5f);
        }

        headerPanel.addUIElement(headerUIElement);
        tooltip.addCustom(headerPanel, 0f).getPosition()
               .setXAlignOffset(0f); // Needed or it will apply the vanilla 5f border offset
    }
}
