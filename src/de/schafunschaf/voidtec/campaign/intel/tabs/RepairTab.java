package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
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

    private final AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
    private final boolean augmentSelected = !isNull(selectedAugmentInCargo);

    public RepairTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.augmentPanelWidth = width * 0.25f;
    }

    public void render() {
        CustomPanelAPI augmentInfoPanel = Global.getSettings().createCustom(augmentPanelWidth, parentHeight - padding, null);
        TooltipMakerAPI augmentUIElement = augmentInfoPanel.createUIElement(augmentPanelWidth, parentHeight - padding, true);

        addAugmentInfo(augmentUIElement);

        augmentInfoPanel.addUIElement(augmentUIElement);

        tooltip.addCustom(augmentInfoPanel, 0f).getPosition()
               .setSize(augmentPanelWidth, parentHeight - padding);
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

    private boolean canBeRepaired() {
        return true;
    }
}
