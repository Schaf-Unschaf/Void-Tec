package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.items.chests.BaseChestData;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Items;
import de.schafunschaf.voidtec.ids.VT_Settings;
import lombok.SneakyThrows;

import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Random;

import static de.schafunschaf.voidtec.ids.VT_Settings.*;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VoidTecUtils {

    public static Color getManufacturerColor(String manufacturerString) {
        FactionAPI faction = Global.getSector().getFaction(manufacturerString.toLowerCase());
        Color manufacturerColor = isNull(faction) ? Global.getSettings().getDesignTypeColor(manufacturerString) : faction.getColor();

        return isNull(manufacturerColor) ? Misc.getGrayColor() : manufacturerColor;
    }

    public static boolean isPlayerDockedAtSpaceport() {
        return Global.getSector().hasTransientScript(VT_DockedAtSpaceportHelper.class);
    }

    public static boolean canPayForInstallation(float hullSizeMult) {
        if (hullmodInstallationWithSP) {
            return Global.getSector().getPlayerStats().getStoryPoints() >= installCostSP;
        }

        return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCostCredits * hullSizeMult;
    }

    public static void addAugmentToFleetCargo(AugmentApplier augment) {
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, augment), 1);
    }

    public static void addRandomAugmentsToFleetCargo(Random random, int numPerItem, int quantity) {
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        for (int i = 0; i < quantity; i++) {
            cargo.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, AugmentDataManager.getRandomAugment(random)), numPerItem);
        }
    }

    public static void addChestToFleetCargo(BaseChestData baseChestData) {
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        cargo.addSpecial(baseChestData, 1f);
    }

    public static float calcNeededCreditsForRepair(AugmentApplier augment) {
        AugmentQuality higherQuality = augment.getAugmentQuality().getHigherQuality();
        return (float) MathUtils.roundWholeNumber(VT_Settings.repairCostCredits * Math.pow(higherQuality.getModifier(), 3), 3);
    }

    @SneakyThrows
    public static void openDialogPlugin(EveryFrameScript transientScript) {
        Robot robot = new Robot();
        Global.getSector().addTransientScript(transientScript);
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }
}
