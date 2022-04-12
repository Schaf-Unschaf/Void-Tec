package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsUtility;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class DismantleAugmentButton extends DefaultButton {

    private final AugmentApplier augment;
    private final AugmentCargoWrapper augmentCargoWrapper;

    public DismantleAugmentButton(AugmentApplier augment) {
        this.augment = augment;
        this.augmentCargoWrapper = null;
    }

    public DismantleAugmentButton(AugmentCargoWrapper augmentCargoWrapper) {
        this.augmentCargoWrapper = augmentCargoWrapper;
        this.augment = augmentCargoWrapper.getAugment();
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (!(!isNull(augmentCargoWrapper) && augment.getAugmentQuality() == AugmentQuality.CUSTOMISED)) {
            if (!isNull(augmentCargoWrapper)) {
                AugmentPartsUtility.disassembleAugment(augmentCargoWrapper);
                AugmentManagerIntel.setSelectedAugmentInCargo(null);
            } else {
                AugmentPartsUtility.disassembleAugment(augment);
                AugmentManagerIntel.setSelectedInstalledAugment(null);
            }
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara(String.format("%s %s?", getName(), augment.getName()), 0f, augment.getAugmentQuality().getColor(),
                        augment.getName());
        if (augment.getAugmentQuality() == AugmentQuality.DEGRADED || augment.isDestroyed()) {
            tooltip.addPara("This Augment is heavily damaged and won't return any usable components.", Misc.getNegativeHighlightColor(),
                            10f);
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return !(!isNull(augmentCargoWrapper) && augment.getAugmentQuality() == AugmentQuality.CUSTOMISED);
    }

    @Override
    public String getConfirmText() {
        return getName();
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        if (!isNull(augmentCargoWrapper) && augment.getAugmentQuality() == AugmentQuality.CUSTOMISED) {
            return "------";
        } else if (augment.getAugmentQuality() == AugmentQuality.DEGRADED || augment.isDestroyed() || augment.getAugmentQuality() == AugmentQuality.CUSTOMISED) {
            return "Remove";
        } else {
            return "Dismantle";
        }
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color bgColor = Misc.scaleColorOnly(Misc.getNegativeHighlightColor(), 0.25f);
        return ButtonUtils.addLabeledButton(tooltip, 90, height, 0f, Misc.getNegativeHighlightColor(), bgColor, CutStyle.BL_TR,
                                            this);
    }
}
