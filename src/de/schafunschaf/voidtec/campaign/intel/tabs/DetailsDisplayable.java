package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.DisplayablePanel;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Icons;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class DetailsDisplayable implements DisplayablePanel {

    @Override
    public void displayPanel(CustomPanelAPI panel, float width, float height, float padding) {
        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);

        AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
        Color borderColor = isNull(selectedAugmentInCargo)
                            ? Misc.getDarkPlayerColor()
                            : selectedAugmentInCargo.getAugment().getAugmentQuality().getColor().brighter();
        uiElement.addAreaCheckbox("", null, new Color(0, 0, 0, 0), borderColor, Color.green, 50f, 50f, 50f).setEnabled(false);
        uiElement.addImage(VT_Icons.AUGMENT_ITEM_ICON, 50f, 50f, 0f);
        uiElement.getPrev().getPosition().setYAlignOffset(uiElement.getPrev().getPosition().getHeight());

        panel.addUIElement(uiElement).setYAlignOffset(-padding - 6f);
    }

    @Override
    public float getPanelWidth() {
        return 0;
    }

    @Override
    public float getPanelHeight() {
        return 0;
    }
}
