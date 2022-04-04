package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class DefaultButton implements IntelButton {

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {

    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return false;
    }

    @Override
    public String getConfirmText() {
        return null;
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getShortcut() {
        return 0;
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        return null;
    }

    @Override
    public void addTooltip(TooltipMakerAPI tooltip) {}
}
