package de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

public class SortOrderButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoUtils.sortDescending = !CargoUtils.sortDescending;
    }

    @Override
    public String getName() {
        return CargoUtils.sortDescending ? "DSC" : "ASC";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        ButtonAPI button = ButtonUtils.addLabeledButton(tooltip, width, height, 0f, Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(),
                                                        CutStyle.TOP, this);

        addTooltip(tooltip);

        return button;
    }

    @Override
    public void addTooltip(final TooltipMakerAPI uiElement) {
        final String ascOrDesc = CargoUtils.sortDescending ? "Ascending" : "Descending";
        final Color color = CargoUtils.sortDescending ? Misc.getNegativeHighlightColor() : Misc.getPositiveHighlightColor();
        final String tooltipText = String.format("Click to sort %s", ascOrDesc);
        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return uiElement.computeStringWidth(tooltipText);
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f, color, ascOrDesc).setAlignment(Alignment.MID);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }
}
