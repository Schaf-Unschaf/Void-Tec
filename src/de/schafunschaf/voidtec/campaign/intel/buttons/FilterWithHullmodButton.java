package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.ShipPanel;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
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
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        Color textColor = ShipPanel.displayWithHullmod ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        Color borderColor = Misc.getDarkPlayerColor();
        Color hlColor = Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addCheckboxButton(uiElement, width, height, 0f, textColor,
                                                         borderColor, hlColor, this);

        button.setChecked(ShipPanel.displayWithHullmod);
        button.highlight();

        addTooltip(uiElement, null);

        return button;
    }

    @Override
    protected void addTooltip(TooltipMakerAPI uiElement, SlotCategory slotCategory) {
        final String tooltipText = "Show ships with\ninstalled VESAI";
        final float tooltipLength = uiElement.computeStringWidth(tooltipText);

        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return tooltipLength;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }
}
