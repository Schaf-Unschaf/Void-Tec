package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.CargoPanel;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

public class ShowDestroyedButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        CargoPanel.showDestroyedAugments = !CargoPanel.showDestroyedAugments;
    }

    @Override
    public String getName() {
        return "D";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        Color bgColor = CargoPanel.showDestroyedAugments ? Misc.getDarkPlayerColor() : Misc.getDarkPlayerColor().darker();
        ButtonAPI button = ButtonUtils.addLabeledButton(uiElement, width, height, 0f, Misc.getBasePlayerColor(), bgColor,
                                                        CutStyle.ALL, this);
        addTooltip(uiElement, null);

        return button;
    }

    @Override
    protected void addTooltip(final TooltipMakerAPI uiElement, SlotCategory slotCategory) {
        String showOrHide = CargoPanel.showDestroyedAugments ? "Hide" : "Show";
        final String tooltipText = String.format("%s %s Augments", showOrHide, AugmentQuality.DESTROYED.name());
        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return 200f;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipText, 0f, AugmentQuality.DESTROYED.getColor(), AugmentQuality.DESTROYED.name())
                       .setAlignment(Alignment.MID);
            }
        }, TooltipMakerAPI.TooltipLocation.LEFT);
    }
}
