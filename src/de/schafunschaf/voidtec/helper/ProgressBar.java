package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;

import java.awt.Color;

public class ProgressBar {

    public static ButtonAPI addStorageMeter(TooltipMakerAPI panel, float size, float height, float value, float maxValue, Color borderColor,
                                            Color backgroundColor, Color barColor, float padding) {
        String numberText = String.format("%s/%s", (int) value, (int) maxValue);
        float bgHeight = height - 2f;
        float barFillSize = Math.min(Math.max((value / maxValue) * size, 0f), size);

        Color spaceHLColor = barColor;
        if (value / maxValue >= 0.75f) {
            spaceHLColor = Misc.getHighlightColor();
        }
        if (value == maxValue) {
            spaceHLColor = Misc.getNegativeHighlightColor();
        }

        ButtonAPI mainButton = panel.addButton("", null, Color.BLACK, borderColor, Alignment.MID, CutStyle.ALL, size, height, padding);
        ButtonAPI mainBackground = panel.addButton("", null, Color.BLACK, backgroundColor, Alignment.MID, CutStyle.ALL, size - 2, bgHeight,
                                                   0f);
        ButtonAPI buttonFillBar = panel.addButton("", null, Color.BLACK, barColor, Alignment.MID, CutStyle.ALL, barFillSize + 16, bgHeight,
                                                  0);
        ButtonAPI secondFillGapButton = panel.addButton("", null, Color.BLACK, borderColor, Alignment.MID, CutStyle.ALL, 0f, height, 0f);
        ButtonAPI numberDisplayButton = panel.addButton("", null, Color.BLACK, barColor, Alignment.MID, CutStyle.ALL, 46 + 18f, height, 0f);
        ButtonAPI numberDisplayButtonBG = panel.addButton("", null, Color.BLACK, Color.BLACK, Alignment.MID, CutStyle.ALL, 46 + 10f,
                                                          bgHeight - 4, 0f);
        ButtonAPI oneQuarterIndicator = panel.addButton("", null, Color.BLACK, borderColor, Alignment.MID, CutStyle.ALL, 0f, height, 0f);
        ButtonAPI halfIndicator = panel.addButton("", null, Color.BLACK, borderColor, Alignment.MID, CutStyle.ALL, 0f, height, 0f);
        ButtonAPI threeQuarterIndicator = panel.addButton("", null, Color.BLACK, borderColor, Alignment.MID, CutStyle.ALL, 0f, height, 0f);
        LabelAPI numberDisplay = panel.addPara(numberText, 3f, Misc.getGrayColor(), spaceHLColor, String.valueOf((int) value),
                                               String.valueOf((int) maxValue));

        mainBackground.getPosition().rightOfMid(mainButton, -size + 1);
        buttonFillBar.getPosition().rightOfMid(mainButton, -size - 16);
        secondFillGapButton.getPosition().leftOfMid(mainButton, 0f);
        numberDisplayButton.getPosition().leftOfMid(mainButton, 0f);
        numberDisplayButtonBG.getPosition().leftOfMid(mainButton, 4f);
        oneQuarterIndicator.getPosition().rightOfTop(mainButton, -(size / 4));
        halfIndicator.getPosition().belowMid(mainButton, -height);
        threeQuarterIndicator.getPosition().rightOfTop(mainButton, -(size / 4) * 3);
        numberDisplay.setAlignment(Alignment.MID);
        numberDisplay.getPosition().leftOfMid(mainButton, -18f);

        return mainButton;
    }
}
