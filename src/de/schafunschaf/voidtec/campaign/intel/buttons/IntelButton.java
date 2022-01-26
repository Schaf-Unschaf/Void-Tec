package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface IntelButton {

    void buttonPressCancelled(IntelUIAPI ui);

    void buttonPressConfirmed(IntelUIAPI ui);

    void createConfirmationPrompt(TooltipMakerAPI tooltip);

    boolean doesButtonHaveConfirmDialog();

    String getConfirmText();

    String getCancelText();

    String getName();

    int getShortcut();
}
