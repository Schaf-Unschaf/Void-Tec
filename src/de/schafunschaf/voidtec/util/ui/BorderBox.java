package de.schafunschaf.voidtec.util.ui;

import com.fs.starfarer.api.ui.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Setter
@RequiredArgsConstructor
public class BorderBox {

    private final CustomPanelAPI panel;
    @Setter(value = AccessLevel.NONE)
    private CustomPanelAPI bodyPanel;

    private float width = 0;
    private float height = 0;
    private float borderSize = 0;
    private float marginTop = 0;
    private float marginRight = 0;
    private float marginBottom = 0;
    private float marginLeft = 0;
    private float padding = 0;
    private float titleHeight = 0;
    private String title = "";
    private Alignment titleAlignment = Alignment.MID;

    public PositionAPI createBorderBox() {
        float internalXMargin = 5f;

        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);

        // Top header with title
        uiElement.addSectionHeading(title, titleAlignment, 0f).getPosition().setSize(width, titleHeight);
        UIComponentAPI titleComponent = uiElement.getPrev();

        // Right border
        uiElement.addSectionHeading("", Alignment.MID, 0f).getPosition()
                 .setSize(borderSize, height - titleHeight)
                 .inTR(0, titleHeight);

        // Left border
        uiElement.addSectionHeading("", Alignment.MID, 0f).getPosition()
                 .setSize(borderSize, height - titleHeight)
                 .inTL(0, titleHeight);

        // Body
        if (bodyPanel != null) {
            bodyPanel.getPosition().inTL(0f, 0);
            uiElement.addCustom(bodyPanel, 0f).getPosition()
                     .inTL(-0 + borderSize + marginLeft, titleHeight + marginTop);
        }

        // Bottom border
        uiElement.addSectionHeading("", Alignment.MID, padding).getPosition()
                 .setSize(width - borderSize * 2, borderSize)
                 .inTMid(height - borderSize);

        return panel.addUIElement(uiElement);
    }

    public BorderBox margin(float margin) {
        marginBottom = margin;
        marginTop = margin;
        marginLeft = margin;
        marginRight = margin;

        return this;
    }

    public CustomPanelAPI getBodyPanel() {
        float trueWidth = this.width - 2 * borderSize - marginLeft - marginRight;
        float remainingHeight = this.height - titleHeight - borderSize - marginTop - marginBottom;
        bodyPanel = panel.createCustomPanel(trueWidth, remainingHeight, null);

        return bodyPanel;
    }
}
