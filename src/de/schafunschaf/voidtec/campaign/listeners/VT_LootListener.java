package de.schafunschaf.voidtec.campaign.listeners;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.listeners.ShowLootListener;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.IndEvo_ids;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Items;
import de.schafunschaf.voidtec.ids.VT_Settings;

import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VT_LootListener implements ShowLootListener {

    // Fleet loot gets handled via VT_CampaignListener
    @Override
    public void reportAboutToShowLootToPlayer(CargoAPI loot, InteractionDialogAPI dialog) {
        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        String entityType = interactionTarget.getCustomEntityType();
        Random salvageRandom = new Random(Misc.getSalvageSeed(interactionTarget));

        if (!isNull(entityType)) {
            switch (entityType) {
                case Entities.SUPPLY_CACHE:
                    addLootToCargo(loot, 5, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                   salvageRandom);
                    return;
                case Entities.SUPPLY_CACHE_SMALL:
                    addLootToCargo(loot, 3, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                   salvageRandom);
                    return;

                case Entities.EQUIPMENT_CACHE:
                    addLootToCargo(loot, 3, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                   salvageRandom);
                    addLootToCargo(loot, 2, SlotCategory.ENGINE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 2, SlotCategory.SYSTEM,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.SHIELD,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.EQUIPMENT_CACHE_SMALL:
                    addLootToCargo(loot, 3, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                   salvageRandom);
                    addLootToCargo(loot, 2, SlotCategory.SYSTEM,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;

                case Entities.WEAPONS_CACHE:
                    addLootToCargo(loot, 5, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 2, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.WEAPONS_CACHE_LOW:
                    addLootToCargo(loot, 2, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.WEAPONS_CACHE_HIGH:
                    addLootToCargo(loot, 2, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.WEAPONS_CACHE_REMNANT:
                    addLootToCargo(loot, 5, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.EXOTIC.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.EXOTIC.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 2, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.EXOTIC.name()}, 1, salvageRandom);
                    return;

                case Entities.WEAPONS_CACHE_SMALL:
                    addLootToCargo(loot, 3, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.WEAPONS_CACHE_SMALL_LOW:
                    addLootToCargo(loot, 1, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.WEAPONS_CACHE_SMALL_HIGH:
                    addLootToCargo(loot, 1, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
                case Entities.WEAPONS_CACHE_SMALL_REMNANT:
                    addLootToCargo(loot, 3, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.EXOTIC.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.EXOTIC.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 1, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.EXOTIC.name()}, 1, salvageRandom);
                    return;

                case Entities.ALPHA_SITE_WEAPONS_CACHE:
                    addLootToCargo(loot, 10, null, new String[]{AugmentQuality.EXPERIMENTAL.name(), AugmentQuality.DOMAIN.name()}, 1,
                                   salvageRandom);
                    return;

                case Entities.STATION_MINING:
                    addLootToCargo(loot, 5, SlotCategory.SYSTEM,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.STRUCTURE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                   salvageRandom);
                    return;

                case Entities.STATION_RESEARCH:
                    addLootToCargo(loot, 5, SlotCategory.SHIELD,
                                   new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.WEAPON,
                                   new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 3, SlotCategory.SPECIAL,
                                   new String[]{AugmentQuality.CUSTOMISED.name(), AugmentQuality.CUSTOMISED.name()},
                                   0, salvageRandom);
                    return;

                case Entities.ORBITAL_HABITAT:
                    addLootToCargo(loot, 5, SlotCategory.REACTOR,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.SYSTEM,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    addLootToCargo(loot, 5, SlotCategory.ENGINE,
                                   new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                    return;
            }

            if (VT_Settings.isIndEvoActive) {
                switch (entityType) {
                    case IndEvo_ids.ARSENAL_ENTITY:
                        addLootToCargo(loot, 5, SlotCategory.WEAPON,
                                       new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                        addLootToCargo(loot, 5, SlotCategory.STRUCTURE,
                                       new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                        addLootToCargo(loot, 5, null, new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                       salvageRandom);
                        return;

                    case IndEvo_ids.LAB_ENTITY:
                        addLootToCargo(loot, 5, SlotCategory.REACTOR,
                                       new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                        addLootToCargo(loot, 5, SlotCategory.ENGINE,
                                       new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1, salvageRandom);
                        addLootToCargo(loot, 5, null, new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}, 1,
                                       salvageRandom);
                        return;
                }
            }
        }

        if (!isNull(interactionTarget.getMarket())) {
            for (MarketConditionAPI condition : interactionTarget.getMarket().getConditions()) {
                switch (condition.getId()) {
                    case Conditions.RUINS_SCATTERED:
                        addLootToCargo(loot, 3, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 2,
                                       salvageRandom);
                        return;
                    case Conditions.RUINS_WIDESPREAD:
                        addLootToCargo(loot, 5, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 2,
                                       salvageRandom);
                        return;
                    case Conditions.RUINS_EXTENSIVE:
                        addLootToCargo(loot, 7, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 2,
                                       salvageRandom);
                        return;
                    case Conditions.RUINS_VAST:
                        addLootToCargo(loot, 10, null, new String[]{AugmentQuality.COMMON.name(), AugmentQuality.EXPERIMENTAL.name()}, 2,
                                       salvageRandom);
                        return;
                }
            }
        }
    }

    private void addLootToCargo(CargoAPI cargo, int maxAmount, SlotCategory slotCategory, String[] qualityRange, int maxDamage,
                                Random salvageRandom) {
        int numLootToAdd = salvageRandom.nextInt(maxAmount);

        for (int i = 0; i < numLootToAdd; i++) {
            int damageAmount = salvageRandom.nextInt(maxDamage + 1);

            AugmentQuality augmentQuality = AugmentQuality.getRandomQualityInRange(qualityRange, salvageRandom, false);
            AugmentApplier augment = AugmentDataManager.getRandomAugment(slotCategory, augmentQuality, null, salvageRandom);
            if (isNull(augment)) {
                continue;
            }

            augment.damageAugment(damageAmount);
            AugmentItemData augmentItemData = new AugmentItemData(VT_Items.AUGMENT_ITEM, null, augment);

            cargo.addSpecial(augmentItemData, 1f);
        }
    }
}
