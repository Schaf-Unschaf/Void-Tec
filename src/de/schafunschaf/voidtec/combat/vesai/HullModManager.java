package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.ShipUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.awt.Color;
import java.util.*;

import static com.fs.starfarer.api.combat.ShipAPI.HullSize;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class HullModManager {

    private final List<AugmentSlot> shipAugmentSlots = new ArrayList<>();
    @Getter
    private final ShipStatEffectManager shipStatEffectManager = new ShipStatEffectManager();
    @Getter
    private final String fleetMemberID;
    private final Map<String, Float> appliedModifiers = new HashMap<>();
    @Getter
    private long randomSeed;

    public HullModManager(FleetMemberAPI fleetMember) {
        this.fleetMemberID = fleetMember.getId();
        this.randomSeed = fleetMemberID.hashCode();
        HullModDataStorage.getInstance().storeShipID(fleetMemberID, this);
        generateSlotsForShip(fleetMember);
    }

    public void applyBeforeCreation(MutableShipStatsAPI stats, String id) {
        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            augmentSlot.applyBeforeCreation(stats, id);
        }
    }

    public void applyAfterCreation(ShipAPI ship, String id) {
        for (AugmentSlot augmentSlot : shipAugmentSlots) {
            augmentSlot.applyAfterCreation(ship, id);
        }
    }

    public void applySlotEffects(MutableShipStatsAPI stats, String id) {
        shipStatEffectManager.clear();
        for (int i = 0; i < shipAugmentSlots.size(); i++) {
            AugmentSlot augmentSlot = shipAugmentSlots.get(i);
            augmentSlot.applyToShip(stats, id, i + 1);
        }
    }

    public void applyFighterEffects(ShipAPI fighter, String id) {
        for (AugmentSlot flightDeckSlot : getFlightDeckSlots()) {
            flightDeckSlot.applyToFighter(fighter.getMutableStats(), id);
        }
    }

    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width) {
        String shipName = stats.getFleetMember().getShipName();
        int maxSlots = shipAugmentSlots.size();
        Color highlightColor = Misc.getHighlightColor();
        tooltip.addPara(
                "The %s is equipped with a special device, allowing her to install up to %s so called '%s'.\n" + "Each Augment can only be installed once in the device and it's impossible to reuse them afterwards.",
                10f, new Color[]{Misc.getBasePlayerColor(), highlightColor, highlightColor}, shipName, String.valueOf(maxSlots),
                "Augments");

        tooltip.addSectionHeading("VESAI Slot Status", Alignment.MID, 6f);
        tooltip.addSpacer(3f);
        showSlots(tooltip, width);
        tooltip.addSectionHeading("Modified Stats", Alignment.MID, 3f);
        tooltip.addSpacer(3f);
        listAppliedStats(tooltip, id, stats);
        addAdditionalDescriptions(tooltip);
        showSModInfo(tooltip, stats);
    }

    private void showSlots(TooltipMakerAPI tooltip, float width) {
        float buttonSize = 24f;
        float buttonPadding = 10f;
        float topBotPadding = 6f;
        float vertSepWidth = 4f;
        float panelSize = 0f;

        CustomPanelAPI slotPanel = Global.getSettings().createCustom(width, buttonSize, null);
        TooltipMakerAPI slotPanelElement = slotPanel.createUIElement(width, buttonSize, false);

        ButtonAPI lastButton = null;
        boolean lastWasUniqueSlot = false;
        boolean lastWasLockedSlot = false;
        for (AugmentSlot augmentSlot : getAllSlots()) {
            boolean isUnique = !augmentSlot.isEmpty() && augmentSlot.getSlottedAugment().isUniqueMod();
            boolean isUniqueSlot = augmentSlot.getSlotCategory() == SlotCategory.COSMETIC
                    || augmentSlot.getSlotCategory() == SlotCategory.SPECIAL;
            boolean isBetweenCategories = lastWasUniqueSlot && !isUniqueSlot
                    || !augmentSlot.isUnlocked() && !lastWasLockedSlot;
            Color slotColor = augmentSlot.isUnlocked() ? augmentSlot.getSlotCategory().getColor() : Misc.getGrayColor();
            ButtonAPI currentButton;

            currentButton = ButtonUtils.addFakeAugmentButton(slotPanelElement, buttonSize, slotColor, slotColor,
                                                             !augmentSlot.isEmpty() || !augmentSlot.isUnlocked(), isUnique);

            if (isNull(lastButton)) {
                currentButton.getPosition().inTL(0, 0);
            } else {
                float extraPadding = isBetweenCategories ? buttonPadding + buttonSize : 0f;
                currentButton.getPosition().rightOfTop(lastButton, buttonPadding + extraPadding);
                if (isBetweenCategories) {
                    UIUtils.addVerticalSeparator(slotPanelElement, vertSepWidth, buttonSize + topBotPadding, Misc.getDarkPlayerColor())
                           .getPosition()
                           .rightOfTop(lastButton, (buttonPadding + extraPadding - vertSepWidth) / 2)
                           .setYAlignOffset(3f);
                }
                panelSize += buttonPadding + extraPadding;
            }

            if (!augmentSlot.isUnlocked()) {
                slotPanelElement.addImage(VT_Icons.LOCKED_SLOT_ICON, buttonSize, 0);
                slotPanelElement.getPrev().getPosition().rightOfTop(currentButton, -buttonSize);
            }

            panelSize += buttonSize;
            lastButton = currentButton;

            lastWasUniqueSlot = isUniqueSlot;
            lastWasLockedSlot = !augmentSlot.isUnlocked();
        }

        float xPad = (width - panelSize) / 2;
        slotPanel.addUIElement(slotPanelElement).inTL(xPad, 0);
        tooltip.addCustom(slotPanel, 0f);
    }

    private void listAppliedStats(TooltipMakerAPI tooltip, String id, MutableShipStatsAPI stats) {
        for (AugmentSlot filledSlot : getFilledSlots()) {
            filledSlot.collectAppliedStats(stats, id);
        }

        List<String> modifierList = new ArrayList<>(appliedModifiers.keySet());
        Collections.sort(modifierList);
        for (String key : modifierList) {
            int value = appliedModifiers.get(key).intValue();
            if (value != 0) {
                BaseStatMod.generateStatTooltip(tooltip, key, value);
            }
        }

        appliedModifiers.clear();
    }

    private void addAdditionalDescriptions(TooltipMakerAPI tooltip) {
        for (AugmentSlot filledSlot : getFilledSlots()) {
            TextWithHighlights additionalDescription = filledSlot.getSlottedAugment().getAdditionalDescription();
            if (isNull(additionalDescription) || additionalDescription.getOriginalString().isEmpty()) {
                continue;
            }

            List<String> highlights = new ArrayList<>();
            List<Color> hlColors = new ArrayList<>();
            String augmentName = filledSlot.getSlottedAugment().getName();
            Color slotColor = filledSlot.getSlotCategory().getColor();

            highlights.add(augmentName);
            hlColors.add(slotColor);

            for (String hlString : additionalDescription.getHighlights()) {
                highlights.add(hlString);
                hlColors.add(additionalDescription.getHlColor());
            }

            tooltip.addPara(String.format("%s: %s", augmentName, additionalDescription.getDisplayString()), 3f,
                            hlColors.toArray(new Color[]{}), highlights.toArray(new String[]{}));
        }
    }

    private void showSModInfo(TooltipMakerAPI tooltip, MutableShipStatsAPI stats) {
        float maxPermanentHullmods = Global.getSettings().getFloat("maxPermanentHullmods");
        int builtInBonusSlots = (int) stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus();
        int maxBuiltInMods = (int) (maxPermanentHullmods + builtInBonusSlots);
        int currentBuiltInMods = stats.getVariant().getSMods().size();

        if (maxBuiltInMods > 0) {
            tooltip.addPara("Available Build-In Slots: %s/%s", 10f, Misc.getTextColor(), Misc.getHighlightColor(),
                            String.valueOf(currentBuiltInMods), String.valueOf(maxBuiltInMods));
        } else {
            tooltip.addPara("Available Build-In Slots: %s", 10f, Misc.getTextColor(), Misc.getNegativeHighlightColor(), "NONE");
        }
        tooltip.addPara("Some %s Augments can grant additional slots for building in hullmods.", 3f, Misc.getGrayColor(),
                        SlotCategory.SPECIAL.getColor(), SlotCategory.SPECIAL.name()).italicize();
    }

    public boolean installAugment(AugmentSlot augmentSlot, AugmentApplier augment) {
        return augmentSlot.isEmpty() && isAugmentCompatible(augmentSlot, augment) && augmentSlot.insertAugment(augment);
    }

    public boolean isAugmentCompatible(AugmentSlot augmentSlot, AugmentApplier augment) {
        if (isNull(augment) || hasSameAugmentSlotted(augment) || augment.getAugmentQuality() == AugmentQuality.DESTROYED
                || hasUniqueModInSlotType(augmentSlot, augment)) {
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

    public boolean hasSameAugmentSlotted(AugmentApplier augment) {
        for (AugmentSlot augmentSlot : getFilledSlots()) {
            if (augmentSlot.getSlottedAugment().getAugmentID().equals(augment.getAugmentID())) {
                return true;
            }
        }

        return false;
    }

    public boolean hasUniqueModInSlotType(AugmentSlot augmentSlot, AugmentApplier augment) {
        AugmentApplier slottedAugment = augmentSlot.getSlottedAugment();
        if (!isNull(slottedAugment)) {
            boolean isUniqueMod = slottedAugment.isUniqueMod();
            if (isUniqueMod) {
                return augment.isUniqueMod();
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

    public List<AugmentSlot> getAllSlots() {
        List<AugmentSlot> slotList = new ArrayList<>(getUniqueSlots());
        slotList.addAll(getSlotsForDisplay());

        return slotList;
    }

    public AugmentApplier damageRandomAugment(int numLevelsDamaged, Random random) {
        if (isNull(random)) {
            random = new Random();
        }

        List<AugmentSlot> slotsWithDamageableAugments = new ArrayList<>();

        for (AugmentSlot filledSlot : getFilledSlots()) {
            AugmentQuality augmentQuality = filledSlot.getSlottedAugment().getAugmentQuality();
            if (augmentQuality != AugmentQuality.CUSTOMISED && augmentQuality.getLowerQuality() != augmentQuality) {
                slotsWithDamageableAugments.add(filledSlot);
            }
        }

        if (slotsWithDamageableAugments.isEmpty()) {
            return null;
        }

        AugmentSlot augmentSlot = slotsWithDamageableAugments.get(random.nextInt(slotsWithDamageableAugments.size()));

        return augmentSlot.getSlottedAugment().damageAugment(numLevelsDamaged);
    }

    public void generateSlotsForShip(FleetMemberAPI fleetMember) {
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Generating slots for %s", ShipUtils.generateShipNameWithClass(fleetMember)));
        }

        Random random = new Random(randomSeed);
        HullSize hullSize = fleetMember.getVariant().getHullSize();
        boolean isPhase = fleetMember.isPhaseShip();
        int numFlightDecks = fleetMember.getNumFlightDecks();
        boolean isCarrier = numFlightDecks > 0;
        boolean isNPC = !isNull(fleetMember.getFleetData())
                && !isNull(fleetMember.getFleetData().getFleet())
                && !fleetMember.getFleetData().getFleet().isPlayerFleet();
        int maxSlots = 6;
        int numUnlockedSlots = isNPC ? random.nextInt(maxSlots) : 0;

        if (VT_Settings.randomSlotAmount) {
            numUnlockedSlots = random.nextInt(maxSlots) + 1;
        } else {
            switch (hullSize) {
                case FRIGATE:
                    numUnlockedSlots += 1;
                    break;
                case DESTROYER:
                    numUnlockedSlots += 2;
                    break;
                case CRUISER:
                    numUnlockedSlots += 3;
                    break;
                case CAPITAL_SHIP:
                    numUnlockedSlots += 4;
                    break;
            }
        }
        numUnlockedSlots = Math.min(numUnlockedSlots, maxSlots);

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
            boolean isUnlocked = i < numUnlockedSlots;
            shipAugmentSlots.add(new AugmentSlot(this, categoryPoolWithWeighting, random, isUnlocked));
            if (VT_Settings.sheepDebug) {
                log.info(String.format("Slot: %s (unlocked: %s)", shipAugmentSlots.get(i + 1).getSlotCategory().name(), isUnlocked));
            }
        }
    }

    public void addStatModifier(String statID, float value, boolean isMult) {
        if (appliedModifiers.containsKey(statID)) {
            float newValue = isMult ? appliedModifiers.get(statID) * value : appliedModifiers.get(statID) + value;
            appliedModifiers.put(statID, newValue);
        } else {
            appliedModifiers.put(statID, value);
        }
    }

    public void rollNewRandomSeed() {
        randomSeed = Misc.genRandomSeed();
    }
}
