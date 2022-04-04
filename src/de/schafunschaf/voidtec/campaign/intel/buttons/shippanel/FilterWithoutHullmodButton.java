package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.ShipPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

public class FilterWithoutHullmodButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        ShipPanel.displayWithoutHullmod = !ShipPanel.displayWithoutHullmod;
    }

    @Override
    public String getName() {
        return "EMPTY";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color textColor = ShipPanel.displayWithoutHullmod ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        Color borderColor = Misc.getDarkPlayerColor();
        Color hlColor = Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, textColor,
                                                         borderColor, hlColor, this);

        button.setChecked(ShipPanel.displayWithoutHullmod);
        button.highlight();

        String tooltipText = "Show ships without\ninstalled VESAI";
        float tooltipLength = tooltip.computeStringWidth(tooltipText);

        addTooltip(tooltip);

        return button;
    }
}