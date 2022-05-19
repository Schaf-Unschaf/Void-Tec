package de.schafunschaf.voidtec.ids;

import lombok.Getter;

@Getter
public class VT_Settings {

    public static final int MAX_SLOTS = 6;
    public static int unlockedSlots = 1;
    public static boolean sheepDebug = false;
    public static boolean enableRemoveHullmodButton = false;
    public static boolean enableChangeSlotButton = false;
    public static boolean enablePlayerAugmentBattleDamage = true;
    public static boolean alwaysExpandTooltips = false;
    public static boolean randomSlotAmount = false;
    public static boolean hullmodInstallationWithSP = false;
    public static boolean sModPenalty = true;
    public static int installCostSP = 1;
    public static float installBaseValueFraction = 0.5f;
    public static int maxNumSlotsForCreditUnlock = 6;
    public static int removalCostSP = 1;
    public static int aiHullmodChance = 50;
    public static int aiSlotFillChance = 50;
    public static float statRollRangeModifier = 1f;
    public static int recoverChance = 30;
    public static int destroyChanceOnRecover = 75;
    public static int damageChanceOnRecover = 75;
    public static int repairCostCredits = 5_000;
    public static int partDisassemblePercentage = 25;
    public static int damageTakenThreshold = 15;
    public static int damageChanceOnDamageTaken = 30;
    public static float chanceReductionPerArmor = 0.07f;

    public static int basePartAmount = 5;
    public static float basicRepairMod = 3f;
    public static float primaryRepairMod = 2f;
    public static float secondaryRepairMod = 1f;
    public static float basicDisassembleMod = 3f;
    public static float primaryDisassembleMod = 2f;
    public static float secondaryDisassembleMod = 1f;

    public static boolean iconFlicker = true;
    public static boolean alternativeChestFlagDisplay = false;

    public static boolean isIndEvoActive;
}
