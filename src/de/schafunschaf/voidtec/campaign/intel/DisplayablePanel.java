package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.ui.CustomPanelAPI;

public interface DisplayablePanel {

    void displayPanel(CustomPanelAPI panel, float width, float height, float padding);

    float getPanelWidth();

    float getPanelHeight();
}
