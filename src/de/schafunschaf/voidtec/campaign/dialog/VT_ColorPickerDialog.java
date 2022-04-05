package de.schafunschaf.voidtec.campaign.dialog;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperLeaveToCargo;
import de.schafunschaf.voidtec.combat.vesai.RightClickAction;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

@RequiredArgsConstructor
public class VT_ColorPickerDialog implements CustomDialogDelegate {

    private final InteractionDialogAPI dialog;
    private final RightClickAction rightClickAction;
    private TextFieldAPI fieldRed = null;
    private TextFieldAPI fieldGreen = null;
    private TextFieldAPI fieldBlue = null;
    private TextFieldAPI fieldAlpha = null;

    @Override
    public void createCustomDialog(CustomPanelAPI panel) {
        Color currentColor = (Color) rightClickAction.getActionObject();

        TooltipMakerAPI uiElement = panel.createUIElement(300, 70, false);
        uiElement.setParaFont(Fonts.INSIGNIA_LARGE);
        uiElement.addPara("Pick new color values:", 0f);
        fieldRed = addColorField(currentColor.getRed(), Color.RED, uiElement);
        fieldGreen = addColorField(currentColor.getGreen(), Color.GREEN, uiElement);
        fieldBlue = addColorField(currentColor.getBlue(), Color.BLUE, uiElement);
        fieldAlpha = addColorField(currentColor.getAlpha(), Color.WHITE, uiElement);

        panel.addUIElement(uiElement);
    }

    private TextFieldAPI addColorField(int value, Color color, TooltipMakerAPI uiElement) {
        TextFieldAPI field = uiElement.addTextField(300, 10f);
        field.setColor(color);
        field.setText(String.valueOf(value));
        field.setUndoOnEscape(true);

        return field;
    }

    @Override
    public boolean hasCancelButton() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Set Color";
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        int redValue = getColorValue(fieldRed);
        int greenValue = getColorValue(fieldGreen);
        int blueValue = getColorValue(fieldBlue);
        int alphaValue = getColorValue(fieldAlpha);

        rightClickAction.setActionObject(new Color(redValue, greenValue, blueValue, alphaValue));

        closeDialog();
    }

    private int getColorValue(TextFieldAPI textField) {
        int parsedValue = 0;
        try {
            parsedValue = Integer.parseInt(textField.getText());

        } catch (NumberFormatException exception) {
            Global.getLogger(VT_ColorPickerDialog.class).error(exception);
        }

        return Math.max(Math.min(parsedValue, 255), 0);
    }

    @Override
    public void customDialogCancel() {
        closeDialog();
    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }

    private void closeDialog() {
        Global.getSector().addTransientScript(new VT_DialogHelperLeaveToCargo());
        dialog.dismiss();
    }
}
