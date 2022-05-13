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
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModProvider;
import de.schafunschaf.voidtec.helper.AppliedStatModifier;
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

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class HullModManager {

    private final List<AugmentSlot> shipAugmentSlots = new ArrayList<>();
    @Getter
    private final ShipStatEffectManager shipStatEffectManager = new ShipStatEffectManager();
    @Getter
    private final String fleetMemberID;
    private final String sModPenalty = "_sModOverLimitPenalty";
    private final List<AppliedStatModifier> appliedModifiers = new ArrayList<>();
    @Getter
    private final long randomSeed;

    private final float supplyPenalty = 100f;
    private final float recoveryPenalty = 100f;
    private final float performancePenalty = 100f / 3;

    public HullModManager(FleetMemberAPI fleetMember) {
        this.fleetMemberID = fleetMember.getId();
        this.randomSeed = fleetMemberID.hashCode();
        HullModDataStorage.getInstance().storeShipID(fleetMemberID, this);
        generateSlotsForShip(fleetMember);
    }

    public void removeHullMod(FleetMemberAPI fleetMember) {
        if (!fleetMemberID.equals(fleetMember.getId())) {
            return;
        }

        for (AugmentSlot slot : getAllSlots()) {
            slot.removeAugment();
        }

        fleetMember.getVariant().removePermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
        HullModDataStorage.getInstance().getDataStorage().remove(fleetMemberID);
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
        tooltip.addSectionHeading("Modified Ship Stats", Alignment.MID, 3f);
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
            boolean hasDestroyedAugment = !augmentSlot.isEmpty() && augmentSlot.getSlottedAugment().isDestroyed();
            Color slotColor = augmentSlot.isUnlocked() ? augmentSlot.getSlotCategory().getColor() : Misc.getGrayColor();
            if (hasDestroyedAugment) {
                slotColor = Misc.scaleColorOnly(slotColor, 0.5f);
            }
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
            if (hasDestroyedAugment) {
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

        List<AppliedStatModifier> positiveModList = new ArrayList<>();
        List<AppliedStatModifier> negativeModList = new ArrayList<>();
        List<AppliedStatModifier> positiveFighterModList = new ArrayList<>();
        List<AppliedStatModifier> negativeFighterModList = new ArrayList<>();
        Collections.sort(appliedModifiers, new Comparator<AppliedStatModifier>() {
            @Override
            public int compare(AppliedStatModifier o1, AppliedStatModifier o2) {
                int isFighterStat = Boolean.compare(o1.isFighterStat(), o2.isFighterStat());

                if (isFighterStat == 0) {
                    return o1.getStatID().compareTo(o2.getStatID());
                } else {
                    return isFighterStat;
                }
            }
        });

        for (AppliedStatModifier asm : appliedModifiers) {
            int statValue = asm.getValue();
            boolean hasNegativeValueAsBenefit = asm.isHasNegativeAsBenefit();
            if (statValue != 0) {
                if (hasNegativeValueAsBenefit && statValue < 0 || !hasNegativeValueAsBenefit && statValue > 0) {
                    if (asm.isFighterStat()) {
                        positiveFighterModList.add(asm);
                    } else {
                        positiveModList.add(asm);
                    }
                } else {
                    if (asm.isFighterStat()) {
                        negativeFighterModList.add(asm);
                    } else {
                        negativeModList.add(asm);
                    }
                }
            }
        }

        for (AppliedStatModifier asm : positiveModList) {
            BaseStatMod.generateStatTooltip(tooltip, asm.getStatID(), asm.getValue(), asm.isFighterStat());
        }

        for (AppliedStatModifier asm : negativeModList) {
            BaseStatMod.generateStatTooltip(tooltip, asm.getStatID(), asm.getValue(), asm.isFighterStat());
        }

        if (!positiveFighterModList.isEmpty() || !negativeFighterModList.isEmpty()) {
            tooltip.addSectionHeading("Modified Fighter Stats", Alignment.MID, 3f);
            tooltip.addSpacer(3f);

            for (AppliedStatModifier asm : positiveFighterModList) {
                BaseStatMod.generateStatTooltip(tooltip, asm.getStatID(), asm.getValue(), asm.isFighterStat());
            }

            for (AppliedStatModifier asm : negativeFighterModList) {
                BaseStatMod.generateStatTooltip(tooltip, asm.getStatID(), asm.getValue(), asm.isFighterStat());
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
        int numOverLimitMods = Math.abs(currentBuiltInMods - maxBuiltInMods);
        boolean overLimit = currentBuiltInMods > maxBuiltInMods;

        if (VT_Settings.sModPenalty && overLimit) {
            String sumSupplyPenalty = (int) (supplyPenalty * numOverLimitMods) + "%";
            String sumRecoveryPenalty = (int) (recoveryPenalty * numOverLimitMods) + "%";
            String sumPerformancePenalty = Math.min(Math.round(performancePenalty * numOverLimitMods), 100) + "%";

            tooltip.addPara("Currently used Build-In Slots: %s / %s",
                            10f, new Color[]{Misc.getNegativeHighlightColor(), Misc.getHighlightColor()},
                            String.valueOf(currentBuiltInMods), String.valueOf(maxBuiltInMods));
            tooltip.addPara("Your ship is over its Build-In capacity!", Misc.getNegativeHighlightColor(), 6f);
            tooltip.addPara("It will suffer a penalty of %s to its maintenance and recovery cost as well as a %s shortened PPT.",
                            6f, Misc.getNegativeHighlightColor(), sumSupplyPenalty, sumPerformancePenalty, sumRecoveryPenalty);
        } else if (maxBuiltInMods > 0) {
            Color color = currentBuiltInMods < maxBuiltInMods ? Misc.getPositiveHighlightColor() : Misc.getHighlightColor();
            tooltip.addPara("Currently used Build-In Slots: %s / %s", 10f, color, String.valueOf(currentBuiltInMods),
                            String.valueOf(maxBuiltInMods));
        } else {
            tooltip.addPara("No Build-In Slots available", Misc.getGrayColor(), 10f);
        }
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

        return augmentSlot.getSlottedAugment().damageAugment(numLevelsDamaged, false);
    }

    public void generateSlotsForShip(FleetMemberAPI fleetMember) {
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Generating slots for %s", ShipUtils.generateShipNameWithClass(fleetMember)));
        }

        Random random = new Random(randomSeed);
        boolean isPhase = fleetMember.isPhaseShip();
        int numFlightDecks = fleetMember.getNumFlightDecks();
        boolean isCarrier = numFlightDecks > 0;
        boolean isNPC = !isNull(fleetMember.getFleetData())
                && !isNull(fleetMember.getFleetData().getFleet())
                && !fleetMember.getFleetData().getFleet().isPlayerFleet();
        int maxSlots = 6;
        int numUnlockedSlots = isNPC ? random.nextInt(maxSlots) : VT_Settings.unlockedSlots;

        if (VT_Settings.randomSlotAmount) {
            numUnlockedSlots = random.nextInt(maxSlots) + 1;
        }

        numUnlockedSlots = Math.min(numUnlockedSlots, maxSlots);

        shipAugmentSlots.add(new AugmentSlot(this, SlotCategory.COSMETIC, true));
        shipAugmentSlots.add(new AugmentSlot(this, SlotCategory.SPECIAL, true));
        if (VT_Settings.sheepDebug) {
            log.info(String.format("Slot: %s (unlocked: %s)", shipAugmentSlots.get(0).getSlotCategory().name(), true));
        }

        Map<SlotCategory, Integer> categoryPoolWithWeighting = new HashMap<>();
        for (SlotCategory allowedCategory : SlotCategory.getGeneralCategories()) {
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

    public void addStatModifier(String statID, float value, boolean isMult, boolean isFighterStat) {
        boolean hasNegativeValueAsBenefit = StatModProvider.getStatMod(statID).hasNegativeValueAsBenefit();
        AppliedStatModifier nextASM = new AppliedStatModifier(statID, (int) value, isMult, isFighterStat, hasNegativeValueAsBenefit);
        boolean foundExistingASM = false;

        for (AppliedStatModifier asm : appliedModifiers) {
            if (asm.equals(nextASM)) {
                asm.update(nextASM.getValue());
                foundExistingASM = true;
            }

            if (foundExistingASM) {
                return;
            }
        }

        appliedModifiers.add(nextASM);
    }

    public void addOverLimitPenalty(MutableShipStatsAPI stats, String id) {
        float maxPermanentHullmods = Global.getSettings().getFloat("maxPermanentHullmods");
        int builtInBonusSlots = (int) stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus();
        int maxBuiltInMods = (int) (maxPermanentHullmods + builtInBonusSlots);
        int currentBuiltInMods = stats.getVariant().getSMods().size();
        int numOverLimitMods = Math.abs(currentBuiltInMods - maxBuiltInMods);
        boolean overLimit = currentBuiltInMods > maxBuiltInMods;

        if (overLimit) {
            stats.getSuppliesPerMonth().modifyPercent(id + sModPenalty, supplyPenalty * numOverLimitMods);
            stats.getSuppliesToRecover().modifyPercent(id + sModPenalty, recoveryPenalty * numOverLimitMods);
            stats.getPeakCRDuration().modifyPercent(id + sModPenalty, -Math.min(Math.round(performancePenalty * numOverLimitMods), 100));
        }
    }

    public void unlockAllSlots() {
        for (AugmentSlot slot : getAllSlots()) {
            if (!slot.isUnlocked()) {
                slot.unlockSlot();
            }
        }
    }
}
