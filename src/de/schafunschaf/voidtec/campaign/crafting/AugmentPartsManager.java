package de.schafunschaf.voidtec.campaign.crafting;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.campaign.crafting.parts.AugmentComponent;
import de.schafunschaf.voidtec.campaign.crafting.parts.CraftingComponent;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;

import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentPartsManager {

    public static final String BASIC_COMPONENT = "Basic";
    private static final String MEM_KEY = "$vt_augmentPartsManager";
    private final List<CraftingComponent> componentInventory = new ArrayList<>();

    public AugmentPartsManager() {
        for (AugmentQuality augmentQuality : AugmentQuality.values) {
            if (augmentQuality == AugmentQuality.DESTROYED || augmentQuality == AugmentQuality.DEGRADED || augmentQuality == AugmentQuality.CUSTOMISED) {
                continue;
            }

            AugmentComponent component = new AugmentComponent(BASIC_COMPONENT, augmentQuality);
            componentInventory.add(component);
        }

        for (SlotCategory category : SlotCategory.values) {
            for (AugmentQuality augmentQuality : AugmentQuality.values) {
                if (augmentQuality == AugmentQuality.DESTROYED || augmentQuality == AugmentQuality.DEGRADED || augmentQuality == AugmentQuality.CUSTOMISED) {
                    continue;
                }

                AugmentComponent component = new AugmentComponent(category, augmentQuality);
                componentInventory.add(component);
            }
        }
    }

    public static AugmentPartsManager getInstance() {
        Object instance = Global.getSector().getMemoryWithoutUpdate().get(MEM_KEY);
        if (isNull(instance)) {
            AugmentPartsManager augmentPartsManager = new AugmentPartsManager();
            Global.getSector().getMemoryWithoutUpdate().set(MEM_KEY, augmentPartsManager);

            return augmentPartsManager;
        }

        return (AugmentPartsManager) instance;
    }

    public void addParts(CraftingComponent component) {
        for (CraftingComponent compFromInventory : componentInventory) {
            if (compFromInventory.equals(component)) {
                compFromInventory.addAmount(component.getAmount());
                return;
            }
        }
    }

    public void removeParts(CraftingComponent component) {
        for (CraftingComponent compFromInventory : componentInventory) {
            if (compFromInventory.equals(component)) {
                compFromInventory.addAmount(-component.getAmount());
                return;
            }
        }
    }

    public CraftingComponent getPart(CraftingComponent component) {
        for (CraftingComponent compFromInventory : componentInventory) {
            if (component.equals(compFromInventory)) {
                return compFromInventory;
            }
        }

        return null;
    }

    public int getSumOfAllParts() {
        int sum = 0;
        for (CraftingComponent component : componentInventory) {
            sum += component.getAmount();
        }

        return sum;
    }

    public List<CraftingComponent> getPartsOfCategory(SlotCategory category) {
        List<CraftingComponent> componentsOfCategory = new ArrayList<>();
        for (CraftingComponent craftingComponent : componentInventory) {
            if (craftingComponent.getPartCategory() == category) {
                componentsOfCategory.add(craftingComponent);
            }
        }

        return componentsOfCategory;
    }
}
