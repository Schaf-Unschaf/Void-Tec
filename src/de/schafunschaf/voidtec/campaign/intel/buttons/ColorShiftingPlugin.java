package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import de.schafunschaf.voidtec.helper.ColorShifter;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ColorShiftingPlugin implements CustomUIPanelPlugin {

    public ColorShiftingPlugin(float padX, float padY, float width, float height, Color color) {
        this.padX = padX;
        this.padY = padY;
        this.width = width;
        this.height = height;
        this.colorShifter = new ColorShifter(color);
    }

    private final float padX;
    private final float padY;
    private final float width;
    private final float height;
    private final ColorShifter colorShifter;

    private PositionAPI position;

    @Override
    public void positionChanged(PositionAPI position) {
        this.position = position;
    }

    @Override
    public void renderBelow(float alphaMult) {

    }

    @Override
    public void render(float alphaMult) {
        if (isNull(position)) {
            return;
        }

        float x = position.getX();
        float y = position.getY();

        Color color = colorShifter.shiftColor(0.5f);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(x + padX, y + padY);
            GL11.glVertex2f(x + padX, y + padY + height);
            GL11.glVertex2f(x + padX + width, y + padY + height);
            GL11.glVertex2f(x + padX + width, y + padY);
        }
        GL11.glEnd();
    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void processInput(List<InputEventAPI> events) {

    }
}
