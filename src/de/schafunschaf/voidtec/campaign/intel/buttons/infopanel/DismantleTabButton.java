package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.CargoPanel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

@RequiredArgsConstructor
public class DismantleTabButton extends DefaultButton {

    private final int numAugments;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoPanel.showDestroyedAugments = true;
        CargoPanel.showOnlyRepairable = false;
        AugmentManagerIntel.setShowingManufacturingPanel(false);
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.DISMANTLE);
    }

    @Override
    public String getName() {
        return numAugments > 0 ? String.format("(%s) Dismantle", numAugments) : "Dismantle";
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

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.DISMANTLE;
    }
}
