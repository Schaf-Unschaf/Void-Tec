package de.schafunschaf.voidtec.campaign.intel.buttons;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.helper.VoidTecUtils;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.util.FormattingTools;
import lombok.RequiredArgsConstructor;

import java.awt.*;

import static de.schafunschaf.voidtec.Settings.*;

@RequiredArgsConstructor
public class InstallHullmodButton implements IntelButton {
    private final FleetMemberAPI fleetMember;

    @Override
    public void buttonPressCancelled(IntelUIAPI ui) {

    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        float hullSizeMult = Misc.getSizeNum(fleetMember.getHullSpec().getHullSize());
        ShipVariantAPI memberVariant = fleetMember.getVariant();
        memberVariant.clearPermaMods();

        memberVariant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);

        if (hullmodInstallationWithSP)
            Global.getSector().getPlayerStats().addStoryPoints(-installCostSP);
        else
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCostCredits * hullSizeMult);
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        float hullSizeMult = Misc.getSizeNum(fleetMember.getHullSpec().getHullSize());
        String installCost = Misc.getDGSCredits(installCostCredits * hullSizeMult);
        Color hlColor = Misc.getHighlightColor();
        if (hullmodInstallationWithSP) {
            installCost = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
            hlColor = Misc.getStoryOptionColor();
        }

        tooltip.addPara("Do you want to install the VoidTec Engineering Suite on your ship?", 0f);
        tooltip.addPara(String.format("This will cost you %s and remove all installed permanent hullmods.", installCost), 3f, hlColor, installCost);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Install";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        String buttonText;
        float hullSizeMult = Misc.getSizeNum(fleetMember.getHullSpec().getHullSize());

        if (VoidTecUtils.isPlayerDockedAtSpaceport())
            if (VoidTecUtils.canPayForInstallation(hullSizeMult))
                buttonText = "Install VESAI";
            else
                buttonText = "Not enough " + (hullmodInstallationWithSP ? "SP" : "credits");
        else
            buttonText = "Need Spaceport";

        return buttonText;
    }

    @Override
    public int getShortcut() {
        return 0;
    }
}
