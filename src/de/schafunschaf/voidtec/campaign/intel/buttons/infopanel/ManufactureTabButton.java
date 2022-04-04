package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.CargoPanel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;

public class ManufactureTabButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoPanel.showDestroyedAugments = false;
        CargoPanel.showOnlyRepairable = false;
        AugmentManagerIntel.setShowingManufacturingPanel(true);
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.MANUFACTURE);
    }

    @Override
    public String getName() {
        return "Manufacture";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        ButtonAPI areaCheckbox = tooltip.addAreaCheckbox(getName(), this, Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(),
                                                         Misc.getBasePlayerColor(),
                                                         width, height, 0f);
        areaCheckbox.setChecked(isSelected());
        areaCheckbox.setEnabled(false); //TODO Implement manufacturing

        return areaCheckbox;
    }

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.MANUFACTURE;
    }
}
