package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;

import java.awt.*;

public class ButtonUtils {
    public static ButtonAPI addLabeledButton(TooltipMakerAPI info, float width, float height, float padding, Color textColor, Color bgColor, CutStyle cutStyle, IntelButton intelButton) {
        ButtonAPI button = info.addButton(intelButton.getName(), intelButton, textColor, bgColor, Alignment.MID, cutStyle, width, height, padding);
        if (intelButton.getShortcut() > 0) {
            button.setShortcut(intelButton.getShortcut(), false);
        }
        return button;
    }

    public static ButtonAPI addAugmentButton(TooltipMakerAPI info, float size, float padding, Color textColor, Color bgColor, IntelButton intelButton) {
        ButtonAPI button = info.addButton(intelButton.getName(), intelButton, textColor, bgColor, Alignment.MID, CutStyle.ALL, size, size, padding);
        if (intelButton.getShortcut() > 0) {
            button.setShortcut(intelButton.getShortcut(), false);
        }
        return button;
    }

    public static ButtonAPI addSeparatorLine(TooltipMakerAPI info, float width, Color color, float padding) {
        return info.addButton("", null, color, color, Alignment.MID, CutStyle.ALL, width, 0f, padding);
    }
}
