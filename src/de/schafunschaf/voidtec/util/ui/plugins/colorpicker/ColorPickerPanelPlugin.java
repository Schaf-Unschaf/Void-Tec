package de.schafunschaf.voidtec.util.ui.plugins.colorpicker;

import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.PositionAPI;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

public class ColorPickerPanelPlugin implements CustomUIPanelPlugin {

    private PositionAPI p;
    private final ColorPickerDialog colorPickerDialog;

    public ColorPickerPanelPlugin(ColorPickerDialog colorPickerDialog) {
        this.colorPickerDialog = colorPickerDialog;
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

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Color color = colorPickerDialog.generateColor();

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(x, y - 35); // BL
            GL11.glVertex2f(x, y - 10); // TL
            GL11.glVertex2f(x + 40, y - 10); //TR
            GL11.glVertex2f(x + 40, y - 35); // BR
        }
        GL11.glEnd();
    }

    public void renderBelow(float alphaMult) {
    }
}
