package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.CargoUtils;

import java.awt.Color;

public class SortOrderButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoUtils.sortDescending = !CargoUtils.sortDescending;
    }

    @Override
    public String getName() {
        return CargoUtils.sortDescending ? "D" : "A";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        Color color = CargoUtils.sortDescending ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        ButtonAPI button = ButtonUtils.addLabeledButton(uiElement, width, height, 0f, color, color.darker().darker().darker(),
                                                        CutStyle.ALL, this);
        addTooltip(uiElement, null);

        return button;
    }

    @Override
    protected void addTooltip(final TooltipMakerAPI uiElement, SlotCategory slotCategory) {
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
        }, TooltipMakerAPI.TooltipLocation.LEFT);
    }
}
