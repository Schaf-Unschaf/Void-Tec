package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.crafting.AugmentPartsUtility;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import de.schafunschaf.voidtec.util.ui.lwjgl.TextUtil;
import de.schafunschaf.voidtec.util.ui.plugins.BasePanelPlugin;
import lombok.SneakyThrows;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class DismantleAugmentButton extends DefaultButton {

    private final AugmentApplier augment;
    private final AugmentCargoWrapper augmentCargoWrapper;
    private final boolean canNotBeDismantled;
    private TextFieldAPI dismantleAmountField;

    public DismantleAugmentButton(AugmentApplier augment) {
        this.augment = augment;
        this.augmentCargoWrapper = null;
        this.canNotBeDismantled = augment.isDestroyed()
                || augment.getAugmentQuality() == AugmentQuality.CUSTOMISED
                || augment.getInitialQuality() == AugmentQuality.DEGRADED;
    }

    public DismantleAugmentButton(AugmentCargoWrapper augmentCargoWrapper) {
        this.augmentCargoWrapper = augmentCargoWrapper;
        this.augment = augmentCargoWrapper.getAugment();
        this.canNotBeDismantled = augment.isDestroyed()
                || augment.getAugmentQuality() == AugmentQuality.CUSTOMISED
                || augment.getInitialQuality() == AugmentQuality.DEGRADED;
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (!(!isNull(augmentCargoWrapper) && augment.getAugmentQuality() == AugmentQuality.CUSTOMISED)) {
            if (!isNull(augmentCargoWrapper)) {
                AugmentPartsUtility.dismantleAugment(augmentCargoWrapper, FormattingTools.parseInteger(dismantleAmountField.getText(), 0));
                AugmentManagerIntel.setSelectedAugmentInCargo(null);
            } else {
                AugmentPartsUtility.dismantleAugment(augment);
                AugmentManagerIntel.setSelectedInstalledAugment(null);
            }
        }
    }

    @Override
    public void createConfirmationPrompt(final TooltipMakerAPI tooltip) {
        tooltip.setForceProcessInput(true);

        tooltip.addPara(String.format("%s %s?", getName(), augment.getName()), 0f, augment.getAugmentQuality().getColor(),
                        augment.getName());

        if (AugmentPartsUtility.getComponentsForDismantling(augment).isEmpty()) {
            tooltip.addPara("This augment appears to have no usable components left to salvage.", Misc.getGrayColor(), 10f);
            tooltip.addPara("Consider selling instead of trashing it to get some pocket change.", Misc.getGrayColor(), 3f);
        } else {
            if (!isNull(augmentCargoWrapper)) {
                String text = "Amount to dismantle:";
                tooltip.addPara(text, 20f);
                UIComponentAPI prev = tooltip.getPrev();

                int stackSize = (int) augmentCargoWrapper.getAugmentCargoStack().getSize();
                dismantleAmountField = UIUtils.addNumberField(tooltip, 45, 30, 0, stackSize,
                                                              -prev.getPosition().getHeight() - 3);
                dismantleAmountField.setUndoOnEscape(true);
                dismantleAmountField.setMaxChars(3);
                dismantleAmountField.setBorderColor(Misc.getDarkPlayerColor());
                dismantleAmountField.setBgColor(new Color(0, 0, 0, 0));
                dismantleAmountField.setLimitByStringWidth(false);
                dismantleAmountField.hideCursor();
                dismantleAmountField.setMidAlignment();
                dismantleAmountField.setText("1");
                float xAlignOffset = tooltip.computeStringWidth(text) - 5;
                tooltip.getPrev().getPosition().setXAlignOffset(xAlignOffset);
                tooltip.addPara("/ " + stackSize, -26).getPosition().setXAlignOffset(50);
                tooltip.addSpacer(0f).getPosition().setXAlignOffset(-xAlignOffset - 50);
            }

            tooltip.addPara("This will give you the following components:", 10f, Misc.getHighlightColor(),
                            Misc.getDGSCredits(VoidTecUtils.calcNeededCreditsForRepair(augment)));
            tooltip.addSpacer(3f);

            tooltip.setBulletedListMode(String.format(" %s ", VT_Strings.BULLET_CHAR));
            for (final CraftingComponent component : AugmentPartsUtility.getComponentsForDismantling(augment)) {
                final Color compCatColor = isNull(component.getPartCategory())
                                           ? Misc.getTextColor()
                                           : component.getPartCategory().getColor();
                tooltip.setBulletColor(component.getPartQuality().getColor());
                final String text = String.format("%s %s-Parts:", component.getPartQuality().getName(), component.getName());
                tooltip.addPara(text, 3f,
                                new Color[]{component.getPartQuality().getColor(), compCatColor, Misc.getHighlightColor()},
                                component.getPartQuality().getName(), component.getName());

                CustomPanelAPI customPanel = Global.getSettings().createCustom(30, 30, new BasePanelPlugin() {
                    @SneakyThrows
                    @Override
                    public void render(float alphaMult) {
                        int amount = isNull(dismantleAmountField) ? 1 : Integer.parseInt(dismantleAmountField.getText());
                        int sumPart = component.getAmount() * amount;
                        TextUtil.drawString(p.getX() + tooltip.computeStringWidth(text) + 30, p.getY() + 20, String.valueOf(sumPart),
                                            Fonts.INSIGNIA_VERY_LARGE, 20, Misc.getHighlightColor());
                    }
                });
                tooltip.addCustom(customPanel, -30f);
            }
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return augment.getAugmentQuality() != AugmentQuality.CUSTOMISED;
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
        if (augment.getAugmentQuality() == AugmentQuality.CUSTOMISED) {
            return "------";
        } else {
            if (canNotBeDismantled) {
                return "Remove";
            } else {
                return "Dismantle";
            }
        }
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color bgColor = Misc.scaleColorOnly(Misc.getNegativeHighlightColor(), 0.25f);
        return ButtonUtils.addLabeledButton(tooltip, 90, height, 0f, Misc.getNegativeHighlightColor(), bgColor, CutStyle.BL_TR,
                                            this);
    }
}
