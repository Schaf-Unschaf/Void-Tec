package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.Settings;
import de.schafunschaf.voidtec.VT_Icons;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.BaseAugment;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.fs.starfarer.api.combat.ShipAPI.HullSize;
import static de.schafunschaf.voidtec.VT_Colors.VT_GREY_COLOR;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
public class HullModManager {
    private final List<AugmentSlot> shipAugmentSlots = new ArrayList<>();

    public HullModManager(FleetMemberAPI fleetMember) {
        generateSlotsForShip(fleetMember);
    }

    public void applySlotEffects(MutableShipStatsAPI stats, String id, Random random) {
        for (AugmentSlot augmentSlot : shipAugmentSlots)
            augmentSlot.apply(stats, id, random, null);
    }

    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width) {
        String shipName = stats.getFleetMember().getShipName();
        int maxSlots = shipAugmentSlots.size();
        Color highlightColor = Misc.getHighlightColor();
        tooltip.addPara("The %s is equipped with a special device, allowing her to install up to %s so called '%s'.\n" +
                "Each Augment can only be installed once in the device and it's impossible to remove them afterwards.", 10f, new Color[]{Misc.getBasePlayerColor(), highlightColor, highlightColor}, shipName, String.valueOf(maxSlots), "Augments");

        int numLockedSlots = 0;
        for (AugmentSlot augmentSlot : getSlotsForDisplay()) {
            if (!augmentSlot.isUnlocked()) {
                numLockedSlots++;
                continue;
            }

            tooltip.addSpacer(10f);

            if (augmentSlot.isEmpty()) {
                tooltip.addButton("", null, VT_GREY_COLOR, VT_GREY_COLOR, width, 0f, 3f);
                tooltip.addPara("Empty Augment Slot", VT_GREY_COLOR, 3f);
                TooltipMakerAPI imageWithText = tooltip.beginImageWithText(VT_Icons.EMPTY_SLOT_ICON, 40f);
                imageWithText.addPara("Type: %s", 3f, augmentSlot.getSlotCategory().color, augmentSlot.getSlotCategory().toString());
                tooltip.addImageWithText(3f);
                tooltip.addButton("", null, VT_GREY_COLOR, VT_GREY_COLOR, width, 0f, 3f);
                continue;
            }

            augmentSlot.generateTooltip(stats, id, tooltip, width);
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
            imageWithText.addPara(String.format("%s Augment slots are still locked", numLockedSlots), 0f, VT_GREY_COLOR, Misc.getHighlightColor(), String.valueOf(numLockedSlots));
            imageWithText.addPara("Type: Unknown", 3f, VT_GREY_COLOR, "Unknown");
            tooltip.addImageWithText(0f);
        }

        if (maxBuiltInMods > 0)
            tooltip.addPara("Available Build-In Slots: %s/%s", 10f, Misc.getTextColor(), Misc.getHighlightColor(), String.valueOf(currentBuiltInMods), String.valueOf(maxBuiltInMods));
        else
            tooltip.addPara("Available Build-In Slots: %s", 10f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), "NONE");
        tooltip.addPara("HINT: Some %s Augments can grant additional slots for building in hullmods", 3f, Misc.getGrayColor(), SlotCategory.SPECIAL.getColor(), SlotCategory.SPECIAL.name());
    }

    public void runCombatScript(ShipAPI ship, float amount) {
        AugmentSlot augmentSlot = getSlotOfType(SlotCategory.SPECIAL);
        if (isNull(augmentSlot))
            return;

        augmentSlot.getSlottedAugment().runCombatScript(ship, amount);
    }

    public boolean installAugment(AugmentSlot augmentSlot, BaseAugment augment) {
        return augmentSlot.isEmpty() && isAugmentCompatible(augmentSlot, augment) && augmentSlot.insertAugment(augment);
    }

    public boolean isAugmentCompatible(AugmentSlot augmentSlot, BaseAugment augment) {
        if (hasSameAugmentSlotted(augment))
            return false;

        SlotCategory slotCategory = augmentSlot.getSlotCategory();

        if (slotCategory.equals(augment.getPrimarySlot()))
            return true;

        return augment.getSecondarySlots().contains(slotCategory);
    }

    public int getUsedSlots() {
        return getFilledSlots().size();
    }

    public int getUnlockedSlotsNum() {
        int unlockedSlots = 0;
        for (AugmentSlot augmentSlot : shipAugmentSlots)
            if (augmentSlot.isUnlocked())
                unlockedSlots++;

        return unlockedSlots;
    }

    private boolean hasSameAugmentSlotted(BaseAugment augment) {
        for (AugmentSlot augmentSlot : getFilledSlots())
            if (augmentSlot.getSlottedAugment().getClass() == augment.getClass())
                return true;

        return false;
    }

    public boolean hasSlotOfType(SlotCategory slotCategory) {
        for (AugmentSlot augmentSlot : shipAugmentSlots)
            if (augmentSlot.getSlotCategory() == slotCategory)
                return true;

        return false;
    }

    public AugmentSlot getSlotOfType(SlotCategory slotCategory) {
        for (AugmentSlot augmentSlot : shipAugmentSlots)
            if (augmentSlot.getSlotCategory() == slotCategory)
                return augmentSlot;

        return null;
    }

    public List<AugmentSlot> getSlotsForDisplay() {
        List<AugmentSlot> slotsForDisplay = new ArrayList<>();
        List<AugmentSlot> lockedSlots = new ArrayList<>();

        for (AugmentSlot augmentSlot : shipAugmentSlots)
            if (augmentSlot.isUnlocked())
                slotsForDisplay.add(augmentSlot);
            else
                lockedSlots.add(augmentSlot);

        Collections.sort(slotsForDisplay, new Comparator<AugmentSlot>() {
            @Override
            public int compare(AugmentSlot slot1, AugmentSlot slot2) {
                return Integer.compare(slot1.getSlotCategory().ordinal(), slot2.getSlotCategory().ordinal());
            }
        });

        slotsForDisplay.addAll(lockedSlots);

        return slotsForDisplay;
    }

    public AugmentSlot damageRandomAugment(Random random) {
        if (isNull(random))
            random = new Random();

        List<AugmentSlot> slotsWithDamageableAugments = new ArrayList<>();

        for (AugmentSlot filledSlot : getFilledSlots()) {
            AugmentQuality augmentQuality = filledSlot.getSlottedAugment().getAugmentQuality();
            if (augmentQuality.getLowerQuality() != augmentQuality)
                slotsWithDamageableAugments.add(filledSlot);
        }

        if (slotsWithDamageableAugments.isEmpty())
            return null;

        AugmentSlot augmentSlot = slotsWithDamageableAugments.get(random.nextInt(slotsWithDamageableAugments.size()));
        augmentSlot.getSlottedAugment().damageAugment();

        return augmentSlot;
    }

    public AugmentSlot repairRandomAugment(Random random) {
        if (isNull(random))
            random = new Random();

        List<AugmentSlot> slotsWithRepairableAugments = new ArrayList<>();

        for (AugmentSlot filledSlot : getFilledSlots()) {
            AugmentApplier slottedAugment = filledSlot.getSlottedAugment();
            if (slottedAugment.getAugmentQuality() != slottedAugment.getInitialQuality())
                slotsWithRepairableAugments.add(filledSlot);
        }

        if (slotsWithRepairableAugments.isEmpty())
            return null;

        AugmentSlot augmentSlot = slotsWithRepairableAugments.get(random.nextInt(slotsWithRepairableAugments.size()));
        augmentSlot.getSlottedAugment().damageAugment();

        return augmentSlot;
    }

    private void generateSlotsForShip(FleetMemberAPI fleetMember) {
        Random random = new Random(fleetMember.getId().hashCode());
        HullSize hullSize = fleetMember.getVariant().getHullSize();
        int maxSlots = 6;
        int numSlots = 0;
        if (Settings.randomSlotAmount)
            numSlots = random.nextInt(maxSlots) + 1;
        else
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

        for (int i = 0; i < maxSlots; i++) {
            boolean isUnlocked = i < numSlots;

            if (hasSlotOfType(SlotCategory.SPECIAL))
                shipAugmentSlots.add(new AugmentSlot(this, Collections.singletonList(SlotCategory.SPECIAL), random, isUnlocked));
            else
                shipAugmentSlots.add(new AugmentSlot(this, random, isUnlocked));
        }
    }

    private List<AugmentSlot> getFilledSlots() {
        List<AugmentSlot> slotList = new ArrayList<>();
        for (AugmentSlot augmentSlot : shipAugmentSlots)
            if (!augmentSlot.isEmpty())
                slotList.add(augmentSlot);

        return slotList;
    }
}
