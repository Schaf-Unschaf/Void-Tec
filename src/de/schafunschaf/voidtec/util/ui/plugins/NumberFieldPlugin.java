package de.schafunschaf.voidtec.util.ui.plugins;

import com.fs.starfarer.api.ui.TextFieldAPI;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.MathUtils;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class NumberFieldPlugin extends BasePanelPlugin {

    private final float minValue;
    private final float maxValue;
    @Setter
    private TextFieldAPI textField = null;
    private int oldValue = 0;

    @Override
    public void advance(float amount) {
        sanitizeField();
    }

    private void sanitizeField() {
        if (isNull(textField)) {
            return;
        }

        String fieldText = textField.getText();
        int value = fieldText.isEmpty() ? 0 : FormattingTools.parseInteger(fieldText, oldValue);
        int newValue = (int) MathUtils.clamp(value, minValue, maxValue);
        textField.setText(String.valueOf(newValue));
        oldValue = newValue;
    }
}
