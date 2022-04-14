package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.IntelUIAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.ids.VT_Settings.*;

public class InstallHullmodButton extends DefaultButton {

    private final FleetMemberAPI fleetMember;
    private final float hullSizeMult;

    public InstallHullmodButton(FleetMemberAPI fleetMember) {
        this.fleetMember = fleetMember;
        this.hullSizeMult = Misc.getSizeNum(fleetMember.getHullSpec().getHullSize());
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        ShipVariantAPI memberVariant = fleetMember.getVariant();
        MutableCharacterStatsAPI playerStats = Global.getSector().getPlayerStats();
        long bonusXP = VoidTecUtils.getBonusXPForInstalling(fleetMember);
        playerStats.addBonusXP(bonusXP, false, null, false);

        memberVariant.clearPermaMods();

        memberVariant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
        if (hullmodInstallationWithSP) {
            playerStats.addStoryPoints(-installCostSP);
        } else {
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCostCredits * hullSizeMult);
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        String installCost = Misc.getDGSCredits(installCostCredits * hullSizeMult);
        Color hlColor = Misc.getHighlightColor();
        int bonusPercent = VoidTecUtils.getBonusXPPercentage(VoidTecUtils.getBonusXPForInstalling(fleetMember));
        if (hullmodInstallationWithSP) {
            installCost = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
            hlColor = Misc.getStoryOptionColor();
        }

        tooltip.addPara("Do you want to install the VoidTec Engineering Suite on your ship?", 0f);
        tooltip.addPara(String.format("This will cost %s and remove all installed SMods.", installCost), 6f, hlColor,
                        installCost);
        if (bonusPercent > 0) {
            String bonusString = String.format("%s%% Bonus XP", bonusPercent);
            tooltip.addPara("You will gain an additional %s for the removed SMods as compensation.", 6f, Misc.getStoryOptionColor(),
                            bonusString);
        }
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

        if (VoidTecUtils.isPlayerDockedAtSpaceport()) {
            if (VoidTecUtils.canPayForInstallation(hullSizeMult)) {
                buttonText = "Install VESAI";
            } else {
                buttonText = "Not enough " + (hullmodInstallationWithSP ? "SP" : "credits");
            }
        } else {
            buttonText = "Need Spaceport";
        }

        return buttonText;
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        boolean spEnabled = VT_Settings.hullmodInstallationWithSP;
        Color hlColor = spEnabled ? Misc.getStoryOptionColor() : Misc.getHighlightColor();
        String highlight = spEnabled
                           ? String.format("%s SP", VT_Settings.installCostSP)
                           : Misc.getDGSCredits(VT_Settings.installCostCredits * hullSizeMult);
        tooltip.addPara("Installation cost: %s", 6f, Misc.getGrayColor(), hlColor, highlight);

        Color base = spEnabled ? Misc.getStoryBrightColor() : Misc.getBrightPlayerColor();
        Color bg = spEnabled ? Misc.getStoryDarkColor() : Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addLabeledButton(tooltip, width, height, 0f, base, bg, CutStyle.C2_MENU,
                                                        new InstallHullmodButton(fleetMember));
        button.setEnabled(canInstall());

        return button;
    }

    private boolean canInstall() {
        return VoidTecUtils.isPlayerDockedAtSpaceport() && VoidTecUtils.canPayForInstallation(hullSizeMult);
    }
}
