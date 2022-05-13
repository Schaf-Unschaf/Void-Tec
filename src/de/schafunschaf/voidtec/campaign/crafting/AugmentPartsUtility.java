package de.schafunschaf.voidtec.campaign.crafting;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.campaign.crafting.parts.AugmentComponent;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentPartsUtility {

    private static final float BASIC_CRAFT_MOD = 1;
    private static final float PRIMARY_CRAFT_MOD = 3;
    private static final float SECONDARY_CRAFT_MOD = 2;

    public static List<CraftingComponent> getComponentsForRepair(AugmentApplier augment) {
        List<CraftingComponent> neededComponents = new ArrayList<>();
        if (!augment.isRepairable()) {
            return neededComponents;
        }

        Random random = new Random(augment.hashCode());
        AugmentQuality higherQuality = augment.getAugmentQuality().getHigherQuality();
        float qualityModifier = higherQuality.getModifier();

        int baseCost = (int) Math.ceil(VT_Settings.basePartAmount * VT_Settings.basicRepairMod * qualityModifier);
        AugmentComponent baseComponent = new AugmentComponent(AugmentPartsManager.BASIC_COMPONENT, higherQuality);
        baseComponent.addAmount(baseCost + random.nextInt(Math.max(baseCost / 2, 1)));
        neededComponents.add(baseComponent);

        int primaryCost = (int) Math.ceil(VT_Settings.basePartAmount * VT_Settings.primaryRepairMod * qualityModifier);
        AugmentComponent primaryComponent = new AugmentComponent(augment.getPrimarySlot(), higherQuality);
        primaryComponent.addAmount(primaryCost + random.nextInt(Math.max(primaryCost / 2, 1)));
        neededComponents.add(primaryComponent);

        for (SlotCategory secondarySlot : augment.getSecondarySlots()) {
            int secondaryCost = (int) Math.ceil(
                    VT_Settings.basePartAmount * VT_Settings.secondaryRepairMod * qualityModifier / augment.getSecondarySlots().size());
            AugmentComponent secondaryComponent = new AugmentComponent(secondarySlot, higherQuality);
            secondaryComponent.addAmount(secondaryCost + random.nextInt(Math.max(secondaryCost / 2, 1)));
            neededComponents.add(secondaryComponent);
        }

        return neededComponents;
    }

    public static boolean canRepairAugment(AugmentApplier augment) {
        if (isNull(augment)) {
            return false;
        }

        if (Global.getSector().getPlayerFleet().getCargo().getCredits().get() < VoidTecUtils.calcNeededCreditsForRepair(augment)) {
            return false;
        }

        for (CraftingComponent component : getComponentsForRepair(augment)) {
            if (!hasEnough(component)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasEnough(CraftingComponent component) {
        CraftingComponent compFromInventory = AugmentPartsManager.getInstance().getPart(component);
        if (isNull(compFromInventory)) {
            return false;
        }

        return compFromInventory.getAmount() >= component.getAmount();
    }

    public static void repairAugment(AugmentApplier augment, int numLevels) {
        for (int i = 0; i < numLevels; i++) {
            if (canRepairAugment(augment)) {
                for (CraftingComponent component : getComponentsForRepair(augment)) {
                    AugmentPartsManager.getInstance().removeParts(component);
                }
                Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(VoidTecUtils.calcNeededCreditsForRepair(augment));
                augment.repairAugment(1);
            }
        }
    }

    public static void dismantleAugment(AugmentCargoWrapper augmentCargoWrapper, int amount) {
        for (int i = 0; i < amount; i++) {
            dismantleAugment(augmentCargoWrapper.getAugment());
        }

        CargoUtils.removeAugmentFromCargo(augmentCargoWrapper, amount);
    }

    public static void dismantleAugment(AugmentApplier augment) {
        List<CraftingComponent> disassembledComponents = getComponentsForDismantling(augment);

        for (CraftingComponent component : disassembledComponents) {
            AugmentPartsManager.getInstance().addParts(component);
        }

        if (!isNull(augment.getInstalledSlot())) {
            augment.getInstalledSlot().removeAugment();
        }
    }

    public static List<CraftingComponent> getComponentsForDismantling(AugmentApplier augment) {
        List<CraftingComponent> possibleComponents = new ArrayList<>();

        Random random = new Random(augment.hashCode());
        AugmentQuality currentQuality = augment.getAugmentQuality();
        AugmentQuality initialQuality = augment.getInitialQuality();
        int damagedMalus = initialQuality.ordinal() - currentQuality.ordinal();

        int basePartAmount = (int) Math.ceil(VT_Settings.basePartAmount * VT_Settings.basicDisassembleMod);
        AugmentComponent baseComponent = new AugmentComponent(AugmentPartsManager.BASIC_COMPONENT, currentQuality);
        AugmentComponent baseComponentIQ = new AugmentComponent(AugmentPartsManager.BASIC_COMPONENT, initialQuality);
        int addAmount = Math.max(Math.round(basePartAmount * (VT_Settings.partDisassemblePercentage / 100f))
                                         + random.nextInt(Math.max(basePartAmount / 2, 1)) - damagedMalus, 1);

        calculatePartsToAdd(possibleComponents, initialQuality, currentQuality, baseComponent, baseComponentIQ, addAmount);

        int primaryPartAmount = (int) Math.ceil(VT_Settings.basePartAmount * VT_Settings.primaryDisassembleMod);
        AugmentComponent primaryComponent = new AugmentComponent(augment.getPrimarySlot(), currentQuality);
        AugmentComponent primaryComponentIQ = new AugmentComponent(augment.getPrimarySlot(), initialQuality);
        addAmount = Math.max(Math.round(basePartAmount * (VT_Settings.partDisassemblePercentage / 100f))
                                     + random.nextInt(Math.max(primaryPartAmount / 2, 1)) - damagedMalus, 1);
        calculatePartsToAdd(possibleComponents, initialQuality, currentQuality, primaryComponent, primaryComponentIQ, addAmount);

        for (SlotCategory secondarySlot : augment.getSecondarySlots()) {
            int secondaryPartAmount = (int) Math.ceil(
                    VT_Settings.basePartAmount * VT_Settings.secondaryDisassembleMod / augment.getSecondarySlots().size());
            AugmentComponent secondaryComponent = new AugmentComponent(secondarySlot, currentQuality);
            AugmentComponent secondaryComponentIQ = new AugmentComponent(secondarySlot, initialQuality);
            addAmount = Math.max(Math.round(basePartAmount * (VT_Settings.partDisassemblePercentage / 100f))
                                         + random.nextInt(Math.max(secondaryPartAmount / 2, 1)) - damagedMalus, 1);
            calculatePartsToAdd(possibleComponents, initialQuality, currentQuality, secondaryComponent, secondaryComponentIQ, addAmount);
        }

        return possibleComponents;
    }

    private static void calculatePartsToAdd(List<CraftingComponent> craftingComponents,
                                            AugmentQuality initialQuality, AugmentQuality currentQuality,
                                            AugmentComponent componentCQ, AugmentComponent componentIQ, int addAmount) {
        if (initialQuality == currentQuality) {
            componentCQ.addAmount(addAmount);
            craftingComponents.add(componentCQ);
        } else {
            if (currentQuality.isGreaterOrEqualThen(AugmentQuality.COMMON)) {
                componentCQ.addAmount(Math.max(Math.round(addAmount / 3f * 2f), 1));
                craftingComponents.add(componentCQ);
            }

            int iqAmount = Math.round(addAmount / 3f);
            if (iqAmount > 0) {
                componentIQ.addAmount(iqAmount);
                craftingComponents.add(componentIQ);
            }
        }
    }
}