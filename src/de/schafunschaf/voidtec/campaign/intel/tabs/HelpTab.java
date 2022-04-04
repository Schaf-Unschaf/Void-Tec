package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class HelpTab {

    private final TooltipMakerAPI tooltip;
    private final float parentHeight;
    private final float padding;
    private final float leftPanelWidth;
    private final float rightPanelWidth;
    private final float panelHeaderHeight = 20f;

    public HelpTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentHeight = height;
        this.padding = padding;
        this.leftPanelWidth = width / 2;
        this.rightPanelWidth = width / 2;
    }

    public void render(CustomPanelAPI mainPanel) {

    }

    private void addLeftPanel() {

    }

    private void addRightPanel() {

    }
}
