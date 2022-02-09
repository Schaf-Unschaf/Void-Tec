package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import de.schafunschaf.voidtec.campaign.intel.buttons.TitlePanel;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Colors;
import de.schafunschaf.voidtec.util.CargoUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.List;

public class AugmentManagerIntel extends BaseIntel {

    public static final String STACK_SOURCE = "augmentManagerIntel";

    @Getter
    private static List<AugmentCargoWrapper> augmentsInCargo;
    @Getter
    @Setter
    private static AugmentCargoWrapper selectedAugmentInCargo;
    @Getter
    @Setter
    private static SlotCategory activeCategoryFilter;

    @Override
    public void notifyPlayerAboutToOpenIntelScreen() {
        selectedAugmentInCargo = null;
        activeCategoryFilter = null;
    }

    @Override
    public boolean canTurnImportantOff() {
        return false;
    }

    @Override
    public boolean hasImportantButton() {
        return false;
    }

    @Override
    protected String getName() {
        return "VESAI";
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    @Override
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        augmentsInCargo = CargoUtils.getAugmentsInCargo();

        TitlePanel titlePanel = new TitlePanel();
        titlePanel.displayPanel(panel, width, height, 0f);
        DisplayablePanel shipPanel = new ShipPanel();
        shipPanel.displayPanel(panel, width, height, titlePanel.getPanelHeight() + 3f);
        new CargoPanel().displayPanel(panel, width - shipPanel.getPanelWidth(), height, titlePanel.getPanelHeight());
        new InfoPanel().displayPanel(panel, shipPanel.getPanelWidth(), 192f,
                                     shipPanel.getPanelHeight() + titlePanel.getPanelHeight() * 2 + 3f);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "voidTec_hullmod_icon");
    }

    @Override
    public boolean isImportant() {
        return true;
    }

    @Override
    public void reportPlayerClickedOn() {
        super.reportPlayerClickedOn();
    }

    @Override
    public Color getTitleColor(ListInfoMode mode) {
        return VT_Colors.VT_COLOR_MAIN;
    }


}
