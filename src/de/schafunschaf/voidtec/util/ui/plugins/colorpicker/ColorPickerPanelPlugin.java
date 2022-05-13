package de.schafunschaf.voidtec.util.ui.plugins.colorpicker;

import de.schafunschaf.voidtec.util.ui.lwjgl.ShapeUtil;
import de.schafunschaf.voidtec.util.ui.plugins.BasePanelPlugin;

import java.awt.Color;

public class ColorPickerPanelPlugin extends BasePanelPlugin {

    private final ColorPickerDialog colorPickerDialog;

    public ColorPickerPanelPlugin(ColorPickerDialog colorPickerDialog) {
        this.colorPickerDialog = colorPickerDialog;
    }

    public void render(float alphaMult) {
        Color pickedColor = colorPickerDialog.generateColor();
        int width = 45;
        int height = 25;
        int borderSize = 1;
        int margin = 1;
        int yOffset = 35;
        ShapeUtil.drawBorderedBox(p.getX(), p.getY() - yOffset, width, height, borderSize, margin, pickedColor);
    }
}
