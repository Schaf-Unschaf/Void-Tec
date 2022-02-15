package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.CargoPanel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
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
    private final float panelHeaderHeight = 20f;

    private final AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();

    public DetailsTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.augmentPanelWidth = width * 0.25f;
        this.primaryPanelWidth = width * 0.375f;
        this.secondaryPanelWidth = width * 0.375f;

        CargoPanel.showDestroyedAugments = false;
        CargoPanel.showOnlyRepairable = false;
    }

    public void render() {
        CustomPanelAPI augmentInfoPanel = Global.getSettings()
                                                .createCustom(augmentPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth, parentHeight - padding, false);

        CustomPanelAPI primaryStatPanel = Global.getSettings()
                                                .createCustom(primaryPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI primaryStatUIElement = primaryStatPanel.createUIElement(primaryPanelWidth, parentHeight - padding, false);

        CustomPanelAPI secondaryStatPanel = Global.getSettings()
                                                  .createCustom(secondaryPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI secondaryStatUIElement = secondaryStatPanel.createUIElement(secondaryPanelWidth, parentHeight - padding, false);

        buildAugmentPanel(augmentUIElement);
        buildStatPanel(primaryStatUIElement, primaryPanelWidth, true);
        buildStatPanel(secondaryStatUIElement, secondaryPanelWidth, false);

        augmentInfoPanel.addUIElement(augmentUIElement);
        primaryStatPanel.addUIElement(primaryStatUIElement);
        secondaryStatPanel.addUIElement(secondaryStatUIElement);

        tooltip.addCustom(augmentInfoPanel, 0f).getPosition()
               .setSize(augmentPanelWidth, parentHeight - padding);
        tooltip.addCustom(primaryStatPanel, 0f).getPosition()
               .setSize(primaryPanelWidth, parentHeight - padding)
               .rightOfTop(augmentInfoPanel, 0f);
        tooltip.addCustom(secondaryStatPanel, 0f).getPosition()
               .setSize(secondaryPanelWidth, parentHeight - padding)
               .rightOfTop(primaryStatPanel, 0f);
    }

    private void buildAugmentPanel(TooltipMakerAPI tooltip) {
        CustomPanelAPI headerPanel = Global.getSettings().createCustom(augmentPanelWidth, panelHeaderHeight, null);
        TooltipMakerAPI headerUIElement = headerPanel.createUIElement(augmentPanelWidth, panelHeaderHeight, false);

        boolean hasAugmentSelected = !isNull(selectedAugmentInCargo);

        headerUIElement.setParaFont(Fonts.ORBITRON_12);
        headerUIElement.addPara(hasAugmentSelected ? "Selected Augment" : "No Augment Selected", Misc.getBasePlayerColor(), 0f)
                       .setAlignment(Alignment.MID);
        ButtonUtils.addSeparatorLine(headerUIElement, augmentPanelWidth - 5f, Misc.getDarkPlayerColor(), 2f)
                   .getPosition()
                   .setXAlignOffset(-5f);

        headerPanel.addUIElement(headerUIElement);

        tooltip.addCustom(headerPanel, 0f).getPosition()
               .setXAlignOffset(0f); // Needed or it will apply the vanilla 5f border offset

        if (hasAugmentSelected) {
            CustomPanelAPI bodyPanel = Global.getSettings().createCustom(augmentPanelWidth, panelHeaderHeight, null);
            TooltipMakerAPI bodyUIElement = bodyPanel.createUIElement(augmentPanelWidth, parentHeight - padding - panelHeaderHeight, true);

            AugmentApplier augment = selectedAugmentInCargo.getAugment();

            bodyUIElement.setParaFont(Fonts.INSIGNIA_LARGE);
            bodyUIElement.addPara(augment.getName(), 0f);
            bodyUIElement.setParaFontDefault();

            ((AugmentItemPlugin) selectedAugmentInCargo.getAugmentCargoStack().getPlugin()).addTechInfo(bodyUIElement, 6f, 0f);
            TextWithHighlights augmentDescription = augment.getDescription();
            bodyUIElement.addPara(augmentDescription.getDisplayString(), 6f, Misc.getHighlightColor(), augmentDescription.getHighlights());
            bodyPanel.addUIElement(bodyUIElement);

            tooltip.addCustom(bodyPanel, 0f).getPosition()
                   .setYAlignOffset(-(parentHeight - padding - tooltip.getPrev().getPosition().getHeight() - panelHeaderHeight));
        }
    }

    private void buildStatPanel(TooltipMakerAPI tooltip, float panelWidth, boolean isPrimary) {
        buildStatHeader(tooltip, panelWidth, isPrimary);

        if (!isNull(selectedAugmentInCargo)) {
            CustomPanelAPI bodyPanel = Global.getSettings().createCustom(panelWidth, panelHeaderHeight, null);
            TooltipMakerAPI bodyUIElement = bodyPanel.createUIElement(panelWidth, parentHeight - padding - panelHeaderHeight, true);

            AugmentApplier augment = selectedAugmentInCargo.getAugment();

            augment.generateStatDescription(bodyUIElement, 0, true, Misc.getBasePlayerColor());

            bodyPanel.addUIElement(bodyUIElement);
            tooltip.addCustom(bodyPanel, 0f).getPosition()
                   .setYAlignOffset(-(parentHeight - padding - tooltip.getPrev().getPosition().getHeight() - panelHeaderHeight));
        }
    }

    private void buildStatHeader(TooltipMakerAPI tooltip, float width, boolean isPrimary) {
        CustomPanelAPI headerPanel = Global.getSettings().createCustom(width, panelHeaderHeight, null);
        TooltipMakerAPI headerUIElement = headerPanel.createUIElement(width, panelHeaderHeight, false);

        boolean hasAugmentSelected = !isNull(selectedAugmentInCargo);

        headerUIElement.setParaFont(Fonts.ORBITRON_12);
        headerUIElement.setParaFontColor(Misc.getBasePlayerColor());

        if (hasAugmentSelected) {
            AugmentApplier augment = selectedAugmentInCargo.getAugment();

            String headerText;
            StringBuilder hlTokens = new StringBuilder();
            List<Color> hlColors = new ArrayList<>();
            List<String> hlStrings = new ArrayList<>();

            if (isPrimary) {
                hlColors.add(augment.getPrimarySlot().getColor());
                hlStrings.add(augment.getPrimarySlot().getName());
                headerText = "Slot:";
                hlTokens = new StringBuilder(" %s");
            } else {
                List<SlotCategory> secondarySlots = augment.getSecondarySlots();

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
        } else {
            headerUIElement.addPara("-", Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
        }

        ButtonUtils.addSeparatorLine(headerUIElement, width - 5f, Misc.getDarkPlayerColor(), 2f)
                   .getPosition()
                   .setXAlignOffset(-5f);

        headerPanel.addUIElement(headerUIElement);
        tooltip.addCustom(headerPanel, 0f).getPosition()
               .setXAlignOffset(0f); // Needed or it will apply the vanilla 5f border offset
    }
}
