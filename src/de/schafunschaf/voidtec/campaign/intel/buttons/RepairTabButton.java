package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.CargoPanel;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

@RequiredArgsConstructor
public class RepairTabButton extends DefaultButton {

    private final int numAugments;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoPanel.showDestroyedAugments = false;
        CargoPanel.showOnlyRepairable = true;
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.REPAIR);
    }

    @Override
    public String getName() {
        return numAugments > 0 ? String.format("(%s) Repair", numAugments) : "Repair";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        Color textColor = numAugments > 0 ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        ButtonAPI areaCheckbox = uiElement.addAreaCheckbox(getName(), this, textColor, Misc.getDarkPlayerColor(),
                                                           textColor, width, height, 0f);

        areaCheckbox.setChecked(isSelected());
        return areaCheckbox;
    }

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.REPAIR;
    }
}
