package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair.RepairAugmentButton;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class RepairTab {

    private final TooltipMakerAPI tooltip;
    private final float parentHeight;
    private final float padding;
    private final float augmentPanelWidth;
    private final float repairPanelWidth;
    private final float panelHeaderHeight = 20f;

    private final AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
    private final boolean augmentSelected = !isNull(selectedAugmentInCargo);

    public RepairTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.augmentPanelWidth = width * 0.25f;
        this.repairPanelWidth = width - augmentPanelWidth;
    }

    public void render() {
        CustomPanelAPI augmentInfoPanel = Global.getSettings().createCustom(augmentPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth, parentHeight - padding, true);
        CustomPanelAPI repairInfoPanel = Global.getSettings().createCustom(repairPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI repairUIElement = repairInfoPanel.createUIElement(repairPanelWidth, parentHeight - padding, true);

        buildAugmentPanel(augmentUIElement);
        buildRepairPanel(repairUIElement);

        augmentInfoPanel.addUIElement(augmentUIElement);
        repairInfoPanel.addUIElement(repairUIElement);

        tooltip.addCustom(augmentInfoPanel, 0f).getPosition()
               .setSize(augmentPanelWidth, parentHeight - padding);
        tooltip.addCustom(repairInfoPanel, 0f).getPosition()
               .setSize(repairPanelWidth, parentHeight - padding)
               .rightOfTop(augmentInfoPanel, 0f);
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

    private void buildRepairPanel(TooltipMakerAPI tooltip) {
        if (!isNull(selectedAugmentInCargo)) {
            new RepairAugmentButton(selectedAugmentInCargo.getAugment()).createButton(tooltip, 0f, 20f);
        }
    }

    private boolean canBeRepaired() {
        return true;
    }
}
