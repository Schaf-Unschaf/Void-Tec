package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
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
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.DISMANTLE);
    }

    @Override
    public String getName() {
        return numAugments > 0 ? String.format("(%s) Dismantle", numAugments) : "Dismantle";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        Color textColor = numAugments > 0 ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        ButtonAPI areaCheckbox = uiElement.addAreaCheckbox(getName(), this, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(),
                                                           textColor, width, height, 0f);

        areaCheckbox.setChecked(isSelected());
        return areaCheckbox;
    }

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.DISMANTLE;
    }
}
