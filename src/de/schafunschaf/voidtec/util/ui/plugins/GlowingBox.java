package de.schafunschaf.voidtec.util.ui.plugins;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import de.schafunschaf.voidtec.helper.ColorShifter;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

public class GlowingBox implements CustomUIPanelPlugin {

    private PositionAPI p;
    private final ColorShifter colorShifter;

    public GlowingBox(Color startingColor) {
        this.colorShifter = new ColorShifter(startingColor);
    }

    public void positionChanged(PositionAPI position) {
        p = position;
    }

    public void advance(float amount) {
    }

    public void processInput(List<InputEventAPI> events) {
    }

    public void render(float alphaMult) {
        float x = p.getX();
        float y = p.getY();
        float w = p.getWidth();
        float h = p.getHeight();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Color color = colorShifter.shiftColor(0.1f);

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(x, y); // BL
            GL11.glVertex2f(x, y + h); // TL
            GL11.glVertex2f(x + w, y + h); // TR
            GL11.glVertex2f(x + w, y); // BR
        }
        GL11.glEnd();
    }

    public void renderBelow(float alphaMult) {
    }
}
