package de.schafunschaf.voidtec.util.ui.lwjgl;

import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class ShapeUtil {

    public static void drawBox(float posX, float posY, float width, float height, Color color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(posX, posY); // BL
            GL11.glVertex2f(posX, posY + height); // TL
            GL11.glVertex2f(posX + width, posY + height); // TR
            GL11.glVertex2f(posX + width, posY); // BR
        }
        GL11.glEnd();
    }

    public static void drawBorderedBox(float posX, float posY, float width, float height, float borderSize, float borderMargin,
                                       Color color) {
        ShapeUtil.drawBox(posX, posY, width, height, color);
        ShapeUtil.drawBox(posX + borderSize, posY + borderSize, width - borderSize * 2, height - borderSize * 2, color.darker().darker());
        ShapeUtil.drawBox(posX + borderMargin * 2, posY + borderMargin * 2, width - borderMargin * 4, height - borderMargin * 4, color);
    }
}
