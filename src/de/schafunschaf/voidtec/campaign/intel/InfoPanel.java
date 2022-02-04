package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.*;
import de.schafunschaf.voidtec.campaign.intel.tabs.DetailsDisplayable;
import lombok.Getter;
import lombok.Setter;

@Getter
public class InfoPanel implements DisplayablePanel {

    @Setter
    @Getter
    private static InfoTabs selectedTab = InfoTabs.DETAILS;
    private float panelHeight;
    private float panelWidth;

    @Override
    public void displayPanel(CustomPanelAPI panel, float width, float height, float padding) {
        float tabHeight = 24f;
        float tabPadding = 10f;
        float creditsXOffset = 20f;
        float smallPad = 3f;
        float mediumPad = 6f;
        float panelMargin = 2f;
        float buttonMargin = 16f;
        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);

        String creditValue = Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get());
        String playerCreditsText = String.format("Credits: %s", creditValue);
        String storyPoints = String.valueOf(Global.getSector().getPlayerStats().getStoryPoints());
        String playerSPText = String.format("Storypoints: %s", storyPoints);
        String numAugmentsInCargo = String.valueOf(AugmentManagerIntel.getAugmentsInCargo().size());
        String augmentsInCargoText = String.format("Augments: %s", numAugmentsInCargo);

        uiElement.addSectionHeading("Information", Alignment.MID, 0f);
        UIComponentAPI headerComponent = uiElement.getPrev();

        float boxHeight = 130f;
        uiElement.addSectionHeading("", Alignment.MID, 0f).getPosition().setSize(panelMargin, boxHeight)
                 .belowRight(headerComponent, 0f)
                 .setXAlignOffset(5f);

        uiElement.addSectionHeading("", Alignment.MID, 0f).getPosition().setSize(panelMargin, boxHeight)
                 .belowLeft(headerComponent, 0f)
                 .setXAlignOffset(-5f);

        uiElement.addSectionHeading("", Alignment.MID, mediumPad).getPosition().setSize(width - panelMargin * 2, 2f)
                 .belowLeft(headerComponent, 0f)
                 .setXAlignOffset(-5f + panelMargin)
                 .setYAlignOffset(-30f);

        uiElement.addSectionHeading("", Alignment.MID, mediumPad).getPosition().setSize(width - panelMargin * 2, panelMargin)
                 .belowLeft(headerComponent, 0f)
                 .setXAlignOffset(-5f + panelMargin)
                 .setYAlignOffset(-boxHeight + panelMargin);

        uiElement.addSpacer(0f).getPosition().belowLeft(headerComponent, 0f);

        uiElement.setParaFont(Fonts.INSIGNIA_LARGE);
        uiElement.addPara(playerCreditsText, smallPad, Misc.getHighlightColor(), creditValue);
        UIComponentAPI creditElement = uiElement.getPrev();
        creditElement.getPosition().setXAlignOffset(panelMargin);
        uiElement.addPara(playerSPText, 0f, Misc.getStoryOptionColor(), storyPoints);
        UIComponentAPI spComponent = uiElement.getPrev();
        spComponent.getPosition().setYAlignOffset(spComponent.getPosition().getHeight());
        spComponent.getPosition().setXAlignOffset(uiElement.computeStringWidth(playerCreditsText) + creditsXOffset);
        uiElement.addPara(augmentsInCargoText, 0f, Misc.getHighlightColor(), numAugmentsInCargo);
        UIComponentAPI augmentsComponent = uiElement.getPrev();
        augmentsComponent.getPosition().setYAlignOffset(augmentsComponent.getPosition().getHeight());
        augmentsComponent.getPosition().setXAlignOffset(uiElement.computeStringWidth(playerSPText) + creditsXOffset);
        uiElement.setParaFont(Fonts.DEFAULT_SMALL);

        IntelButton detailsButton = new DetailsTabButton();
        ButtonAPI detailsUIButton = detailsButton.createButton(uiElement,
                                                               uiElement.computeStringWidth(
                                                                       detailsButton.getName()) + buttonMargin,
                                                               tabHeight);
        IntelButton repairButton = new RepairTabButton();
        ButtonAPI repairUIButton = repairButton.createButton(uiElement,
                                                             uiElement.computeStringWidth(
                                                                     repairButton.getName()) + buttonMargin,
                                                             tabHeight);
        IntelButton manufactureButton = new ManufactureTabButton();
        ButtonAPI manufactureUIButton = manufactureButton.createButton(uiElement,
                                                                       uiElement.computeStringWidth(
                                                                               manufactureButton.getName()) + buttonMargin,
                                                                       tabHeight);
        IntelButton disassembleButton = new DisassembleTabButton();
        ButtonAPI disassembleUIButton = disassembleButton.createButton(uiElement,
                                                                       uiElement.computeStringWidth(
                                                                               disassembleButton.getName()) + buttonMargin,
                                                                       tabHeight);

        disassembleUIButton.getPosition().inTR(smallPad + panelMargin, headerComponent.getPosition().getHeight() + smallPad);
        manufactureUIButton.getPosition().leftOfTop(disassembleUIButton, tabPadding);
        repairUIButton.getPosition().leftOfTop(manufactureUIButton, tabPadding);
        detailsUIButton.getPosition().leftOfTop(repairUIButton, tabPadding);

        uiElement.addSpacer(0f).getPosition().belowLeft(creditElement, 0f);

        //        uiElement.addSectionHeading("", Alignment.MID, mediumPad).getPosition().setSize(width - panelMargin, 2f)
        //                 .setXAlignOffset(-5f);
        //
        //        uiElement.addSectionHeading("", Alignment.MID, mediumPad).getPosition().setSize(width - panelMargin, panelMargin)
        //                 .setXAlignOffset(-5f)
        //                 .setYAlignOffset(-139f);

        panel.addUIElement(uiElement).setYAlignOffset(-padding - headerComponent.getPosition().getHeight() - mediumPad);

        switch (selectedTab) {
            case DETAILS:
                new DetailsDisplayable().displayPanel(panel, width, height, padding);
                break;
            case REPAIR:
                break;
            case DISASSEMBLE:
                break;
            case MANUFACTURE:
                break;
        }
    }

    @Override
    public float getPanelWidth() {
        return 0;
    }

    @Override
    public float getPanelHeight() {
        return 0;
    }

    public enum InfoTabs {
        DETAILS,
        REPAIR,
        DISASSEMBLE,
        MANUFACTURE
    }
}
