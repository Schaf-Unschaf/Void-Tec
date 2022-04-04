package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsManager;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsUtility;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Strings;
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
        AugmentPartsUtility.repairAugment(augment, 1);
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        AugmentQuality nextQuality = augment.getAugmentQuality().getHigherQuality();
        tooltip.addPara("Repair %s (%s) and increase its quality to %s?", 0f,
                        new Color[]{Misc.getTextColor(), augment.getAugmentQuality().getColor(), nextQuality.getColor()},
                        augment.getName(), augment.getAugmentQuality().getName(), nextQuality.getName());

        tooltip.addPara("This will cost you %s in addition to the following materials:", 3f, Misc.getHighlightColor(),
                        Misc.getDGSCredits(VoidTecUtils.calcNeededCreditsForRepair(augment)));
        tooltip.addSpacer(3f);
        tooltip.setBulletedListMode(String.format("  %s ", VT_Strings.BULLET_CHAR));
        for (CraftingComponent component : AugmentPartsUtility.getComponentsForRepair(augment)) {
            Color amountColor = AugmentPartsUtility.hasEnough(component) ? Misc.getHighlightColor() : Misc.getGrayColor();
            Color compCatColor = isNull(component.getPartCategory()) ? Misc.getTextColor() : component.getPartCategory().getColor();
            int storedAmount = AugmentPartsManager.getInstance().getPart(component).getAmount();
            tooltip.addPara("%s %s %s-Parts (%s in storage)", 0f,
                            new Color[]{amountColor, nextQuality.getColor(), compCatColor, Misc.getHighlightColor()},
                            String.valueOf(component.getAmount()), nextQuality.getName(), component.getName(),
                            String.valueOf(storedAmount));
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Repair";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        return String.format("Repair Augment (%s)", repairAmount);
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        float buttonWidth = width != 0 ? width : tooltip.computeStringWidth(getName()) + 20f;

        return ButtonUtils.addLabeledButton(tooltip, buttonWidth, height, 0f,
                                            Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), CutStyle.TOP, this);
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
                return true;
            }

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return stringWidth;
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(tooltipString, 0f,
                                Misc.getHighlightColor(), String.valueOf(repairAmount));
                if (expanded) {
                    tooltip.addPara("Repair is free.. for now", Misc.getGrayColor(), 6f);
                }
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }
}
