package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.ShipPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

public class FilterHullSizeButton extends DefaultButton {

    private final ShipAPI.HullSize filter;
    private final boolean filterActive;

    public FilterHullSizeButton(ShipAPI.HullSize filter) {
        this.filter = filter;
        this.filterActive = ShipPanel.displayHullSizes.contains(filter);
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (filterActive) {
            ShipPanel.displayHullSizes.remove(filter);
        } else {
            ShipPanel.displayHullSizes.add(filter);
        }
    }

    @Override
    public String getName() {
        return filter.name().substring(0, 2);
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        Color textColor = filterActive ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        Color borderColor = Misc.getDarkPlayerColor();
        Color hlColor = Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addCheckboxButton(uiElement, width, height, 0f, textColor,
                                                         borderColor, hlColor, this);

        button.setChecked(filterActive);
        button.highlight();

        addTooltip(uiElement, null);

        return button;
    }

    @Override
    protected void addTooltip(TooltipMakerAPI uiElement, SlotCategory slotCategory) {
        String shipSizeString = FormattingTools.capitalizeFirst(
                filter.toString().replace(ShipAPI.HullSize.CAPITAL_SHIP.toString(), "Capital").toLowerCase());
        final String tooltipText = String.format("Filter by %s-Size", shipSizeString);
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
