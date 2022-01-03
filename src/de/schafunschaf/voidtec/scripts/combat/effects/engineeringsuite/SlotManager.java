package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.Settings;
import de.schafunschaf.voidtec.VT_Icons;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.BaseAugment;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.*;

import static de.schafunschaf.voidtec.VT_Colors.VT_GREY_COLOR;

@Getter
public class SlotManager {
    private final List<AugmentSlot> shipAugmentSlots = new ArrayList<>();

    public SlotManager(FleetMemberAPI fleetMember) {
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

        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            tooltip.addSpacer(10f);

            if (augmentSlot.isEmpty()) {
                tooltip.addButton("", null, VT_GREY_COLOR, VT_GREY_COLOR, width, 0f, 3f);
                tooltip.addPara("Empty Augment Slot", VT_GREY_COLOR, 3f);
                TooltipMakerAPI imageWithText = tooltip.beginImageWithText(VT_Icons.EMPTY_SLOT_ICON, 40f);
                imageWithText.addPara("Type: %s", 3f, augmentSlot.getSlotCategory().color, augmentSlot.getSlotCategory().toString());
                tooltip.addImageWithText(3f);
                tooltip.addButton("", null, VT_GREY_COLOR, VT_GREY_COLOR, width, 0f, 3f);
            }

            augmentSlot.generateTooltip(stats, id, tooltip, width);
        }
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

    public int getMaxSlots() {
        return shipAugmentSlots.size();
    }

    public int getUsedSlots() {
        return getFilledSlots().size();
    }

    private boolean hasSameAugmentSlotted(BaseAugment augment) {
        for (AugmentSlot augmentSlot : getFilledSlots())
            if (augmentSlot.getSlottedAugment().getClass() == augment.getClass())
                return true;

        return false;
    }

    private void generateSlotsForShip(FleetMemberAPI fleetMember) {
        Random random = new Random(fleetMember.getId().hashCode());
        int numSlots = random.nextInt(Settings.slotMaxAmount) + 1;

        for (int i = 0; i < numSlots; i++)
            shipAugmentSlots.add(new AugmentSlot(this, random));

        Collections.sort(shipAugmentSlots, new Comparator<AugmentSlot>() {
            @Override
            public int compare(AugmentSlot slot1, AugmentSlot slot2) {
                return Integer.compare(slot1.getSlotCategory().ordinal(), slot2.getSlotCategory().ordinal());
            }
        });
    }

    private AugmentSlot getFreeSlotForCategory(SlotCategory slotCategory) {
        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            if (augmentSlot.isEmpty() && augmentSlot.getSlotCategory().equals(slotCategory))
                return augmentSlot;
        }

        return null;
    }

    private List<AugmentSlot> getFilledSlots() {
        List<AugmentSlot> slotList = new ArrayList<>();
        for (AugmentSlot augmentSlot : shipAugmentSlots)
            if (!augmentSlot.isEmpty())
                slotList.add(augmentSlot);

        return slotList;
    }
}
