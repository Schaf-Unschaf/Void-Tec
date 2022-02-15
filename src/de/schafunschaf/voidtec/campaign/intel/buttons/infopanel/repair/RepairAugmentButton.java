package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import lombok.RequiredArgsConstructor;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class RepairAugmentButton extends DefaultButton {

    private final AugmentApplier augment;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        augment.repairAugment(1);
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara("Repair %s?", 0f, augment.getAugmentQuality().getColor(), augment.getName());
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
        return "Repair";
    }

    @Override
    public ButtonAPI createButton(TooltipMakerAPI uiElement, float width, float height) {
        ButtonAPI button = ButtonUtils.addLabeledButton(uiElement, uiElement.computeStringWidth(getName()) + 10f, height, 0f,
                                                        Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), CutStyle.ALL, this);

        button.setEnabled(!isNull(augment) && augment.isRepairable());
        return button;
    }

    @Override
    protected void addTooltip(TooltipMakerAPI uiElement, SlotCategory slotCategory) {
        super.addTooltip(uiElement, slotCategory);
    }
}
