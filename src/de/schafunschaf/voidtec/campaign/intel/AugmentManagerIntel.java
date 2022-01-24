package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.VT_Colors;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;

public class AugmentManagerIntel extends BaseIntel {
    public static final String STACK_SOURCE = "augmentManagerIntel";

    @Getter
    @Setter
    private static AugmentCargoWrapper selectedAugmentInCargo;
    @Getter
    @Setter
    private static SlotCategory activeCategoryFilter;
    private float titleSize = 0f;
    //    private float shipListSize = 0f;
    private float shipListWidth = 0f;
//    private float cargoListSize = 0f;

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        addTitlePanel(panel, width, height);
        addWelcomeText(panel, width, height);
        addTabs(panel, width, height);
        shipListWidth = ShipPanel.addShipListPanel(panel, height, titleSize + 10f);
        CargoPanel.addAugmentsInCargoPanel(panel, width - shipListWidth, height, titleSize + 10f);
    }

    private void addTitlePanel(CustomPanelAPI panel, float width, float height) {
        TooltipMakerAPI uiElement = panel.createUIElement(width, height, false);
        LabelAPI sectionHeading = uiElement.addSectionHeading("", Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(), Alignment.MID, 0f);
        titleSize = sectionHeading.getPosition().getHeight();
        uiElement.addPara("VESAI - VoidTec Engineering Suite Augmentation Interface", 0f, Misc.getBrightPlayerColor(), Misc.getHighlightColor(), "VESAI", "V", "E", "S", "A", "I").setAlignment(Alignment.MID);
        PositionAPI textPosition = uiElement.getPrev().getPosition();
        textPosition.setYAlignOffset(textPosition.getHeight() + 2f);

        panel.addUIElement(uiElement);
    }

    private void addWelcomeText(CustomPanelAPI panel, float width, float height) {

    }

    private void addTabs(CustomPanelAPI panel, float width, float height) {

    }

    @Override
    public Color getTitleColor(ListInfoMode mode) {
        return VT_Colors.VT_COLOR_MAIN;
    }

    @Override
    protected String getName() {
        return "VESAI";
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "vt_vesai_icon");
    }

    @Override
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    @Override
    public boolean isImportant() {
        return true;
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
    public void notifyPlayerAboutToOpenIntelScreen() {
        selectedAugmentInCargo = null;
        activeCategoryFilter = null;
    }

    @Override
    public void reportPlayerClickedOn() {
        super.reportPlayerClickedOn();
    }
}
