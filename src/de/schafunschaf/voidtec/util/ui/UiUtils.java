package de.schafunschaf.voidtec.util.ui;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIComponentAPI;

public class UiUtils {
    public static UIComponentAPI addVerticalSeparator(float height, float width, float xOffset, float yOffset, TooltipMakerAPI uiElement) {
        uiElement.addSectionHeading("", Alignment.MID, 0f).getPosition()
                 .setSize(width, height)
                 .setYAlignOffset(yOffset)
                 .setXAlignOffset(xOffset);

        return uiElement.getPrev();
    }
}
