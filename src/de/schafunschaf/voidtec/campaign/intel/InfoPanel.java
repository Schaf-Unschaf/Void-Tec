package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.*;
import de.schafunschaf.voidtec.campaign.intel.tabs.DetailsTab;
import de.schafunschaf.voidtec.campaign.intel.tabs.RepairTab;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.util.ui.BorderBox;
import de.schafunschaf.voidtec.util.ui.UiUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
public class InfoPanel {

    @Setter
    @Getter
    private static InfoTabs selectedTab = InfoTabs.DETAILS;

    public void displayPanel(CustomPanelAPI panel, float width, float height, float padding) {
        float tabHeight = 24f;
        float tabPadding = 10f;
        float smallPad = 3f;
        float mediumPad = 6f;
        float textMargin = 5f;
        float textYOffset = 2f;
        float buttonMargin = 16f;

        float panelMargin = 2f;
        float borderSize = 2f;

        int usableAugments = 0;
        int repairableAugments = 0;
        int destroyedAugments = 0;
        for (AugmentCargoWrapper augmentsInCargo : AugmentManagerIntel.getAugmentsInCargo()) {
            float stackSize = augmentsInCargo.getAugmentCargoStack().getSize();
            if (augmentsInCargo.getAugment().isDestroyed()) {
                destroyedAugments += stackSize;
            } else if (augmentsInCargo.getAugment().isRepairable()) {
                repairableAugments += stackSize;
            } else {
                usableAugments += stackSize;
            }
        }

        String creditValue = Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get());
        String playerCreditsText = String.format("Credits: %s", creditValue);
        String storyPoints = String.valueOf(Global.getSector().getPlayerStats().getStoryPoints());
        String playerSPText = String.format("Storypoints: %s", storyPoints);
        String numUsableAugments = String.valueOf(usableAugments);
        String augmentsInCargoText = String.format("Augments: %s", numUsableAugments);

        BorderBox borderBox = new BorderBox(panel).width(width).height(height)
                                                  .borderSize(borderSize).margin(panelMargin).title("Information");
        CustomPanelAPI boxBodyPanel = borderBox.getBodyPanel();
        TooltipMakerAPI boxBodyElement = boxBodyPanel.createUIElement(boxBodyPanel.getPosition().getWidth(),
                                                                      boxBodyPanel.getPosition().getHeight(),
                                                                      false);

        // Add Info-Box header elements
        boxBodyElement.setParaFont(Fonts.INSIGNIA_LARGE);
        float vertSeparatorHeight = tabHeight + smallPad + panelMargin * 2 - textYOffset;

        // Credit element
        float playerCreditsStringWidth = boxBodyElement.computeStringWidth(playerCreditsText);
        boxBodyElement.addPara(playerCreditsText, smallPad, Misc.getHighlightColor(), creditValue);
        UIComponentAPI creditComponent = boxBodyElement.getPrev();
        creditComponent.getPosition().inTL(textMargin, textYOffset)
                       .setSize(playerCreditsStringWidth, creditComponent.getPosition().getHeight());

        UiUtils.addVerticalSeparator(vertSeparatorHeight, borderSize,
                                     playerCreditsStringWidth + textMargin,
                                     creditComponent.getPosition().getHeight() + panelMargin + textYOffset,
                                     boxBodyElement);

        // SP element
        float playerSPStringWidth = boxBodyElement.computeStringWidth(playerSPText);
        boxBodyElement.addPara(playerSPText, 0f, Misc.getStoryOptionColor(), storyPoints);
        UIComponentAPI spComponent = boxBodyElement.getPrev();
        spComponent.getPosition().rightOfTop(creditComponent, 2 * textMargin + borderSize)
                   .setSize(playerSPStringWidth, spComponent.getPosition().getHeight());

        UiUtils.addVerticalSeparator(vertSeparatorHeight, borderSize,
                                     playerSPStringWidth + textMargin,
                                     spComponent.getPosition().getHeight() + panelMargin + textYOffset,
                                     boxBodyElement);

        // Augments element
        float augmentsStringWidth = boxBodyElement.computeStringWidth(augmentsInCargoText);
        boxBodyElement.addPara(augmentsInCargoText, 0f, Misc.getHighlightColor(), numUsableAugments);
        UIComponentAPI augmentsComponent = boxBodyElement.getPrev();
        augmentsComponent.getPosition().rightOfTop(spComponent, 2 * textMargin + borderSize);

        UiUtils.addVerticalSeparator(vertSeparatorHeight, borderSize,
                                     augmentsStringWidth + textMargin,
                                     augmentsComponent.getPosition().getHeight() + panelMargin + textYOffset,
                                     boxBodyElement);

        boxBodyElement.setParaFont(Fonts.DEFAULT_SMALL);

        // Add buttons
        IntelButton detailsButton = new DetailsTabButton();
        ButtonAPI detailsUIButton = detailsButton.createButton(boxBodyElement,
                                                               boxBodyElement.computeStringWidth(
                                                                       detailsButton.getName()) + buttonMargin,
                                                               tabHeight);
        IntelButton repairButton = new RepairTabButton(repairableAugments);
        ButtonAPI repairUIButton = repairButton.createButton(boxBodyElement,
                                                             boxBodyElement.computeStringWidth(
                                                                     repairButton.getName()) + buttonMargin,
                                                             tabHeight);
        IntelButton dismantleButton = new DismantleTabButton(destroyedAugments);
        ButtonAPI dismantleUIButton = dismantleButton.createButton(boxBodyElement,
                                                                   boxBodyElement.computeStringWidth(
                                                                           dismantleButton.getName()) + buttonMargin,
                                                                   tabHeight);
        IntelButton manufactureButton = new ManufactureTabButton();
        ButtonAPI manufactureUIButton = manufactureButton.createButton(boxBodyElement,
                                                                       boxBodyElement.computeStringWidth(
                                                                               manufactureButton.getName()) + buttonMargin,
                                                                       tabHeight);

        manufactureUIButton.getPosition().inTR(0f, 0f);
        dismantleUIButton.getPosition().leftOfTop(manufactureUIButton, tabPadding);
        repairUIButton.getPosition().leftOfTop(dismantleUIButton, tabPadding);
        detailsUIButton.getPosition().leftOfTop(repairUIButton, tabPadding);

        boxBodyElement.addSectionHeading("", Alignment.MID, 0f).getPosition()
                      .setSize(boxBodyPanel.getPosition().getWidth() + panelMargin * 2, borderSize)
                      .inTL(-panelMargin, vertSeparatorHeight - panelMargin);

        boxBodyElement.addSpacer(0f).getPosition().inTL(0f, vertSeparatorHeight + borderSize);

        switch (selectedTab) {
            case DETAILS:
                new DetailsTab(boxBodyElement, boxBodyPanel.getPosition().getWidth() + panelMargin,
                               boxBodyPanel.getPosition().getHeight() + tabHeight + smallPad - panelMargin - borderSize,
                               tabHeight + vertSeparatorHeight + panelMargin).render();
                break;
            case REPAIR:
                new RepairTab(boxBodyElement, boxBodyPanel.getPosition().getWidth() + panelMargin,
                              boxBodyPanel.getPosition().getHeight() + tabHeight + smallPad - panelMargin - borderSize,
                              tabHeight + vertSeparatorHeight + panelMargin).render();
                break;
            case DISMANTLE:
                break;
            case MANUFACTURE:
                break;
        }
        boxBodyPanel.addUIElement(boxBodyElement).inTL(0, 0);
        borderBox.createBorderBox().inTL(0f, padding + mediumPad);
    }

    public enum InfoTabs {
        DETAILS,
        REPAIR,
        DISMANTLE,
        MANUFACTURE
    }
}
