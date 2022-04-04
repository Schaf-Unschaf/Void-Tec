package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.CargoPanel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;

import java.awt.Color;

public class HelpTabButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoPanel.showDestroyedAugments = false;
        CargoPanel.showOnlyRepairable = false;
        AugmentManagerIntel.setShowingManufacturingPanel(false);
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.HELP);
    }

    @Override
    public String getName() {
        return "?";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color textColor = Misc.getHighlightColor();
        Color bgColor = isSelected() ? Misc.getDarkHighlightColor().darker().darker() : Misc.getDarkPlayerColor();
        Color hlColor = isSelected() ? Misc.getDarkHighlightColor() : Misc.getDarkPlayerColor();
        ButtonAPI areaCheckbox = tooltip.addAreaCheckbox(getName(), this, hlColor, bgColor,
                                                         textColor, width, height, 0f);
        areaCheckbox.setChecked(isSelected());

        return areaCheckbox;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final String tooltipText = "Displays help and information about the UI.";
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
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.HELP;
    }
}
