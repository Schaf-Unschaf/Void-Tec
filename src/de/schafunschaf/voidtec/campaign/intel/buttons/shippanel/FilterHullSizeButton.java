package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.ShipPanel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
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
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color textColor = filterActive ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        Color borderColor = Misc.getDarkPlayerColor();
        Color hlColor = Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, textColor,
                                                         borderColor, hlColor, this);

        button.setChecked(filterActive);
        button.highlight();

        String shipSizeString = FormattingTools.capitalizeFirst(
                filter.toString().replace(ShipAPI.HullSize.CAPITAL_SHIP.toString(), "Capital").toLowerCase());
        String tooltipText = String.format("Filter by %s-Size", shipSizeString);
        float tooltipLength = tooltip.computeStringWidth(tooltipText);

        addTooltip(tooltip);

        return button;
    }
}
