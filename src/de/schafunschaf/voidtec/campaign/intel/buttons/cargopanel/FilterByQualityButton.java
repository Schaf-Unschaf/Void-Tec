package de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class FilterByQualityButton extends DefaultButton {

    private final AugmentQuality augmentQuality;
    private final boolean isActive;

    public FilterByQualityButton(AugmentQuality augmentQuality) {
        this.augmentQuality = augmentQuality;
        this.isActive = !isNull(AugmentManagerIntel.getActiveQualityFilter())
                && AugmentManagerIntel.getActiveQualityFilter() == augmentQuality;
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (isActive) {
            AugmentManagerIntel.setActiveQualityFilter(null);
            AugmentManagerIntel.setSelectedSlot(null);
        } else {
            AugmentManagerIntel.setActiveQualityFilter(augmentQuality);
        }
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color buttonColor = augmentQuality.getColor();

        boolean checked = isActive || isNull(AugmentManagerIntel.getActiveQualityFilter());

        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, buttonColor, buttonColor, buttonColor, this);
        button.setChecked(checked);
        addTooltip(tooltip);

        return button;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        tooltip.setParaFontDefault();
        final String tooltipText = String.format("Display only %s quality", augmentQuality);
        final float tooltipWidth = tooltip.computeStringWidth(tooltipText);

        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return tooltipWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f, augmentQuality.getColor(), augmentQuality.toString());
            }
        }, TooltipMakerAPI.TooltipLocation.ABOVE);
    }
}
