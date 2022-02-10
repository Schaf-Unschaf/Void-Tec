package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.ShipUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.awt.Color;
import java.util.*;

import static com.fs.starfarer.api.combat.ShipAPI.HullSize;
import static de.schafunschaf.voidtec.ids.VT_Colors.VT_GREY_COLOR;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class HullModManager {

    private final List<AugmentSlot> shipAugmentSlots = new ArrayList<>();
    @Getter
    private final ShipStatEffectManager shipStatEffectManager = new ShipStatEffectManager();
    @Getter
    private final String fleetMemberID;

    public HullModManager(FleetMemberAPI fleetMember) {
        this.fleetMemberID = fleetMember.getId();
        HullModDataStorage.getInstance().storeShipID(fleetMemberID, this);
        generateSlotsForShip(fleetMember);
    }

    public void applySlotEffects(MutableShipStatsAPI stats, String id) {
        shipStatEffectManager.clear();
        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            augmentSlot.applyToShip(stats, id);
        }
    }

    public void applyFighterEffects(ShipAPI fighter, String id) {
        for (AugmentSlot flightDeckSlot : getFlightDeckSlots()) {
            flightDeckSlot.applyToFighter(fighter.getMutableStats(), id);
        }
    }

    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, boolean isItemTooltip) {
        String shipName = stats.getFleetMember().getShipName();
        int maxSlots = shipAugmentSlots.size();
        Color highlightColor = Misc.getHighlightColor();
        tooltip.addPara(
                "The %s is equipped with a special device, allowing her to install up to %s so called '%s'.\n" + "Each Augment can only be installed once in the device and it's impossible to reuse them afterwards.",
                10f, new Color[]{Misc.getBasePlayerColor(), highlightColor, highlightColor}, shipName, String.valueOf(maxSlots),
                "Augments");

        tooltip.addSpacer(10f);

        int numLockedSlots = 0;

        boolean isFirstUnique = true;
        for (AugmentSlot uniqueSlot : getUniqueSlots()) {
            if (uniqueSlot.isEmpty()) {
                addEmptySlotDesc(tooltip, width, uniqueSlot);
            } else {
                uniqueSlot.generateTooltip(stats, id, tooltip, width, isItemTooltip);
            }

            if (isFirstUnique) {
                tooltip.addSpacer(10f);
                isFirstUnique = false;
            }
        }

        for (AugmentSlot augmentSlot : getSlotsForDisplay()) {
            if (!augmentSlot.isUnlocked()) {
                numLockedSlots++;
                continue;
            }

            tooltip.addSpacer(10f);

            if (augmentSlot.isEmpty()) {
                addEmptySlotDesc(tooltip, width, augmentSlot);
                continue;
            }

            augmentSlot.generateTooltip(stats, id, tooltip, width, isItemTooltip);
        }

        float maxPermanentHullmods = Global.getSettings().getFloat("maxPermanentHullmods");
        int builtInBonusSlots = (int) stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus();
        int maxBuiltInMods = (int) (maxPermanentHullmods + builtInBonusSlots);
        int currentBuiltInMods = stats.getVariant().getSMods().size();

        tooltip.addSectionHeading("VESAI System Status", Alignment.MID, 10f);
        tooltip.addPara("--- All systems are fully operational ---", Misc.getPositiveHighlightColor(), 3f).setAlignment(Alignment.MID);

        if (numLockedSlots > 0) {
            tooltip.addSpacer(10f);
            TooltipMakerAPI imageWithText = tooltip.beginImageWithText(VT_Icons.LOCKED_SLOT_ICON, 32f);
            imageWithText.addPara(String.format("%s Augment slots are still locked", numLockedSlots), 0f, VT_GREY_COLOR,
                                  Misc.getHighlightColor(), String.valueOf(numLockedSlots));
            imageWithText.addPara("Type: Unknown", 3f, VT_GREY_COLOR, "Unknown");
            tooltip.addImageWithText(0f);
        }

        if (maxBuiltInMods > 0) {
            tooltip.addPara("Available Build-In Slots: %s/%s", 10f, Misc.getTextColor(), Misc.getHighlightColor(),
                            String.valueOf(currentBuiltInMods), String.valueOf(maxBuiltInMods));
        } else {
            tooltip.addPara("Available Build-In Slots: %s", 10f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), "NONE");
        }
        tooltip.addPara("HINT: Some %s Augments can grant additional slots for building in hullmods", 3f, Misc.getGrayColor(),
                        SlotCategory.SPECIAL.getColor(), SlotCategory.SPECIAL.name());
    }

    private void addEmptySlotDesc(TooltipMakerAPI tooltip, float width, AugmentSlot augmentSlot) {
        tooltip.addButton("", null, VT_GREY_COLOR, VT_GREY_COLOR, width, 0f, 3f);
        tooltip.addPara("Empty Augment Slot", VT_GREY_COLOR, 3f);
        TooltipMakerAPI imageWithText = tooltip.beginImageWithText(VT_Icons.EMPTY_SLOT_ICON, 40f);
        imageWithText.addPara("Type: %s", 3f, augmentSlot.getSlotCategory().color, augmentSlot.getSlotCategory().toString());
        tooltip.addImageWithText(3f);
        tooltip.addButton("", null, VT_GREY_COLOR, VT_GREY_COLOR, width, 0f, 3f);
    }

    public void runCombatScript(ShipAPI ship, float amount) {
        for (AugmentSlot augmentSlot : getFilledSlots()) {
            augmentSlot.getSlottedAugment().runCustomScript(ship, amount);
        }
    }

    public boolean installAugment(AugmentSlot augmentSlot, AugmentApplier augment) {
        return augmentSlot.isEmpty() && isAugmentCompatible(augmentSlot, augment) && augmentSlot.insertAugment(augment);
    }

    public boolean isAugmentCompatible(AugmentSlot augmentSlot, AugmentApplier augment) {
        if (isNull(augment) || hasSameAugmentSlotted(augment) || augment.getAugmentQuality() == AugmentQuality.DESTROYED) {
            return false;
        }

        SlotCategory slotCategory = augmentSlot.getSlotCategory();

        if (slotCategory.equals(augment.getPrimarySlot())) {
            return true;
        }

        if (isNull(augment.getSecondarySlots())) {
            return false;
        }

        return augment.getSecondarySlots().contains(slotCategory);
    }

    private boolean hasSameAugmentSlotted(AugmentApplier augment) {
        for (AugmentSlot augmentSlot : getFilledSlots()) {
            if (augmentSlot.getSlottedAugment().getAugmentID().equals(augment.getAugmentID())) {
                return true;
            }
        }

        return false;
    }

    public List<AugmentSlot> getUniqueSlots() {
        List<AugmentSlot> augmentSlots = new ArrayList<>();
        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (augmentSlot.getSlotCategory() == SlotCategory.SPECIAL || augmentSlot.getSlotCategory() == SlotCategory.COSMETIC) {
                augmentSlots.add(augmentSlot);
            }
        }

        return augmentSlots;
    }

    public List<AugmentSlot> getFlightDeckSlots() {
        List<AugmentSlot> flightDeckSlots = new ArrayList<>();
        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (augmentSlot.getSlotCategory() == SlotCategory.FLIGHT_DECK) {
                flightDeckSlots.add(augmentSlot);
            }
        }

        return flightDeckSlots;
    }

    public List<AugmentSlot> getUnlockedSlots() {
        List<AugmentSlot> unlockedSlots = new ArrayList<>();

        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (augmentSlot.isUnlocked()) {
                unlockedSlots.add(augmentSlot);
            }
        }

        return unlockedSlots;
    }

    public List<AugmentSlot> getEmptySlots() {
        List<AugmentSlot> emptySlots = new ArrayList<>();

        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (!augmentSlot.isEmpty()) {
                continue;
            }

            emptySlots.add(augmentSlot);
        }

        return emptySlots;
    }

    public List<AugmentSlot> getFilledSlots() {
        List<AugmentSlot> filledSlots = new ArrayList<>();

        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (!augmentSlot.isEmpty()) {
                filledSlots.add(augmentSlot);
            }
        }

        return filledSlots;
    }

    public List<AugmentSlot> getSlotsForDisplay() {
        List<AugmentSlot> slotsForDisplay = new ArrayList<>();
        List<AugmentSlot> lockedSlots = new ArrayList<>();

        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (augmentSlot.getSlotCategory() == SlotCategory.SPECIAL || augmentSlot.getSlotCategory() == SlotCategory.COSMETIC) {
                continue;
            }
            if (augmentSlot.isUnlocked()) {
                slotsForDisplay.add(augmentSlot);
            } else {
                lockedSlots.add(augmentSlot);
            }
        }

        Collections.sort(slotsForDisplay, new Comparator<AugmentSlot>() {
            @Override
            public int compare(AugmentSlot slot1, AugmentSlot slot2) {
                return Integer.compare(slot1.getSlotCategory().ordinal(), slot2.getSlotCategory().ordinal());
            }
        });

        slotsForDisplay.addAll(lockedSlots);

        return slotsForDisplay;
    }

    public AugmentSlot damageRandomAugment(int numLevelsDamaged, Random random) {
        if (isNull(random)) {
            random = new Random();
        }

        List<AugmentSlot> slotsWithDamageableAugments = new ArrayList<>();

        for (AugmentSlot filledSlot : getFilledSlots()) {
            AugmentQuality augmentQuality = filledSlot.getSlottedAugment().getAugmentQuality();
            if (augmentQuality != AugmentQuality.UNIQUE && augmentQuality.getLowerQuality() != augmentQuality) {
                slotsWithDamageableAugments.add(filledSlot);
            }
        }

        if (slotsWithDamageableAugments.isEmpty()) {
            return null;
        }

        AugmentSlot augmentSlot = slotsWithDamageableAugments.get(random.nextInt(slotsWithDamageableAugments.size()));
        augmentSlot.getSlottedAugment().damageAugment(numLevelsDamaged);

        return augmentSlot;
    }

    public AugmentSlot repairRandomAugment(int numLevelsRepaired, Random random) {
        if (isNull(random)) {
            random = new Random();
        }

        List<AugmentSlot> slotsWithRepairableAugments = new ArrayList<>();

        for (AugmentSlot filledSlot : getFilledSlots()) {
            AugmentApplier slottedAugment = filledSlot.getSlottedAugment();
            if (slottedAugment.getAugmentQuality() != slottedAugment.getInitialQuality()) {
                slotsWithRepairableAugments.add(filledSlot);
            }
        }

        if (slotsWithRepairableAugments.isEmpty()) {
            return null;
        }

        AugmentSlot augmentSlot = slotsWithRepairableAugments.get(random.nextInt(slotsWithRepairableAugments.size()));
        augmentSlot.getSlottedAugment().repairAugment(numLevelsRepaired);

        return augmentSlot;
    }

    public void generateSlotsForShip(FleetMemberAPI fleetMember) {
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Generating slots for %s", ShipUtils.generateShipNameWithClass(fleetMember)));
        }

        Random random = new Random(fleetMember.getId().hashCode());
        HullSize hullSize = fleetMember.getVariant().getHullSize();
        boolean isPhase = fleetMember.isPhaseShip();
        int numFlightDecks = fleetMember.getNumFlightDecks();
        boolean isCarrier = numFlightDecks > 0;
        int maxSlots = 6;
        int numSlots = 0;

        if (VT_Settings.randomSlotAmount) {
            numSlots = random.nextInt(maxSlots) + 1;
        } else {
            switch (hullSize) {
                case FRIGATE:
                    numSlots = 1;
                    break;
                case DESTROYER:
                    numSlots = 2;
                    break;
                case CRUISER:
                    numSlots = 3;
                    break;
                case CAPITAL_SHIP:
                    numSlots = 4;
                    break;
            }
        }

        shipAugmentSlots.add(new AugmentSlot(this, SlotCategory.COSMETIC, true));
        shipAugmentSlots.add(new AugmentSlot(this, SlotCategory.SPECIAL, true));
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Slot: %s (unlocked: %s)", shipAugmentSlots.get(0).getSlotCategory().name(), true));
        }

        Map<SlotCategory, Integer> categoryPoolWithWeighting = new HashMap<>();
        for (SlotCategory allowedCategory : SlotCategory.getAllowedCategories()) {
            if (isPhase && allowedCategory == SlotCategory.SHIELD) {
                continue;
            }
            if (isCarrier && allowedCategory == SlotCategory.FLIGHT_DECK) {
                categoryPoolWithWeighting.put(allowedCategory, allowedCategory.getWeighting() + numFlightDecks / 2);
                continue;
            }

            categoryPoolWithWeighting.put(allowedCategory, allowedCategory.getWeighting());
        }

        for (int i = 0; i < maxSlots; i++) {
            boolean isUnlocked = i < numSlots;
            shipAugmentSlots.add(new AugmentSlot(this, categoryPoolWithWeighting, random, isUnlocked));
            if (VT_Settings.sheepDebug) {
                log.info(String.format("Slot: %s (unlocked: %s)", shipAugmentSlots.get(i + 1).getSlotCategory().name(), isUnlocked));
            }
        }
    }
}
