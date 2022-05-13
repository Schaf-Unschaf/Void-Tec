package de.schafunschaf.voidtec.util;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.imported.WelcomeMessageLoader;
import lombok.SneakyThrows;

import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;

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

    public static boolean canPayForInstallation(float installCost) {
        return Global.getSector().getPlayerFleet().getCargo().getCredits().get() >= installCost;
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

    public static boolean checkIfFighterStat(AugmentApplier augment) {
        AugmentSlot installedSlot = augment.getInstalledSlot();
        return !isNull(installedSlot) && installedSlot.getSlotCategory() == SlotCategory.FLIGHT_DECK
                || augment.getPrimarySlot() == SlotCategory.FLIGHT_DECK;
    }

    public static long getBonusXPForInstalling(FleetMemberAPI fleetMember) {
        int sModsToRemove = getSModsToRemove(fleetMember);
        long xpForLevelUp = Global.getSettings().getLevelupPlugin().getXPForLevel(Global.getSector().getPlayerStats().getLevel() + 1);
        return xpForLevelUp / 3 * Math.max(sModsToRemove, 0);
    }

    public static int getSModsToRemove(FleetMemberAPI fleetMember) {
        int numSMods = fleetMember.getVariant().getSMods().size();
        boolean hasBestOfTheBest = Global.getSector().getPlayerStats().getSkillLevel(Skills.BEST_OF_THE_BEST) > 0;
        return hasBestOfTheBest ? numSMods - 1 : numSMods;
    }

    public static int getBonusXPPercentage(long bonusXP) {
        long xpForLevelUp = Global.getSettings().getLevelupPlugin().getXPForLevel(Global.getSector().getPlayerStats().getLevel() + 1);
        return Math.round(100f / xpForLevelUp * bonusXP);
    }

    public static String getRandomIntelMessage() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();

        for (Pair<String, Float> pair : WelcomeMessageLoader.getMessageList()) {
            picker.add(pair.one, pair.two);
        }

        return picker.pick();
    }
}
