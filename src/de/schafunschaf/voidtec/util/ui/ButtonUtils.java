package de.schafunschaf.voidtec.util.ui;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;

import java.awt.Color;

public class ButtonUtils {

    public static ButtonAPI addLabeledButton(TooltipMakerAPI tooltip, float width, float height, float padding, Color textColor, Color bgColor,
                                             CutStyle cutStyle, IntelButton intelButton) {
        ButtonAPI button = tooltip.addButton(intelButton.getName(), intelButton, textColor, bgColor, Alignment.MID, cutStyle, width, height,
                                          padding);
        if (intelButton.getShortcut() > 0) {
            button.setShortcut(intelButton.getShortcut(), false);
        }
        return button;
    }

    public static ButtonAPI addCheckboxButton(TooltipMakerAPI tooltip, float width, float height, float padding, Color textColor,
                                              Color borderColor, Color hlColor, IntelButton intelButton) {
        ButtonAPI button = tooltip.addAreaCheckbox(intelButton.getName(), intelButton, hlColor, borderColor, textColor, width, height,
                                                   padding);
        if (intelButton.getShortcut() > 0) {
            button.setShortcut(intelButton.getShortcut(), false);
        }
        return button;
    }

    public static ButtonAPI addAugmentButton(TooltipMakerAPI tooltip, float size, Color borderColor, Color hlColor,
                                             boolean isChecked, boolean isUnique, IntelButton intelButton) {
        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, size, size, 0f, Misc.getTextColor(), borderColor, hlColor, intelButton);
        button.setChecked(isChecked);

        intelButton.addTooltip(tooltip);
        UIComponentAPI prev = tooltip.getPrev();

        if (isUnique) {
            UIComponentAPI border = UIUtils.addBox(tooltip, "", null, null, size - 2, size - 2, 4f, 5f,
                                                   Misc.getTextColor(), Color.BLACK, Color.BLACK, null);
            UIComponentAPI stripe = UIUtils.addBox(tooltip, "", null, null, size - 2, size / 4, 1f, 0f,
                                                   Misc.getTextColor(), Color.BLACK, new Color(0, 0, 0, 150), null);

            border.getPosition().rightOfMid(prev, -size + 1);
            stripe.getPosition().rightOfMid(prev, -size + 1);
        }

        return button;
    }

    public static ButtonAPI addFakeAugmentButton(TooltipMakerAPI tooltip, float size, Color borderColor, Color hlColor,
                                                 boolean isChecked, boolean isUnique) {
        ButtonAPI button = tooltip.addAreaCheckbox("", null, hlColor, borderColor, Misc.getTextColor(), size, size, 0f);
        button.setChecked(isChecked);

        UIComponentAPI prev = tooltip.getPrev();

        if (isUnique) {
            UIComponentAPI border = UIUtils.addBox(tooltip, "", null, null, size - 2, size - 2, 4f, 5f,
                                                   Misc.getTextColor(), Color.BLACK, Color.BLACK, null);
            UIComponentAPI stripe = UIUtils.addBox(tooltip, "", null, null, size - 2, size / 4, 1f, 0f,
                                                   Misc.getTextColor(), Color.BLACK, new Color(0, 0, 0, 150), null);

            border.getPosition().rightOfMid(prev, -size + 1);
            stripe.getPosition().rightOfMid(prev, -size + 1);
        }

        return button;
    }

    public static ButtonAPI addSeparatorLine(TooltipMakerAPI info, float width, Color color, float padding) {
        return info.addButton("", null, color, color, Alignment.MID, CutStyle.ALL, width, 0f, padding);
    }
}
