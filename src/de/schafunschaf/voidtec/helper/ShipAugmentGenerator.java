package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.skills.BaseSkillEffectDescription;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.imported.CustomFleetCategories;
import de.schafunschaf.voidtec.util.ShipUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.math.BigDecimal;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class ShipAugmentGenerator {

    public static GenerationCategory getCategoryForFleet(CampaignFleetAPI fleet) {
        if (fleet.isPlayerFleet()) {
            return null;
        }

        String factionID = fleet.getFaction().getId();

        if (CustomFleetCategories.isIgnoredFaction(factionID)) {
            return null;
        }

        if (CustomFleetCategories.isDomainFaction(factionID)) {
            return GenerationCategory.DOMAIN;
        }

        if (CustomFleetCategories.isRemnantFaction(factionID)) {
            return GenerationCategory.REMNANT;
        }

        if (CustomFleetCategories.isSpecialFaction(factionID)) {
            return GenerationCategory.SPECIAL;
        }

        if (CustomFleetCategories.isCivilianFaction(factionID)) {
            return GenerationCategory.CIVILIAN;
        }

        if (CustomFleetCategories.isOutlawFaction(factionID)) {
            return GenerationCategory.OUTLAW;
        }

        MemoryAPI memory = fleet.getMemoryWithoutUpdate();
        String fleetType = (String) memory.get("$fleetType");
        if (FleetTypes.TASK_FORCE.equals(fleetType)) {
            return GenerationCategory.SPECIAL;
        }

        return GenerationCategory.MILITARY;
    }

    public static void generateFleetAugments(CampaignFleetAPI fleet, float probability, GenerationCategory category) {
        if (isNull(category)) {
            return;
        }

        Random random = new Random(fleet.getId().hashCode());

        for (FleetMemberAPI fleetMember : fleet.getFleetData().getMembersListCopy()) {
            if (fleetMember.isStation()) {
                continue;
            }

            generateShipAugments(fleetMember, probability, category, random);
        }
    }

    public static void generateShipAugments(FleetMemberAPI ship, float probability, GenerationCategory category, Random random) {
        if (isNull(category) || ship.getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
            return;
        }

        if (BaseSkillEffectDescription.isCivilian(ship)) {
            category = GenerationCategory.CIVILIAN;
        }

        int bound = (int) Math.pow(10, BigDecimal.valueOf(probability).scale());
        int scaledProb = (int) (probability * bound);
        if (random.nextInt(bound) <= scaledProb) {
            HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
            HullModManager hullModManager = hullModDataStorage.getHullModManager(ship.getId());
            if (isNull(hullModManager)) {
                hullModManager = new HullModManager(ship);
            }

            ShipVariantAPI variant = ship.getVariant();
            variant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
            fillUnlockedSlots(hullModManager, ship, ship.getFleetData().getFleet().getFaction(),
                              category.getQualityRange(), random);
        }
    }

    public static void fillUnlockedSlots(HullModManager hullModManager, FleetMemberAPI ship, FactionAPI preferredFaction,
                                         String[] qualityRange, Random random) {
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Generating random augments for [%s]", ShipUtils.generateShipNameWithClass(ship)));
        }

        if (isNull(random)) {
            random = new Random();
        }

        for (AugmentSlot shipAugmentSlot : hullModManager.getEmptySlots()) {
            SlotCategory slotCategory = shipAugmentSlot.getSlotCategory();
            AugmentQuality augmentQuality = AugmentQuality.getRandomQualityInRange(qualityRange, random, false);

            AugmentApplier augment = AugmentDataManager.getRandomAugment(slotCategory, augmentQuality, preferredFaction, random);
            boolean success = hullModManager.installAugment(shipAugmentSlot, augment);

            if (VT_Settings.sheepDebug) {
                log.info(String.format("Slot: %s - Augment: %s - Quality: %s - Success: %s", slotCategory.name(),
                                       isNull(augment) ? "NULL" : augment.getAugmentID(),
                                       isNull(augment) ? "NULL" : augment.getAugmentQuality().name(), success));
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum GenerationCategory {
        OUTLAW(new String[]{AugmentQuality.DAMAGED.name(), AugmentQuality.COMMON.name()}),
        CIVILIAN(new String[]{AugmentQuality.DAMAGED.name(), AugmentQuality.MILITARY.name()}),
        MILITARY(new String[]{AugmentQuality.COMMON.name(), AugmentQuality.MILITARY.name()}),
        SPECIAL(new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}),
        REMNANT(new String[]{AugmentQuality.REMNANT.name()}),
        DOMAIN(new String[]{AugmentQuality.DOMAIN.name()});

        public static final GenerationCategory[] values = values();
        String[] qualityRange;

        public static GenerationCategory getEnum(String valueString) {
            for (GenerationCategory value : values) {
                if (value.name().equalsIgnoreCase(valueString)) {
                    return value;
                }
            }

            return null;
        }
    }

}
