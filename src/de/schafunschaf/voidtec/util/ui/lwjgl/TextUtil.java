package de.schafunschaf.voidtec.util.ui.lwjgl;

import org.lazywizard.lazylib.ui.FontException;
import org.lazywizard.lazylib.ui.LazyFont;

import java.awt.Color;

public class TextUtil {

    public static void drawString(float posX, float posY, String string, String font, float size, Color color) throws FontException {
        LazyFont lazyFont = LazyFont.loadFont(font);
        LazyFont.DrawableString testString = lazyFont.createText(string, color, size);
        testString.draw(posX, posY);
    }
}
