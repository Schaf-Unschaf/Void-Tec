package de.schafunschaf.voidtec.campaign.intel.buttons.infopanel.repair;

import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.campaign.intel.tabs.RepairTab;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

public class ShowPrimaryButton extends DefaultButton {

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        RepairTab.setShowSecondary(false);
    }

    @Override
    public String getName() {
        return "Show Primary Stats";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        float buttonWidth = width != 0 ? width : tooltip.computeStringWidth(getName()) + 10f;
        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, buttonWidth, height, 0f,
                                                         Misc.getBasePlayerColor(), Misc.getDarkPlayerColor(), Misc.getDarkPlayerColor(),
                                                         this);
        button.setChecked(!RepairTab.isShowSecondary());

        return button;
    }
}