package de.schafunschaf.voidtec.util.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public class UIUtils {

    public static LabelAPI addVerticalSeparator(TooltipMakerAPI tooltip, float width, float height, Color color) {
        LabelAPI vertSeparator = tooltip.addSectionHeading("", color, color, Alignment.MID, 0f);
        vertSeparator.getPosition().setSize(width, height);

        return vertSeparator;
    }

    public static void addHorizontalSeparator(TooltipMakerAPI tooltip, float width, float height, Color color, float padding) {
        CustomPanelAPI customPanelAPI = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI uiElement = customPanelAPI.createUIElement(width, height, false);

        LabelAPI horSeparator = uiElement.addSectionHeading("", color, color, Alignment.MID, 0f);
        horSeparator.getPosition().setSize(width, height);

        customPanelAPI.addUIElement(uiElement).inTL(0, 0);
        tooltip.addCustom(customPanelAPI, padding);
    }

    public static UIComponentAPI addBox(TooltipMakerAPI tooltip, String text, @Nullable Alignment alignment, @Nullable String font,
                                                 float width, float height, float borderSize, float borderMargin,
                                                 @Nullable Color textColor, Color borderColor, Color bgColor) {
        if (alignment == null) {
            alignment = Alignment.MID;
        }
        if (textColor == null) {
            textColor = Misc.getTextColor();
        }

        CustomPanelAPI customPanel = Global.getSettings().createCustom(width, height, null);
        TooltipMakerAPI uiElement = customPanel.createUIElement(width, height, false);
        if (font != null && !font.isEmpty()) {
            uiElement.setParaFont(font);
        }
        uiElement.addSectionHeading("", textColor, bgColor, alignment, 0f).getPosition()
                 .setSize(width - borderSize * 2 - borderMargin * 2, height - borderSize * 2 - borderMargin * 2)
                 .inTL(borderMargin + borderSize, borderMargin + borderSize); // filling
        uiElement.addPara(text, textColor, 0f).setAlignment(alignment);
        uiElement.getPrev().getPosition().setSize(width - borderSize * 2 - borderMargin * 2, height - borderSize * 2 - borderMargin * 2)
                 .inTL(borderMargin + borderSize, borderMargin + borderSize);
        uiElement.addSectionHeading("", Color.BLACK, borderColor, Alignment.MID, 0f).getPosition()
                 .setSize(width, borderSize)
                 .inTL(0, 0); // top
        uiElement.addSectionHeading("", Color.BLACK, borderColor, Alignment.MID, 0f).getPosition()
                 .setSize(borderSize, height - borderSize)
                 .inTL(0, borderSize); // left
        uiElement.addSectionHeading("", Color.BLACK, borderColor, Alignment.MID, 0f).getPosition()
                 .setSize(borderSize, height - borderSize)
                 .inTL(width - borderSize, borderSize); // right
        uiElement.addSectionHeading("", Color.BLACK, borderColor, Alignment.MID, 0f).getPosition()
                 .setSize(width, borderSize)
                 .inTL(0, height - borderSize); // bot

        customPanel.addUIElement(uiElement).inTL(0, 0);
        tooltip.addCustom(customPanel, 0f);

        return customPanel;
    }
}
