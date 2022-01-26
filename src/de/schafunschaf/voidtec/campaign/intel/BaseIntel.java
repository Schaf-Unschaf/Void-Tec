package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;

public class BaseIntel extends BaseIntelPlugin {

    @Override
    public boolean doesButtonHaveConfirmDialog(Object buttonId) {
        if (buttonId instanceof IntelButton) {
            return ((IntelButton) buttonId).doesButtonHaveConfirmDialog();
        }

        return false;
    }

    @Override
    public void createConfirmationPrompt(Object buttonId, TooltipMakerAPI prompt) {
        if (buttonId instanceof IntelButton) {
            ((IntelButton) buttonId).createConfirmationPrompt(prompt);
        }
    }

    @Override
    public String getConfirmText(Object buttonId) {
        if (buttonId instanceof IntelButton) {
            return ((IntelButton) buttonId).getConfirmText();
        }

        return "Confirm";
    }

    @Override
    public String getCancelText(Object buttonId) {
        if (buttonId instanceof IntelButton) {
            return ((IntelButton) buttonId).getCancelText();
        }

        return "Cancel";
    }

    @Override
    public void buttonPressConfirmed(Object buttonId, IntelUIAPI ui) {
        if (buttonId instanceof IntelButton) {
            ((IntelButton) buttonId).buttonPressConfirmed(ui);
        }

        Global.getSector().getPlayerFleet().getFleetData().setSyncNeeded();
        Global.getSector().getPlayerFleet().getFleetData().syncMemberLists();
        ui.updateUIForItem(this);
    }

    @Override
    public void buttonPressCancelled(Object buttonId, IntelUIAPI ui) {
    }
}
