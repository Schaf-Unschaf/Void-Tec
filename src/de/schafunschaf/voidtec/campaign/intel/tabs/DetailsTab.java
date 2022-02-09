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
import de.schafunschaf.voidtec.helper.SlotCategoryList;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class DetailsTab {

    private final TooltipMakerAPI tooltip;
    private final float parentWidth;
    private final float parentHeight;
    private final float padding;
    private final float augmentPanelWidth;
    private final float primaryPanelWidth;
    private final float secondaryPanelWidth;

    private final AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
    private final boolean augmentSelected = !isNull(selectedAugmentInCargo);

    public DetailsTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentWidth = width;
        this.parentHeight = height;
        this.padding = padding;
        float panelWidthUnit = width / 10f;
        this.augmentPanelWidth = panelWidthUnit * 2.5f;
        this.primaryPanelWidth = panelWidthUnit * 3.75f;
        this.secondaryPanelWidth = panelWidthUnit * 3.75f;

        CargoPanel.showDestroyedAugments = false;
        CargoPanel.showOnlyRepairable = false;
    }

    public void render() {
        CustomPanelAPI augmentInfoPanel = Global.getSettings()
                                                .createCustom(augmentPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth, parentHeight - padding, true);

        CustomPanelAPI primaryStatPanel = Global.getSettings()
                                                .createCustom(primaryPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI primaryStatUIElement = primaryStatPanel.createUIElement(primaryPanelWidth, parentHeight - padding, true);

        CustomPanelAPI secondaryStatPanel = Global.getSettings()
                                                  .createCustom(secondaryPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI secondaryStatUIElement = secondaryStatPanel.createUIElement(secondaryPanelWidth, parentHeight - padding, true);

        addAugmentInfo(augmentUIElement);
        addPrimaryStatSection(primaryStatUIElement);
        addSecondaryStatSection(secondaryStatUIElement);

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

    private void addAugmentInfo(TooltipMakerAPI tooltip) {
        if (!isNull(selectedAugmentInCargo)) {
            AugmentApplier augment = selectedAugmentInCargo.getAugment();

            tooltip.setParaFont(Fonts.ORBITRON_12);
            tooltip.addPara("Selected Augment", Misc.getBasePlayerColor(), 0f).setAlignment(Alignment.MID);
            ButtonUtils.addSeparatorLine(tooltip, augmentPanelWidth - 5f, Misc.getDarkPlayerColor(), 2f)
                       .getPosition()
                       .setXAlignOffset(-5f);
            tooltip.addSpacer(3f).getPosition().setXAlignOffset(5f);
            tooltip.setParaFont(Fonts.INSIGNIA_LARGE);
            tooltip.addPara(augment.getName(), 0f);
            tooltip.setParaFontDefault();

            ((AugmentItemPlugin) selectedAugmentInCargo.getAugmentCargoStack().getPlugin()).addTechInfo(tooltip, 6f, 0f);
            TextWithHighlights augmentDescription = augment.getDescription();
            tooltip.addPara(augmentDescription.getDisplayString(), 6f, Misc.getHighlightColor(), augmentDescription.getHighlights());
        }
    }

    private void addPrimaryStatSection(TooltipMakerAPI tooltip) {
        if (!isNull(selectedAugmentInCargo)) {
            AugmentApplier augment = selectedAugmentInCargo.getAugment();

            tooltip.setParaFont(Fonts.ORBITRON_12);
            tooltip.addPara(String.format("Slot: %s", augment.getPrimarySlot().getName()), Misc.getBasePlayerColor(), 0f)
                   .setAlignment(Alignment.MID);
            tooltip.setParaFontDefault();
            ButtonUtils.addSeparatorLine(tooltip, primaryPanelWidth - 5f, Misc.getDarkPlayerColor(), 2f)
                       .getPosition()
                       .setXAlignOffset(-5f);
            tooltip.addSpacer(3f).getPosition().setXAlignOffset(5f);
            augment.generateStatDescription(tooltip, 0, true, Misc.getBasePlayerColor());
        }
    }

    private void addSecondaryStatSection(TooltipMakerAPI tooltip) {
        if (!isNull(selectedAugmentInCargo)) {
            AugmentApplier augment = selectedAugmentInCargo.getAugment();
            tooltip.setParaFont(Fonts.ORBITRON_12);
            if (augment.getSecondarySlots().isEmpty()) {
                tooltip.addPara("-", 0f).setAlignment(Alignment.MID);
                ButtonUtils.addSeparatorLine(tooltip, secondaryPanelWidth - 5f, Misc.getDarkPlayerColor(), 2f)
                           .getPosition()
                           .setXAlignOffset(-5f);
            } else {
                List<SlotCategory> secSlotList = new SlotCategoryList<>(augment.getSecondarySlots());
                String slotString = FormattingTools.singularOrPlural(secSlotList.size(), "Slot");
                tooltip.addPara(String.format("%s: %s", slotString, secSlotList), Misc.getBasePlayerColor(), 0f)
                       .setAlignment(Alignment.MID);
                tooltip.setParaFontDefault();
                ButtonUtils.addSeparatorLine(tooltip, secondaryPanelWidth - 5f, Misc.getDarkPlayerColor(), 2f)
                           .getPosition()
                           .setXAlignOffset(-5f);
                tooltip.addSpacer(3f).getPosition().setXAlignOffset(5f);
            }
            augment.generateStatDescription(tooltip, 0, false, Misc.getBasePlayerColor());
        }
    }
}
