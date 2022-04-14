package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.campaign.intel.tabs.RepairTab;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class ShowSecondaryButton extends DefaultButton {

    private final boolean isHighlighted;
    private final boolean hasSecondaryStats;

    public ShowSecondaryButton(AugmentApplier selectedAugment) {
        isHighlighted = !isNull(selectedAugment) && !isNull(selectedAugment.getInstalledSlot()) && !selectedAugment.isInPrimarySlot();
        hasSecondaryStats = !selectedAugment.getSecondaryStatMods().isEmpty();
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        RepairTab.setShowSecondary(true);
    }

    @Override
    public String getName() {
        return "Show Secondary Stats";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        float buttonWidth = width != 0 ? width : tooltip.computeStringWidth(getName()) + 10f;
        Color textColor = isHighlighted ? Misc.getHighlightColor() : Misc.getBasePlayerColor();
        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, buttonWidth, height, 0f, textColor, Misc.getDarkPlayerColor(),
                                                         Misc.getDarkPlayerColor(), this);

        if (!hasSecondaryStats) {
            button.setEnabled(false);
            button.setChecked(false);
            RepairTab.setShowSecondary(false);
        } else {
            button.setChecked(RepairTab.isShowSecondary());
        }

        return button;
    }
}