package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.BaseTooltipCreator;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.ids.VT_Settings.removalCostSP;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class FilledSlotButton extends DefaultButton {

    private final AugmentApplier augment;
    private final FleetMemberAPI fleetMember;

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentApplier slottedAugment = augment;

        String bullet = "• ";
        String removalCost = String.format("%s Story " + FormattingTools.singularOrPlural(removalCostSP, "Point"), removalCostSP);
        Color hlColor = Misc.getStoryOptionColor();
        tooltip.addPara("Do you want to remove this augment?", 0f);
        tooltip.addPara(bullet + slottedAugment.getName(), slottedAugment.getAugmentQuality().getColor(), 10f);
        tooltip.addPara(String.format("This will cost you %s", removalCost), 10f, hlColor, removalCost);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getConfirmText() {
        return "Remove";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        SlotCategory slotCategory = augment.getInstalledSlot().getSlotCategory();
        Color slotColor = slotCategory.getColor();
        float scaleFactor;

        AugmentCargoWrapper selectedAugment = AugmentManagerIntel.getSelectedAugmentInCargo();
        SlotCategory activeCategoryFilter = AugmentManagerIntel.getActiveCategoryFilter();
        boolean isFiltered = activeCategoryFilter == slotCategory;

        if (!isNull(selectedAugment)) {
            scaleFactor = 0.1f;
        } else if (!isNull(activeCategoryFilter)) {
            scaleFactor = isFiltered ? 0.5f : 0.1f; // No Augment selected but with filter. Will highlight prim and sec slots
        } else {
            scaleFactor = 1f; // No Augment selected and no filter
        }

        Color buttonColor = Misc.scaleColorOnly(slotColor, scaleFactor);

        ButtonAPI button = ButtonUtils.addAugmentButton(uiElement, height, 0f, buttonColor, buttonColor,
                                                        this);

        addTooltip(uiElement, slotCategory);

        return button;
    }

    @Override
    protected void addTooltip(TooltipMakerAPI uiElement, final SlotCategory slotCategory) {
        uiElement.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                augment.generateTooltip(fleetMember.getStats(), VoidTecEngineeringSuite.HULL_MOD_ID, tooltip, getTooltipWidth(this),
                                        slotCategory, false, null);
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }
}
