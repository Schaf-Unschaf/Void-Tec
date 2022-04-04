package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Colors;
import de.schafunschaf.voidtec.util.CargoUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentManagerIntel extends BaseIntel {

    public static final String STACK_SOURCE = "augmentManagerIntel";
    @Getter
    private static List<AugmentCargoWrapper> augmentsInCargo;
    @Getter
    @Setter
    private static AugmentCargoWrapper selectedAugmentInCargo;
    @Getter
    @Setter
    private static AugmentApplier selectedInstalledAugment;
    @Getter
    @Setter
    private static AugmentSlot selectedSlot;
    @Getter
    @Setter
    private static SlotCategory activeCategoryFilter;
    @Getter
    @Setter
    private static boolean isShowingManufacturingPanel = false;

    public static AugmentManagerIntel getInstance() {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        AugmentManagerIntel instance;

        if (intelManager.hasIntelOfClass(AugmentManagerIntel.class)) {
            instance = ((AugmentManagerIntel) intelManager.getIntel(AugmentManagerIntel.class).get(0));
        } else {
            instance = new AugmentManagerIntel();
        }

        return instance;
    }

    public static AugmentApplier getSelectedAugment() {
        AugmentCargoWrapper selectedAugmentInCargo = getSelectedAugmentInCargo();

        return isNull(selectedAugmentInCargo) ? getSelectedInstalledAugment() : selectedAugmentInCargo.getAugment();
    }

    @Override
    public void notifyPlayerAboutToOpenIntelScreen() {
        selectedAugmentInCargo = null;
        activeCategoryFilter = null;
        selectedSlot = null;
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

        /*
         * Panel-Size at 1280x768 with 100% scaling is 895 x 550.
         * This is the minimum supported resolution and size which will act as a base and getting upscaled on higher res.
         */

        float padding = 1f;
        float titlePanelHeight = 18f;
        float cargoPanelWidth = 260f;
        float cargoPanelHeight = height - titlePanelHeight - padding;
        float infoPanelWidth = width - cargoPanelWidth - padding;
        float infoPanelHeight = width / 4f;
        float shipPanelWidth = width - cargoPanelWidth - padding;
        float shipPanelHeight = height - infoPanelHeight - titlePanelHeight - padding;

        new TitlePanel(width, titlePanelHeight).render(panel);
        new ShipPanel(shipPanelWidth, shipPanelHeight, titlePanelHeight + padding + infoPanelHeight + padding).render(panel);
        new CargoPanel(cargoPanelWidth - padding, cargoPanelHeight, titlePanelHeight).render(panel);
        new InfoPanel(infoPanelWidth, infoPanelHeight, titlePanelHeight + padding).render(panel);
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
