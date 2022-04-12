package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

@RequiredArgsConstructor
public class RepairTabButton extends DefaultButton {

    private final int numAugments;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        AugmentManagerIntel.setShowingManufacturingPanel(false);
        AugmentManagerIntel.setSelectedSlot(null);
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.REPAIR);
    }

    @Override
    public String getName() {
        return (numAugments > 0 ? String.format("(%s) Repair", numAugments) : "Repair") + " / Dismantle";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color textColor = numAugments > 0 ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        Color bgColor = numAugments > 0 && isSelected() ? Misc.getDarkHighlightColor().darker().darker() : Misc.getDarkPlayerColor();
        Color hlColor = numAugments > 0 ? Misc.getDarkHighlightColor() : Misc.getDarkPlayerColor();
        ButtonAPI areaCheckbox = tooltip.addAreaCheckbox(getName(), this, hlColor, bgColor,
                                                         textColor, width, height, 0f);
        areaCheckbox.setChecked(isSelected());

        return areaCheckbox;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final String tooltipText = "Lets you repair damaged and degraded Augments or\n" +
                "dismantle no longer needed ones for spare parts.\n" +
                "Depending on the severity you may need additional\n" +
                "resources and multiple tries to restore their full power.";
        final float tooltipWidth = tooltip.computeStringWidth(tooltipText);

        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return tooltipWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.REPAIR;
    }
}
