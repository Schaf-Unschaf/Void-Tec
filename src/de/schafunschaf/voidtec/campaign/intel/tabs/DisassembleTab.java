package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.TextWithHighlights;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class DisassembleTab {

    private final TooltipMakerAPI tooltip;
    private final float parentHeight;
    private final float padding;
    private final float augmentPanelWidth;
    private final float disassemblePanelWidth;
    private final AugmentApplier selectedAugment = AugmentManagerIntel.getSelectedAugment();
    public DisassembleTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.augmentPanelWidth = width * 0.25f;
        this.disassemblePanelWidth = width - augmentPanelWidth;
    }

    public void render(CustomPanelAPI mainPanel) {
        if (isNull(selectedAugment)) {
            tooltip.setParaFont(Fonts.INSIGNIA_VERY_LARGE);
            tooltip.addPara("No Augment Selected", Misc.getBasePlayerColor(), padding).setAlignment(Alignment.MID);
            tooltip.setParaFontDefault();
            return;
        }

        CustomPanelAPI augmentInfoPanel = mainPanel.createCustomPanel(augmentPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth, parentHeight - padding, true);
        CustomPanelAPI disassemblePanel = mainPanel.createCustomPanel(disassemblePanelWidth, parentHeight - padding, null);
        TooltipMakerAPI disassembleUIElement = disassemblePanel.createUIElement(disassemblePanelWidth, parentHeight - padding, true);

        buildAugmentPanel(mainPanel, augmentUIElement);
        buildDisassemblePanel(mainPanel, disassembleUIElement);

        augmentInfoPanel.addUIElement(augmentUIElement);
        disassemblePanel.addUIElement(disassembleUIElement);

        tooltip.addCustom(augmentInfoPanel, 0f).getPosition()
               .setSize(augmentPanelWidth, parentHeight - padding);
        tooltip.addCustom(disassemblePanel, 0f).getPosition()
               .setSize(disassemblePanelWidth, parentHeight - padding)
               .rightOfTop(augmentInfoPanel, 0f);
    }

    private void buildAugmentPanel(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
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
    }

    private void buildDisassemblePanel(CustomPanelAPI mainPanel, TooltipMakerAPI disassembleUIElement) {
        addHeader(mainPanel, disassembleUIElement);
        addDisassembleResultInfo(mainPanel, tooltip);
    }

    private void addHeader(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        float headerHeight = 25f;

        CustomPanelAPI headerPanel = mainPanel.createCustomPanel(disassemblePanelWidth - 12f, headerHeight, null);
        TooltipMakerAPI headerUIElement = headerPanel.createUIElement(disassemblePanelWidth - 12f, headerHeight, true);

        //        TimerBar disassembleTimer = TimerBar.getTimer("disassembleTimer");
        //        if (isNull(disassembleTimer)) {
        //            disassembleTimer = new TimerBar("disassembleTimer", 10f, 300f, 24f, 1f, Misc.getTextColor(),
        //                                            Misc.getDarkPlayerColor().darker(), Misc.getBasePlayerColor(), 0f, Alignment.RMID);
        //        }

        //        disassembleTimer.addStartButton("Start");
        //        disassembleTimer.renderTimer(headerUIElement, mainPanel);

        headerPanel.addUIElement(headerUIElement).inTL(0, 0);
        tooltip.addCustom(headerPanel, 0f);
    }

    private void addDisassembleResultInfo(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
    }
}
