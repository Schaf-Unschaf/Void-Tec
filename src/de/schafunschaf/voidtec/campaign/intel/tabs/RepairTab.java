package de.schafunschaf.voidtec.campaign.intel.tabs;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class RepairTab {

    private final TooltipMakerAPI tooltip;
    private final float parentWidth;
    private final float parentHeight;
    private final float padding;

    private final AugmentCargoWrapper selectedAugmentInCargo = AugmentManagerIntel.getSelectedAugmentInCargo();
    private final boolean augmentSelected = !isNull(selectedAugmentInCargo);

    public RepairTab(TooltipMakerAPI tooltip, float width, float height, float padding) {
        this.tooltip = tooltip;
        this.parentWidth = width;
        this.parentHeight = height;
        this.padding = padding;
    }

    public void render() {

    }
}
