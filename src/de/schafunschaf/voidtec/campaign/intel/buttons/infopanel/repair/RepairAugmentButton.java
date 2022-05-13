package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsManager;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsUtility;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class RepairAugmentButton extends DefaultButton {

    private final AugmentApplier augment;
    private final int repairAmount;

    public RepairAugmentButton(AugmentApplier augment) {
        this.augment = augment;
        this.repairAmount = isNull(augment) ? 0 : augment.getInitialQuality().ordinal() - augment.getAugmentQuality().ordinal();
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (augment.isRepairable() && AugmentPartsUtility.canRepairAugment(augment)) {
            if (augment.isInstalled()) {
                AugmentPartsUtility.repairAugment(augment, 1);
            } else {
                CargoAPI sourceStorage = CargoUtils.removeAugmentFromStorage(augment);
                AugmentApplier cloneAugment = AugmentDataManager.cloneAugment(this.augment);
                cloneAugment.repairAugment(1);
                CargoUtils.addAugmentToCargo(cloneAugment, sourceStorage);
            }
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentQuality nextQuality = augment.getAugmentQuality().getHigherQuality();
        tooltip.addPara("Repair %s (%s) and increase its quality to %s?", 0f,
                        new Color[]{Misc.getTextColor(), augment.getAugmentQuality().getColor(), nextQuality.getColor()},
                        augment.getName(), augment.getAugmentQuality().getName(), nextQuality.getName());

        tooltip.addPara("This will cost you %s in addition to the following materials:", 10f, Misc.getHighlightColor(),
                        Misc.getDGSCredits(VoidTecUtils.calcNeededCreditsForRepair(augment)));
        tooltip.addSpacer(3f);
        tooltip.setBulletedListMode(String.format(" %s ", VT_Strings.BULLET_CHAR));
        for (CraftingComponent component : AugmentPartsUtility.getComponentsForRepair(augment)) {
            Color hasEnoughColor = AugmentPartsUtility.hasEnough(component) ? Misc.getPositiveHighlightColor() : Color.RED;
            Color compCatColor = isNull(component.getPartCategory()) ? Misc.getTextColor() : component.getPartCategory().getColor();
            int storedAmount = AugmentPartsManager.getInstance().getPart(component).getAmount();
            tooltip.setBulletColor(hasEnoughColor);
            tooltip.addPara("%s %s %s-Parts (%s in storage)", 0f,
                            new Color[]{Misc.getHighlightColor(), nextQuality.getColor(), compCatColor, Misc.getHighlightColor()},
                            String.valueOf(component.getAmount()), nextQuality.getName(), component.getName(),
                            String.valueOf(storedAmount));
        }

        if (!AugmentPartsUtility.canRepairAugment(augment)) {
            tooltip.setBulletedListMode("");
            tooltip.addPara("You need more materials to perform the repair.", Misc.getNegativeHighlightColor(), 10f);
            tooltip.setParaFontDefault();
            tooltip.addPara("To get more parts, try dismantling no longer needed Augments.", Misc.getGrayColor(), 3f).italicize();
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return augment.isRepairable();
    }

    @Override
    public String getConfirmText() {
        if (AugmentPartsUtility.canRepairAugment(augment)) {
            return "Repair";
        } else {
            return "Return";
        }
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        return augment.isRepairable() ? String.format("Repair (%s)", repairAmount) : "------";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color bgColor = Misc.scaleColorOnly(Misc.getPositiveHighlightColor(), 0.25f);

        return ButtonUtils.addLabeledButton(tooltip, 90, height, 0f,
                                            Misc.getPositiveHighlightColor(), bgColor, CutStyle.TL_BR, this);
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {
        final String tooltipString = String.format("You will need to repair this Augment %s\n" +
                                                           "more %s to restore its full functionality.",
                                                   repairAmount, FormattingTools.singularOrPlural(repairAmount, "time"));
        final float stringWidth = tooltip.computeStringWidth(tooltipString);

        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            @Override
            public boolean isTooltipExpandable(Object tooltipParam) {
                return false;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return stringWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipString, 0f, Misc.getHighlightColor(), String.valueOf(repairAmount));
                if (expanded) {
                    tooltip.addPara("Repair is free.. for now", Misc.getGrayColor(), 6f);
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }
}
