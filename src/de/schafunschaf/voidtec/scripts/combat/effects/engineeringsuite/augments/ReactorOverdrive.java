package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments;

import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.SlotCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatProvider;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.VT_StatModKeys;

import java.util.Arrays;
import java.util.List;

public class ReactorOverdrive extends BaseAugment {
    public static final String AUGMENT_ID = "reactorOverdrive";

    public ReactorOverdrive() {
        this(AUGMENT_ID,
                null,
                "Reactor Overdrive",
                "Removes 'unnecessary' safety locks from the ships flux grid. This will put the reactor under more stress in exchange for a higher flux throughput.",
                null,
                null,
                0,
                SlotCategory.REACTOR,
                Arrays.asList(SlotCategory.UNIVERSAL, SlotCategory.SYSTEM),
                Arrays.asList(
                        StatProvider.getStatMod(VT_StatModKeys.FLUX_DISSIPATION),
                        StatProvider.getStatMod(VT_StatModKeys.VENT_RATE),
                        StatProvider.getStatMod(VT_StatModKeys.PEAK_CR)
                ),
                Arrays.asList(
                        new StatModValue<>(20f, 40f, true),
                        new StatModValue<>(10f, 25f, true),
                        new StatModValue<>(-15f, -30f, true)
                ),
                Arrays.asList(
                        StatProvider.getStatMod(VT_StatModKeys.SYSTEM_REGEN),
                        StatProvider.getStatMod(VT_StatModKeys.SYSTEM_COOLDOWN),
                        StatProvider.getStatMod(VT_StatModKeys.OVERLOAD_DURATION)
                ),
                Arrays.asList(
                        new StatModValue<>(10f, 20f, true),
                        new StatModValue<>(10f, 20f, true),
                        new StatModValue<>(15f, 30f, false)
                ),
                null
        );
    }

    public ReactorOverdrive(String augmentID, String manufacturer, String name, String description, String primaryStatDescription, String secondaryStatDescription, int rarity, SlotCategory primarySlot, List<SlotCategory> secondarySlots, List<BaseStatMod> primaryStatMods, List<StatModValue<Float, Float, Boolean>> primaryStatValues, List<BaseStatMod> secondaryStatMods, List<StatModValue<Float, Float, Boolean>> secondaryStatValues, UpgradeQuality upgradeQuality) {
        super(augmentID, manufacturer, name, description, primaryStatDescription, secondaryStatDescription, rarity, primarySlot, secondarySlots, primaryStatMods, primaryStatValues, secondaryStatMods, secondaryStatValues, upgradeQuality);
    }
}