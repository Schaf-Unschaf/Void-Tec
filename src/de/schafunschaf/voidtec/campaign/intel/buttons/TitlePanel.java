package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import lombok.Getter;

@Getter
public class TitlePanel {

    private float panelHeight;
    private float panelWidth;

    public void displayPanel(CustomPanelAPI panel, float width, float height, float padding) {
        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);
        LabelAPI sectionHeading = uiElement.addSectionHeading("", Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        uiElement.addPara("VESAI - VoidTec Engineering Suite Augmentation Interface", 0f, Misc.getBrightPlayerColor(),
                          Misc.getHighlightColor(), "VESAI", "V", "E", "S", "A", "I").setAlignment(Alignment.MID);
        PositionAPI textPosition = uiElement.getPrev().getPosition();
        textPosition.setYAlignOffset(textPosition.getHeight() + 2f);

        panelHeight = sectionHeading.getPosition().getHeight();
        panelWidth = width;

        panel.addUIElement(uiElement);
    }
}
