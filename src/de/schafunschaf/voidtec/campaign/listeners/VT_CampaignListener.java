package de.schafunschaf.voidtec.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.combat.vesai.HullModManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.AugmentGenerator;
import de.schafunschaf.voidtec.helper.DamagedAugmentData;
import de.schafunschaf.voidtec.ids.VT_Items;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.MathUtils;
import de.schafunschaf.voidtec.util.ShipUtils;
import lombok.Getter;

import java.util.*;

import static com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.FleetMemberData;
import static com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.Status;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VT_CampaignListener extends BaseCampaignEventListener {

    @Getter
    private static final Map<String, Set<DamagedAugmentData>> damagedShipsInLastBattle = new HashMap<>();
    private static final List<String> preBattlePlayerFleetIDs = new ArrayList<>();

    public VT_CampaignListener(boolean permaRegister) {
        super(permaRegister);
    }

    private void savePlayerMemberIDs() {
        for (FleetMemberAPI fleetMember : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            preBattlePlayerFleetIDs.add(fleetMember.getId());
        }
    }

    // Give NPC-Fleets augments depending on fleet type, ship type and faction
    private void populateNPCShipsWithAugments(SectorEntityToken interactionTarget) {
        int battleJoinRange = Global.getSettings().getInt("battleJoinRange");
        List<CampaignFleetAPI> nearbyFleets = Misc.findNearbyFleets(interactionTarget, battleJoinRange, new Misc.FleetFilter() {
            @Override
            public boolean accept(CampaignFleetAPI curr) {
                return !curr.isPlayerFleet();
            }
        });

        if (interactionTarget instanceof CampaignFleetAPI && !interactionTarget.isPlayerFleet()) {
            nearbyFleets.add((CampaignFleetAPI) interactionTarget);
        }

        for (CampaignFleetAPI fleet : nearbyFleets) {
            AugmentGenerator.generateFleetAugments(fleet, VT_Settings.aiHullmodChance);
        }
    }

    // Clean the storage when docking at a friendly spaceport
    private void cleanHullmodData(SectorEntityToken interactionTarget) {
        if (!isNull(interactionTarget.getMarket())) {
            HullModDataStorage.getInstance().cleanDataStorage();
            if (canInstallVESAI(interactionTarget)) {
                Global.getSector().addTransientScript(new VT_DockedAtSpaceportHelper(interactionTarget.getMarket()));
            }
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (!battle.isPlayerInvolved()) {
            return;
        }

        reportDamagedAugments();
    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        if (isNull(interactionTarget)) {
            return;
        }

        savePlayerMemberIDs();
        cleanHullmodData(interactionTarget);
        populateNPCShipsWithAugments(interactionTarget);
    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        applyAfterBattleDamage(result);
    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {
        generateAugmentLoot(plugin, loot);
        recoverAugmentsFromCapturedShips(plugin, loot);
    }

    private void recoverAugmentsFromCapturedShips(FleetEncounterContextPlugin plugin, CargoAPI loot) {
        HullModDataStorage hullModDataStorage = HullModDataStorage.getInstance();
        List<FleetMemberAPI> recoveredShipsWithVESAI = new ArrayList<>();
        for (FleetMemberAPI fleetMember : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if (!preBattlePlayerFleetIDs.contains(fleetMember.getId())
                    && fleetMember.getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
                recoveredShipsWithVESAI.add(fleetMember);
            }
        }

        Random random = new Random(Misc.getSalvageSeed(plugin.getBattle().getNonPlayerCombined()));
        CargoAPI recoveredAugments = prepareAugmentsForSalvage(null, recoveredShipsWithVESAI, random);
        for (FleetMemberAPI memberAPI : recoveredShipsWithVESAI) {
            hullModDataStorage.removeHullmod(memberAPI);
        }

        loot.addAll(recoveredAugments);
    }

    private void applyAfterBattleDamage(EngagementResultAPI result) {
        EngagementResultForFleetAPI playerResult = result.getWinnerResult().isPlayer()
                                                   ? result.getWinnerResult()
                                                   : result.getLoserResult();

        for (FleetMemberAPI fleetMember : playerResult.getDeployed()) {
            applyAfterBattleDamageToAugments(result, fleetMember);
        }
    }

    private void generateAugmentLoot(FleetEncounterContextPlugin plugin, CargoAPI loot) {
        Random random = new Random(Misc.getSalvageSeed(plugin.getBattle().getNonPlayerCombined()));

        List<FleetMemberData> casualties = plugin.getLoserData().getOwnCasualties();
        casualties.addAll(plugin.getWinnerData().getOwnCasualties());

        loot.addAll(prepareAugmentsForSalvage(getUnrecoverableShips(casualties), null, random));
    }

    private void reportDamagedAugments() {
        if (!damagedShipsInLastBattle.isEmpty()) {
            AugmentManagerIntel augmentManagerIntel = AugmentManagerIntel.getInstance();
            Global.getSector()
                  .getCampaignUI()
                  .addMessage(augmentManagerIntel, CommMessageAPI.MessageClickAction.INTEL_TAB, augmentManagerIntel);

            damagedShipsInLastBattle.clear();
        }
    }

    private void applyAfterBattleDamageToAugments(EngagementResultAPI result, FleetMemberAPI fleetMember) {
        HullModManager hullModManager = HullModDataStorage.getInstance().getHullModManager(fleetMember.getId());
        if (isNull(hullModManager)) {
            return;
        }

        String shipNameWithClass = ShipUtils.generateShipNameWithClass(fleetMember);
        Set<DamagedAugmentData> damagedAugments = damagedShipsInLastBattle.get(shipNameWithClass);
        if (isNull(damagedAugments)) {
            damagedAugments = new LinkedHashSet<>();
        }

        Random random = new Random(result.getBattle().getSeed());
        float sizeMod = Misc.getSizeNum(fleetMember.getHullSpec().getHullSize());
        float armorRating = fleetMember.getHullSpec().getArmorRating();
        int damageAttempts = Math.round(fleetMember.getStatus().getHullDamageTaken() * 100 / VT_Settings.damageTakenThreshold);
        int damageChance = Math.max(
                Math.round(VT_Settings.damageChanceOnDamageTaken - VT_Settings.chanceReductionPerArmor * armorRating / sizeMod), 1);

        for (int i = 0; i < damageAttempts; i++) {
            if (MathUtils.rollSuccessful(damageChance, random)) {
                damagedAugments.add(new DamagedAugmentData(hullModManager.damageRandomAugment(1, random)));
            }
        }

        if (!damagedAugments.isEmpty()) {
            damagedShipsInLastBattle.put(shipNameWithClass, damagedAugments);
        }
    }

    private boolean canInstallVESAI(SectorEntityToken interactionTarget) {
        boolean hasSpaceport = interactionTarget.getMarket().hasSpaceport();
        boolean isNotHostile = interactionTarget.getFaction().getRelationshipLevel(Factions.PLAYER).isAtWorst(RepLevel.SUSPICIOUS);

        return hasSpaceport && isNotHostile;
    }

    private List<FleetMemberData> getUnrecoverableShips(List<FleetMemberData> casualties) {
        List<FleetMemberData> unrecoverableShips = new ArrayList<>();
        for (FleetMemberData casualty : casualties) {
            if (casualty.getStatus() == Status.DESTROYED || casualty.getStatus() == Status.DISABLED) {
                unrecoverableShips.add(casualty);
            }
        }

        return unrecoverableShips;
    }

    private CargoAPI prepareAugmentsForSalvage(List<FleetMemberData> casualties, List<FleetMemberAPI> fleetMemberList,
                                               Random salvageRandom) {
        CargoAPI augmentLoot = Global.getFactory().createCargo(true);

        if (isNull(fleetMemberList)) {
            fleetMemberList = new ArrayList<>();
        }

        if (!isNull(casualties)) {
            for (FleetMemberData casualty : casualties) {
                fleetMemberList.add(casualty.getMember());
            }
        }

        for (FleetMemberAPI fleetMember : fleetMemberList) {
            if (fleetMember.getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
                HullModManager hullModManager = HullModDataStorage.getInstance().getHullModManager(fleetMember.getId());
                for (AugmentSlot filledSlot : hullModManager.getFilledSlots()) {
                    if (salvageRandom.nextInt(100) + 1 <= VT_Settings.recoverChance) {
                        AugmentApplier slottedAugment = filledSlot.getSlottedAugment();
                        filledSlot.removeAugment();
                        if (salvageRandom.nextInt(100) + 1 <= VT_Settings.damageChanceOnDestroy) {
                            int numLevelsDamaged = salvageRandom.nextInt(3) + 1;
                            augmentLoot.addSpecial(
                                    new AugmentItemData(VT_Items.AUGMENT_ITEM, null, slottedAugment.damageAugment(numLevelsDamaged)), 1f);
                        } else {
                            augmentLoot.addSpecial(new AugmentItemData(VT_Items.AUGMENT_ITEM, null, slottedAugment), 1f);
                        }
                    }
                }
            }
        }

        return augmentLoot;
    }
}
