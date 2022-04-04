package de.schafunschaf.voidtec.campaign.intel.buttons.cargopanel;

import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class FilterByCategoryButton extends DefaultButton {

    private final SlotCategory slotCategory;
    private final boolean isActive;

    public FilterByCategoryButton(SlotCategory slotCategory) {
        this.slotCategory = slotCategory;
        this.isActive = !isNull(AugmentManagerIntel.getActiveCategoryFilter())
                && AugmentManagerIntel.getActiveCategoryFilter() == slotCategory;
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (isActive) {
            AugmentManagerIntel.setActiveCategoryFilter(null);
            AugmentManagerIntel.setSelectedSlot(null);
        } else {
            AugmentManagerIntel.setActiveCategoryFilter(slotCategory);
        }
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color buttonColor = slotCategory.getColor();

        boolean checked = isActive || isNull(AugmentManagerIntel.getActiveCategoryFilter());

        return ButtonUtils.addAugmentButton(tooltip, width, buttonColor, buttonColor, checked, false, this);
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final String tooltipText = String.format("Display only %s slots", slotCategory);
        final float tooltipWidth = tooltip.computeStringWidth(tooltipText);

        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return tooltipWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f, slotCategory.getColor(), slotCategory.toString());
            }
        }, TooltipMakerAPI.TooltipLocation.ABOVE);
    }
}
