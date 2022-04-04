package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsManager;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.*;
import de.schafunschaf.voidtec.campaign.intel.tabs.DetailsTab;
import de.schafunschaf.voidtec.campaign.intel.tabs.DisassembleTab;
import de.schafunschaf.voidtec.campaign.intel.tabs.HelpTab;
import de.schafunschaf.voidtec.campaign.intel.tabs.RepairTab;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.util.ui.BorderBox;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@RequiredArgsConstructor
public class InfoPanel {

    @Setter
    @Getter
    private static InfoTabs selectedTab = InfoTabs.DETAILS;

    private final float panelWidth;
    private final float panelHeight;
    private final float padding;

    private final float tabHeight = 24f;
    private final float tabPadding = 10f;
    private final float smallPad = 3f;
    private final float textYOffset = 2f;
    private final float buttonMargin = 16f;
    private final float panelMargin = 2f;
    private final float borderSize = 2f;
    private final float vertSeparatorHeight = tabHeight + smallPad + panelMargin * 2 - textYOffset;
    private final AugmentPartsManager partsManager = AugmentPartsManager.getInstance();
    private float textMargin = 15f;
    private int usableAugments;
    private int repairableAugments;
    private int destroyedAugments;
    private int sumAugmentParts;

    private float buttonPanelWidth;

    public void render(CustomPanelAPI mainPanel) {
        fetchAugmentNumbers();
        addTabDisplay(mainPanel);
    }

    private void addTabDisplay(CustomPanelAPI mainPanel) {
        float subFHDOffset;
        float subHDMargin;
        float subHDPadding;
        float subHDButtonOffset;
        if (panelWidth < 970f) {
            subFHDOffset = vertSeparatorHeight;
            subHDMargin = vertSeparatorHeight * 2;
            subHDPadding = (vertSeparatorHeight + borderSize) * 2;
            subHDButtonOffset = 0;
        } else {
            subFHDOffset = 0f;
            subHDMargin = vertSeparatorHeight;
            subHDPadding = vertSeparatorHeight + panelMargin + borderSize;
            subHDButtonOffset = -vertSeparatorHeight - borderSize - panelMargin / 2;
        }

        final String title = getTitleString();

        BorderBox borderBox = new BorderBox(mainPanel).width(panelWidth).height(panelHeight - 2f).titleHeight(18f)
                                                      .borderSize(borderSize).margin(panelMargin).title(title);

        // Workaround for a weird behaviour when the dismantle tab is open with an augment selected
        boolean hasAugmentSelected = !isNull(AugmentManagerIntel.getSelectedInstalledAugment())
                || !isNull(AugmentManagerIntel.getSelectedAugmentInCargo());
        if (selectedTab == InfoTabs.DISMANTLE && hasAugmentSelected) {
            borderBox.marginTop(panelMargin - 1f);
        }

        CustomPanelAPI boxBodyPanel = borderBox.getBodyPanel();
        TooltipMakerAPI boxBodyElement = boxBodyPanel.createUIElement(boxBodyPanel.getPosition().getWidth(),
                                                                      boxBodyPanel.getPosition().getHeight(),
                                                                      false);

        addInfoBoxHeadElements(mainPanel, boxBodyElement);

        boxBodyElement.addSectionHeading("", Alignment.MID, 0f).getPosition()
                      .setSize(boxBodyPanel.getPosition().getWidth() + panelMargin * 2, borderSize)
                      .inTL(-panelMargin, vertSeparatorHeight - panelMargin);
        boxBodyElement.addSpacer(0f).getPosition().inTL(0f, vertSeparatorHeight + borderSize);

        CustomPanelAPI tabButtons = addTabButtons(mainPanel);
        boxBodyElement.addCustom(tabButtons, subHDButtonOffset);

        boxBodyElement.addSpacer(0f).getPosition().inTL(0f, subHDPadding);

        float tabWidth = boxBodyPanel.getPosition().getWidth() + panelMargin;
        float tabHeight = boxBodyPanel.getPosition()
                                      .getHeight() + this.tabHeight + smallPad - panelMargin - borderSize - subHDMargin;
        float tabPadding = this.tabHeight + panelMargin;

        switch (selectedTab) {
            case DETAILS:
                new DetailsTab(boxBodyElement, tabWidth, tabHeight, tabPadding, subFHDOffset).render(mainPanel);
                break;
            case REPAIR:
                new RepairTab(boxBodyElement, tabWidth, tabHeight, tabPadding, subFHDOffset, buttonPanelWidth).render(mainPanel);
                break;
            case DISMANTLE:
                new DisassembleTab(boxBodyElement, tabWidth, tabHeight, tabPadding).render(mainPanel);
                break;
            case MANUFACTURE:
                break;
            case HELP:
                new HelpTab(boxBodyElement, tabWidth, tabHeight, tabPadding).render(mainPanel);
                break;
        }

        boxBodyPanel.addUIElement(boxBodyElement).inTL(0, 0);
        borderBox.createBorderBox().inTL(0f, this.padding + 1f);
    }

    private void addInfoBoxHeadElements(CustomPanelAPI mainPanel, TooltipMakerAPI tooltip) {
        final String creditValue = Misc.getDGSCredits(Global.getSector().getPlayerFleet().getCargo().getCredits().get());
        final String playerCreditsText = String.format("Credits: %s", creditValue);
        final String storyPoints = String.valueOf(Global.getSector().getPlayerStats().getStoryPoints());
        final String playerSPText = String.format("SP: %s", storyPoints);
        final String numUsableAugments = String.valueOf(usableAugments);
        final String augmentsInCargoText = String.format("Augments: %s", numUsableAugments);
        final String numParts = String.valueOf(sumAugmentParts);
        final String numPartsText = String.format("Parts: %s", numParts);

        CustomPanelAPI infoPanel = mainPanel.createCustomPanel(panelWidth - panelMargin * 2, vertSeparatorHeight, null);
        TooltipMakerAPI infoUIElement = infoPanel.createUIElement(panelWidth - panelMargin * 2, vertSeparatorHeight, false);

        infoUIElement.setParaFont(Fonts.INSIGNIA_LARGE);

        float sumTextWidth = infoUIElement.computeStringWidth(playerCreditsText) + infoUIElement.computeStringWidth(playerSPText)
                + infoUIElement.computeStringWidth(augmentsInCargoText) + infoUIElement.computeStringWidth(numPartsText)
                + 3 * borderSize + 8 * textMargin;
        float subFHDOffset;

        if (panelWidth < 970f) {
            subFHDOffset = (panelWidth - panelMargin * 2 - sumTextWidth) / 2;
        } else {
            subFHDOffset = 0f;
            textMargin = 5f;
        }

        //Layout: Credits | SP | Augments | Parts
        // Credits element
        UIComponentAPI creditElement = addInfoTextComponent(infoUIElement, null, null, playerCreditsText,
                                                            Misc.getHighlightColor(), creditValue);
        addVertSeparator(infoUIElement, creditElement);

        // SP element
        UIComponentAPI spElement = addInfoTextComponent(infoUIElement, creditElement, null, playerSPText, Misc.getStoryOptionColor(),
                                                        storyPoints);
        addVertSeparator(infoUIElement, spElement);

        // Augments element
        UIComponentAPI augmentElement = addInfoTextComponent(infoUIElement, spElement, null, augmentsInCargoText,
                                                             Misc.getHighlightColor(), numUsableAugments);
        addVertSeparator(infoUIElement, augmentElement);

        // Parts element
        BaseTooltipCreator partsTooltip = new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 380f;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.beginTable(Misc.getBasePlayerColor(), Color.BLACK, Color.black, 18f,
                                   "Category", 100f, "COM", 45f, "MIL", 45f, "EXP", 45f, "EXO", 45f, "DOM", 45f, "CUS", 45f);

                addComponentRow(tooltip, partsManager.getPartsOfCategory(null));
                for (SlotCategory slotCategory : SlotCategory.values) {
                    addComponentRow(tooltip, partsManager.getPartsOfCategory(slotCategory));
                }

                tooltip.addTable("", 0, 0f);
                tooltip.getPrev().getPosition().inTL(0, 0);
                ButtonUtils.addSeparatorLine(tooltip, 380f, Misc.getDarkPlayerColor(), 0f).getPosition()
                           .inTL(5f, 20f);
            }
        };

        addInfoTextComponent(infoUIElement, augmentElement, partsTooltip, numPartsText,
                             Misc.getHighlightColor(), numParts);

        infoUIElement.setParaFont(Fonts.DEFAULT_SMALL);

        infoPanel.addUIElement(infoUIElement).inTMid(0);
        tooltip.addCustom(infoPanel, 0f).getPosition().setXAlignOffset(subFHDOffset);
    }

    private void addComponentRow(TooltipMakerAPI tooltip, List<CraftingComponent> craftingComponents) {
        List<Object> columns = new ArrayList<>();

        columns.add(Alignment.LMID);
        columns.add(Misc.getTextColor());
        columns.add(craftingComponents.get(0).getName());

        for (CraftingComponent component : craftingComponents) {
            int amount = component.getAmount();
            String amountString = amount == 0 ? "---" : String.valueOf(amount);
            columns.add(Alignment.RMID);
            columns.add(component.getPartQuality().getColor());
            columns.add(amountString);
        }

        tooltip.addRow(columns.toArray(new Object[0]));
    }

    private void addVertSeparator(TooltipMakerAPI tooltip, UIComponentAPI prevComponent) {
        LabelAPI verticalSeparator = UIUtils.addVerticalSeparator(tooltip, borderSize, vertSeparatorHeight, Misc.getDarkPlayerColor());
        verticalSeparator.getPosition().rightOfMid(prevComponent, textMargin);
    }

    private UIComponentAPI addInfoTextComponent(TooltipMakerAPI tooltip, UIComponentAPI prevComponent, BaseTooltipCreator tooltipCreator,
                                                String text, Color hlColor, String highlight) {
        float stringWidth = tooltip.computeStringWidth(text);
        tooltip.addPara(text, 0f, hlColor, highlight);

        if (!isNull(tooltipCreator)) {
            tooltip.addTooltipToPrevious(tooltipCreator, TooltipMakerAPI.TooltipLocation.LEFT);
        }

        UIComponentAPI uiComponent = tooltip.getPrev();

        if (isNull(prevComponent)) {
            uiComponent.getPosition().inTL(textMargin, textYOffset)
                       .setSize(stringWidth, uiComponent.getPosition().getHeight());
        } else {
            uiComponent.getPosition()
                       .rightOfTop(prevComponent, 2 * textMargin + borderSize)
                       .setSize(stringWidth, uiComponent.getPosition().getHeight());
        }

        return uiComponent;
    }

    private void fetchAugmentNumbers() {
        usableAugments = 0;
        repairableAugments = 0;
        destroyedAugments = 0;
        sumAugmentParts = partsManager.getSumOfAllParts();

        for (AugmentCargoWrapper augmentsInCargo : AugmentManagerIntel.getAugmentsInCargo()) {
            float stackSize = augmentsInCargo.getAugmentCargoStack().getSize();
            if (augmentsInCargo.getAugment().isDestroyed()) {
                destroyedAugments += stackSize;
            } else if (augmentsInCargo.getAugment().isRepairable()) {
                repairableAugments += stackSize;
                usableAugments += stackSize;
            } else {
                usableAugments += stackSize;
            }
        }
    }

    private CustomPanelAPI addTabButtons(CustomPanelAPI mainPanel) {
        CustomPanelAPI tabButtonPanel = mainPanel.createCustomPanel(panelWidth, tabHeight, null);
        TooltipMakerAPI tabButtonElement = tabButtonPanel.createUIElement(panelWidth, tabHeight, false);

        IntelButton detailsButton = new DetailsTabButton();
        float detailsWidth = tabButtonElement.computeStringWidth(detailsButton.getName()) + buttonMargin;
        ButtonAPI detailsUIButton = detailsButton.addButton(tabButtonElement, detailsWidth, tabHeight);
        detailsButton.addTooltip(tabButtonElement);

        IntelButton repairButton = new RepairTabButton(repairableAugments);
        float repairWidth = tabButtonElement.computeStringWidth(repairButton.getName()) + buttonMargin;
        ButtonAPI repairUIButton = repairButton.addButton(tabButtonElement, repairWidth, tabHeight);
        repairButton.addTooltip(tabButtonElement);

        IntelButton dismantleButton = new DismantleTabButton(destroyedAugments);
        float dismantleWidth = tabButtonElement.computeStringWidth(dismantleButton.getName()) + buttonMargin;
        ButtonAPI dismantleUIButton = dismantleButton.addButton(tabButtonElement, dismantleWidth, tabHeight);
        dismantleButton.addTooltip(tabButtonElement);

        IntelButton manufactureButton = new ManufactureTabButton();
        float manufactureWidth = tabButtonElement.computeStringWidth(manufactureButton.getName()) + buttonMargin;
        ButtonAPI manufactureUIButton = manufactureButton.addButton(tabButtonElement, manufactureWidth, tabHeight);
        manufactureButton.addTooltip(tabButtonElement);

        IntelButton helpButton = new HelpTabButton();
        float helpWidth = tabButtonElement.computeStringWidth(helpButton.getName()) + buttonMargin;
        ButtonAPI helpUIButton = helpButton.addButton(tabButtonElement, helpWidth, tabHeight);
        helpButton.addTooltip(tabButtonElement);

        helpUIButton.getPosition().inTR(tabPadding, panelMargin);
        manufactureUIButton.getPosition().leftOfTop(helpUIButton, tabPadding);
        dismantleUIButton.getPosition().leftOfTop(manufactureUIButton, tabPadding);
        repairUIButton.getPosition().leftOfTop(dismantleUIButton, tabPadding);
        detailsUIButton.getPosition().leftOfTop(repairUIButton, tabPadding);

        tabButtonPanel.addUIElement(tabButtonElement).inTMid(0);

        this.buttonPanelWidth =
                detailsWidth + repairWidth + dismantleWidth + manufactureWidth + helpWidth + 2 * panelMargin + 5 * tabPadding;

        return tabButtonPanel;
    }

    private String getTitleString() {
        switch (selectedTab) {
            case DETAILS:
                return "Display information and install Augments";
            case REPAIR:
                return "Repair damaged Augments";
            case DISMANTLE:
                return "Disassemble Augments for spare parts";
            case MANUFACTURE:
                return "Manufacture new Augments";
            case HELP:
                return "Interface-Guide";
            default:
                return "";
        }
    }

    public enum InfoTabs {
        DETAILS,
        REPAIR,
        DISMANTLE,
        MANUFACTURE,
        HELP
    }
}
