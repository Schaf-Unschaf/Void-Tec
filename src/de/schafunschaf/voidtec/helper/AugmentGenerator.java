package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.Global;
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
import de.schafunschaf.voidtec.ids.VT_MemoryKeys;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.ids.VT_Tags;
import de.schafunschaf.voidtec.imported.CustomFactionCategories;
import de.schafunschaf.voidtec.imported.SpecialShips;
import de.schafunschaf.voidtec.util.MathUtils;
import de.schafunschaf.voidtec.util.ShipUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class AugmentGenerator {

    public static void generateFleetAugments(CampaignFleetAPI fleet) {
        Random random = new Random(fleet.getId().hashCode());
        fleet.setInflated(true);

        for (FleetMemberAPI fleetMember : fleet.getFleetData().getMembersListCopy()) {
            if (fleetMember.isStation()) {
                continue;
            }

            if (SpecialShips.isSpecialShip(fleetMember)) {
                generateShipAugments(fleetMember, 100, SpecialShips.getQualityRange(fleetMember), true, random);
                continue;
            }

            if (MathUtils.rollSuccessful(VT_Settings.aiHullmodChance, random)) {
                generateShipAugments(fleetMember, VT_Settings.aiSlotFillChance, getCategoryForFleet(fleet), random);
            }
        }
    }

    private static GenerationCategory getCategoryForFleet(CampaignFleetAPI fleet) {
        if (fleet.isPlayerFleet()) {
            return null;
        }

        String factionID = fleet.getFaction().getId();

        if (CustomFactionCategories.isIgnoredFaction(factionID)) {
            return null;
        }

        if (CustomFactionCategories.isDomainFaction(factionID)) {
            return GenerationCategory.DOMAIN;
        }

        if (CustomFactionCategories.isRemnantFaction(factionID)) {
            return GenerationCategory.REMNANT;
        }

        if (CustomFactionCategories.isSpecialFaction(factionID)) {
            return GenerationCategory.SPECIAL;
        }

        if (CustomFactionCategories.isCivilianFaction(factionID)) {
            return GenerationCategory.CIVILIAN;
        }

        if (CustomFactionCategories.isOutlawFaction(factionID)) {
            return GenerationCategory.OUTLAW;
        }

        MemoryAPI memory = fleet.getMemoryWithoutUpdate();
        String fleetType = (String) memory.get("$fleetType");
        if (FleetTypes.TASK_FORCE.equals(fleetType)) {
            return GenerationCategory.SPECIAL;
        }

        return GenerationCategory.MILITARY;
    }

    public static void generateShipAugments(FleetMemberAPI ship, int probability, GenerationCategory category, Random random) {
        if (isNull(category) || ship.getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
            return;
        }

        if (BaseSkillEffectDescription.isCivilian(ship)) {
            category = GenerationCategory.CIVILIAN;
        }

        generateShipAugments(ship, probability, category.getQualityRange(), false, random);
    }

    public static void generateShipAugments(FleetMemberAPI ship, int probability, String[] qualityRange, boolean unlockAll, Random random) {
        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        HullModManager hullModManager = hullModDataStorage.getHullModManager(ship.getId());
        if (isNull(hullModManager)) {
            hullModManager = new HullModManager(ship);
        }

        if (unlockAll) {
            hullModManager.unlockAllSlots();
        }

        ShipVariantAPI variant = ship.getVariant();
        variant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
        Object prefFactionObject = ship.getFleetData().getFleet().getMemoryWithoutUpdate().get(VT_MemoryKeys.VT_PREF_FACTION_KEY);
        FactionAPI preferredFaction = isNull(prefFactionObject)
                                      ? ship.getFleetData().getFleet().getFaction()
                                      : Global.getSector().getFaction(((String) prefFactionObject));
        fillUnlockedSlots(hullModManager, ship, preferredFaction, qualityRange, probability, random);
    }

    public static void fillUnlockedSlots(HullModManager hullModManager, FleetMemberAPI ship, FactionAPI preferredFaction,
                                         String[] qualityRange, int probability, Random random) {
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Generating random augments for [%s]", ShipUtils.generateShipNameWithClass(ship)));
        }

        if (isNull(random)) {
            random = new Random();
        }

        for (AugmentSlot shipAugmentSlot : hullModManager.getUnlockedSlots()) {
            if (!shipAugmentSlot.isEmpty() || !MathUtils.rollSuccessful(probability, random)) {
                continue;
            }

            SlotCategory slotCategory = shipAugmentSlot.getSlotCategory();
            AugmentQuality augmentQuality = AugmentQuality.getRandomQualityInRange(qualityRange, random, false);
            List<String> shipTags = new ArrayList<>();

            String roleTag = BaseSkillEffectDescription.isCivilian(ship) ? VT_Tags.CIVILIAN_ONLY : VT_Tags.MILITARY_ONLY;
            shipTags.add(roleTag);

            AugmentApplier augment = AugmentDataManager.getRandomAugment(slotCategory, augmentQuality, preferredFaction, shipTags, random,
                                                                         false);
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
        OUTLAW(new String[]{AugmentQuality.DEGRADED.name(), AugmentQuality.COMMON.name()}),
        CIVILIAN(new String[]{AugmentQuality.DEGRADED.name(), AugmentQuality.MILITARY.name()}),
        MILITARY(new String[]{AugmentQuality.COMMON.name(), AugmentQuality.MILITARY.name()}),
        SPECIAL(new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}),
        REMNANT(new String[]{AugmentQuality.EXOTIC.name()}),
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
