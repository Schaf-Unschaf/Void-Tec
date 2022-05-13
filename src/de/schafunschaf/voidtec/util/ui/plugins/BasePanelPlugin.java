package de.schafunschaf.voidtec.util.ui.plugins;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;

import java.util.List;

public class BasePanelPlugin implements CustomUIPanelPlugin {

    protected PositionAPI p;

    @Override
    public void positionChanged(PositionAPI position) {
        this.p = position;
    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }
}
