package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.ShipPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

public class FilterWithHullmodButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        ShipPanel.displayWithHullmod = !ShipPanel.displayWithHullmod;
    }

    @Override
    public String getName() {
        return "VESAI";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color textColor = ShipPanel.displayWithHullmod ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        Color borderColor = Misc.getDarkPlayerColor();
        Color hlColor = Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, textColor,
                                                         borderColor, hlColor, this);

        button.setChecked(ShipPanel.displayWithHullmod);
        button.highlight();

        String tooltipText = "Show ships with\ninstalled VESAI";
        float tooltipLength = tooltip.computeStringWidth(tooltipText);

        addTooltip(tooltip);

        return button;
    }
}
