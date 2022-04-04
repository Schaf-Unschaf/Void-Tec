package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TitlePanel {

    private final float panelWidth;
    private final float panelHeight;

    public void render(CustomPanelAPI mainPanel) {
        TooltipMakerAPI uiElement = mainPanel.createUIElement(panelWidth, panelHeight, false);
        uiElement.addSectionHeading("", Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        uiElement.addPara("VESAI - VoidTec Engineering Suite Augmentation Interface", 0f, Misc.getBrightPlayerColor(),
                          Misc.getHighlightColor(), "VESAI", "V", "E", "S", "A", "I").setAlignment(Alignment.MID);
        PositionAPI textPosition = uiElement.getPrev().getPosition();
        textPosition.setYAlignOffset(textPosition.getHeight() + 2f);

        mainPanel.addUIElement(uiElement).inTL(0, 0);
    }
}
