package de.schafunschaf.voidtec.campaign.crafting;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.campaign.crafting.parts.AugmentComponent;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.VoidTecUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentPartsUtility {

    private static final int BASE_PART_AMOUNT = 5;
    private static final int BASIC_REPAIR_MOD = 3;
    private static final int PRIMARY_REPAIR_MOD = 2;
    private static final int SECONDARY_REPAIR_MOD = 1;
    private static final int BASIC_DISASSEMBLE_MOD = 2;
    private static final int PRIMARY_DISASSEMBLE_MOD = 2;
    private static final int SECONDARY_DISASSEMBLE_MOD = 1;
    private static final int BASIC_CRAFT_MOD = 1;
    private static final int PRIMARY_CRAFT_MOD = 3;
    private static final int SECONDARY_CRAFT_MOD = 2;

    public static List<CraftingComponent> getComponentsForRepair(AugmentApplier augment) {
        List<CraftingComponent> neededComponents = new ArrayList<>();
        if (!augment.isRepairable()) {
            return neededComponents;
        }

        Random random = new Random(augment.hashCode());
        AugmentQuality higherQuality = augment.getAugmentQuality().getHigherQuality();
        float qualityModifier = (float) Math.pow(higherQuality.getModifier(), 2);

        int baseCost = (int) Math.ceil(BASE_PART_AMOUNT * BASIC_REPAIR_MOD * qualityModifier);
        AugmentComponent baseComponent = new AugmentComponent(AugmentPartsManager.BASIC_COMPONENT, higherQuality);
        baseComponent.addAmount(baseCost + random.nextInt(Math.max(baseCost / 2, 1)));
        neededComponents.add(baseComponent);

        int primaryCost = (int) Math.ceil(BASE_PART_AMOUNT * PRIMARY_REPAIR_MOD * qualityModifier);
        AugmentComponent primaryComponent = new AugmentComponent(augment.getPrimarySlot(), higherQuality);
        primaryComponent.addAmount(primaryCost + random.nextInt(Math.max(primaryCost / 2, 1)));
        neededComponents.add(primaryComponent);

        for (SlotCategory secondarySlot : augment.getSecondarySlots()) {
            int secondaryCost = (int) Math.ceil(
                    BASE_PART_AMOUNT * SECONDARY_REPAIR_MOD * qualityModifier / augment.getSecondarySlots().size());
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

    public static void disassembleAugment(AugmentApplier augment) {
        List<CraftingComponent> disassembledComponents = new ArrayList<>();

        Random random = new Random();
        AugmentQuality quality = augment.getAugmentQuality();
        float qualityModifier = quality.getModifier();

        int baseCost = (int) Math.ceil(BASE_PART_AMOUNT * BASIC_DISASSEMBLE_MOD * qualityModifier);
        AugmentComponent baseComponent = new AugmentComponent(AugmentPartsManager.BASIC_COMPONENT, quality);
        int addAmount = Math.round(baseCost + random.nextInt(Math.max(baseCost / 2, 1)) * (VT_Settings.partDisassemblePercentage / 100f));
        baseComponent.addAmount(addAmount);
        disassembledComponents.add(baseComponent);

        int primaryCost = (int) Math.ceil(BASE_PART_AMOUNT * PRIMARY_DISASSEMBLE_MOD * qualityModifier);
        AugmentComponent primaryComponent = new AugmentComponent(augment.getPrimarySlot(), quality);
        addAmount = Math.round(baseCost + random.nextInt(Math.max(primaryCost / 2, 1)) * (VT_Settings.partDisassemblePercentage / 100f));
        primaryComponent.addAmount(addAmount);
        disassembledComponents.add(primaryComponent);

        for (SlotCategory secondarySlot : augment.getSecondarySlots()) {
            int secondaryCost = (int) Math.ceil(
                    BASE_PART_AMOUNT * SECONDARY_DISASSEMBLE_MOD * qualityModifier / augment.getSecondarySlots().size());
            AugmentComponent secondaryComponent = new AugmentComponent(secondarySlot, quality);
            addAmount = Math.round(
                    baseCost + random.nextInt(Math.max(secondaryCost / 2, 1)) * (VT_Settings.partDisassemblePercentage / 100f));
            secondaryComponent.addAmount(addAmount);
            disassembledComponents.add(secondaryComponent);
        }

        for (CraftingComponent component : disassembledComponents) {
            AugmentPartsManager.getInstance().addParts(component);
        }
    }
}
