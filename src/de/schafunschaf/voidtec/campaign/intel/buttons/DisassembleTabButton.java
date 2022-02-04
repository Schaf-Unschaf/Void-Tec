package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.InfoPanel;

public class DisassembleTabButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        InfoPanel.setSelectedTab(InfoPanel.InfoTabs.DISASSEMBLE);
    }

    @Override
    public String getName() {
        return "Disassemble";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        ButtonAPI areaCheckbox = uiElement.addAreaCheckbox(getName(), this, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(),
                                                           Misc.getBasePlayerColor(),
                                                           width, height, 0f);

        areaCheckbox.setChecked(isSelected());
        return areaCheckbox;
    }

    private boolean isSelected() {
        return InfoPanel.getSelectedTab() == InfoPanel.InfoTabs.DISASSEMBLE;
    }
}
