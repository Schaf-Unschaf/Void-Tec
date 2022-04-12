package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;

public class DetailsTabButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        AugmentManagerIntel.setShowingManufacturingPanel(false);
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.DETAILS);
    }

    @Override
    public String getName() {
        return "Install / Details";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        ButtonAPI areaCheckbox = tooltip.addAreaCheckbox(getName(), this, Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(),
                                                         Misc.getBasePlayerColor(),
                                                         width, height, 0f);
        areaCheckbox.setChecked(isSelected());

        return areaCheckbox;
    }

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.DETAILS;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final String tooltipText = "Displays information about the currently selected Augment.";
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
}
