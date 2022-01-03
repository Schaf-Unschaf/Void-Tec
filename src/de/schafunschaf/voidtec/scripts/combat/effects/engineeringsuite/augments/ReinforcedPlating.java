package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments;

import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.SlotCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatProvider;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.VT_StatModKeys;

import java.util.Arrays;
import java.util.List;

public class ReinforcedPlating extends BaseAugment {
    public static final String AUGMENT_ID = "reinforcedPlating";

    public ReinforcedPlating() {
        this(AUGMENT_ID,
                null,
                "Reinforced Plating",
                "Increases the ships overall durability",
                null,
                null,
                0,
                SlotCategory.STRUCTURE,
                Arrays.asList(SlotCategory.UNIVERSAL, SlotCategory.ENGINE),
                Arrays.asList(
                        StatProvider.getStatMod(VT_StatModKeys.ARMOR_HEALTH),
                        StatProvider.getStatMod(VT_StatModKeys.HULL_HEALTH),
                        StatProvider.getStatMod(VT_StatModKeys.SHIP_TURN)
                ),
                Arrays.asList(
                        new StatModValue<>(15f, 30f, true),
                        new StatModValue<>(15f, 30f, true),
                        new StatModValue<>(-15f, -40f, false)
                ),
                Arrays.asList(
                        StatProvider.getStatMod(VT_StatModKeys.ENGINE_HEALTH),
                        StatProvider.getStatMod(VT_StatModKeys.ENGINE_REPAIR)
                ),
                Arrays.asList(
                        new StatModValue<>(20f, 30f, true),
                        new StatModValue<>(-10f, -20f, true)
                ),
                null
        );
    }

    public ReinforcedPlating(String augmentID, String manufacturer, String name, String description, String primaryStatDescription, String secondaryStatDescription, int rarity, SlotCategory primarySlot, List<SlotCategory> secondarySlots, List<BaseStatMod> primaryStatMods, List<StatModValue<Float, Float, Boolean>> primaryStatValues, List<BaseStatMod> secondaryStatMods, List<StatModValue<Float, Float, Boolean>> secondaryStatValues, UpgradeQuality upgradeQuality) {
        super(augmentID, manufacturer, name, description, primaryStatDescription, secondaryStatDescription, rarity, primarySlot, secondarySlots, primaryStatMods, primaryStatValues, secondaryStatMods, secondaryStatValues, upgradeQuality);
    }
}
