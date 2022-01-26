package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentSlot;
import de.schafunschaf.voidtec.util.FormattingTools;
import lombok.RequiredArgsConstructor;

import java.awt.Color;

import static de.schafunschaf.voidtec.VT_Settings.removalCostSP;

@RequiredArgsConstructor
public class FilledSlotButton extends EmptySlotButton {

    private final AugmentSlot augmentSlot;

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();

        String bullet = "• ";
        String removalCost = String.format("%s Story " + FormattingTools.singularOrPlural(removalCostSP, "Point"), removalCostSP);
        Color hlColor = Misc.getStoryOptionColor();
        tooltip.addPara("Do you want to remove this augment?", 0f);
        tooltip.addPara(bullet + slottedAugment.getName(), slottedAugment.getAugmentQuality().getColor(), 10f);
        tooltip.addPara(String.format("This will cost you %s", removalCost), 10f, hlColor, removalCost);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Remove";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }
}
