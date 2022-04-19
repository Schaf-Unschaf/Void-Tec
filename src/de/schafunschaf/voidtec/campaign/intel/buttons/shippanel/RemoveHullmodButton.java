package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

public class RemoveHullmodButton extends DefaultButton {

    private final FleetMemberAPI fleetMember;

    public RemoveHullmodButton(FleetMemberAPI fleetMember) {
        this.fleetMember = fleetMember;
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        HullModManager hullModManager = HullModDataStorage.getInstance().getHullModManager(fleetMember.getId());

        hullModManager.removeHullMod(fleetMember);
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.addPara("Remove the Hullmod and destroy all installed Augments in the process?", 0f);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Remove";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        return "REMOVE";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color base = Misc.getBrightPlayerColor();
        Color bg = Misc.getDarkPlayerColor();

        return ButtonUtils.addLabeledButton(tooltip, width, height, 0f, base, bg, CutStyle.ALL,
                                            new RemoveHullmodButton(fleetMember));
    }
}
