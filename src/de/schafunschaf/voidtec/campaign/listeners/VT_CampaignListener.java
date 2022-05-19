package de.schafunschaf.voidtec.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemPlugin;
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

    private void removeAugmentsFromMarket(MarketAPI market) {
        List<SubmarketAPI> vanillaMarkets = new ArrayList<>();
        vanillaMarkets.add(market.getSubmarket(Submarkets.SUBMARKET_OPEN));
        vanillaMarkets.add(market.getSubmarket(Submarkets.SUBMARKET_BLACK));
        vanillaMarkets.add(market.getSubmarket(Submarkets.GENERIC_MILITARY));

        for (SubmarketAPI submarket : vanillaMarkets) {
            if (isNull(submarket)) {
                continue;
            }

            CargoAPI submarketCargo = submarket.getCargo();
            if (isNull(submarketCargo)) {
                continue;
            }

            for (CargoStackAPI cargoStack : submarketCargo.getStacksCopy()) {
                if (cargoStack.getPlugin() instanceof AugmentItemPlugin) {
                    submarketCargo.removeStack(cargoStack);
                }
            }
        }
    }

    private void savePlayerMemberIDs() {
        for (FleetMemberAPI fleetMember : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            preBattlePlayerFleetIDs.add(fleetMember.getId());
        }
    }

    // Give NPC-Fleets augments depending on fleet type, ship type and faction
    private void populateNPCShipsWithAugments(SectorEntityToken interactionTarget) {
        List<CampaignFleetAPI> nearbyFleets = new ArrayList<>();

        if (!isNull(interactionTarget.getContainingLocation())) {
            int battleJoinRange = Global.getSettings().getInt("battleJoinRange");
            nearbyFleets = Misc.findNearbyFleets(interactionTarget, battleJoinRange, new Misc.FleetFilter() {
                @Override
                public boolean accept(CampaignFleetAPI curr) {
                    return !curr.isPlayerFleet();
                }
            });
        }

        if (interactionTarget instanceof CampaignFleetAPI && !interactionTarget.isPlayerFleet()) {
            nearbyFleets.add((CampaignFleetAPI) interactionTarget);
        }

        CampaignFleetAPI defenderFleet = interactionTarget.getMemoryWithoutUpdate().getFleet("$defenderFleet");
        if (!isNull(defenderFleet)) {
            nearbyFleets.add(defenderFleet);
        }

        MarketAPI interactionTargetMarket = interactionTarget.getMarket();
        if (!isNull(interactionTargetMarket)) {
            CampaignFleetAPI nexResponseFleet = interactionTargetMarket.getMemoryWithoutUpdate().getFleet("$nex_responseFleet");
            if (!isNull(nexResponseFleet)) {
                nearbyFleets.add(nexResponseFleet);
            }
        }

        for (CampaignFleetAPI fleet : nearbyFleets) {
            AugmentGenerator.generateFleetAugments(fleet);
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
    public void reportFleetSpawned(CampaignFleetAPI fleet) {
        if (Global.getSector().getCampaignUI().isShowingDialog()) {
            AugmentGenerator.generateFleetAugments(fleet);
        }
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

    @Override
    public void reportPlayerClosedMarket(MarketAPI market) {
        removeAugmentsFromMarket(market);
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
        if (VT_Settings.enablePlayerAugmentBattleDamage) {
            EngagementResultForFleetAPI playerResult = result.getWinnerResult().isPlayer()
                                                       ? result.getWinnerResult()
                                                       : result.getLoserResult();

            for (FleetMemberAPI fleetMember : playerResult.getDeployed()) {
                if (fleetMember.getFleetData().getFleet().isPlayerFleet()) {
                    applyAfterBattleDamageToAugments(result, fleetMember);
                }
            }
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
                AugmentApplier damagedAugment = hullModManager.damageRandomAugment(1, random);
                if (isNull(damagedAugment)) {
                    continue;
                }

                damagedAugments.add(new DamagedAugmentData(damagedAugment));
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
                    if (MathUtils.rollSuccessful(VT_Settings.recoverChance, salvageRandom)) {
                        AugmentApplier slottedAugment = filledSlot.getSlottedAugment();
                        filledSlot.removeAugment();
                        if (MathUtils.rollSuccessful(VT_Settings.destroyChanceOnRecover, salvageRandom)) {
                            augmentLoot.addSpecial(
                                    new AugmentItemData(VT_Items.AUGMENT_ITEM, null, slottedAugment.destroy()), 1f);
                        } else if (MathUtils.rollSuccessful(VT_Settings.damageChanceOnRecover, salvageRandom)) {
                            int numLevelsDamaged = salvageRandom.nextInt(5) + 1;
                            augmentLoot.addSpecial(
                                    new AugmentItemData(VT_Items.AUGMENT_ITEM, null, slottedAugment.damageAugment(numLevelsDamaged, false)),
                                    1f);
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
