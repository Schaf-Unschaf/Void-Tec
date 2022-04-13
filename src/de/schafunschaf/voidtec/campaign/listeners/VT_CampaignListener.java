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

    public VT_CampaignListener(boolean permaRegister) {
        super(permaRegister);
    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        if (isNull(interactionTarget)) {
            return;
        }

        // Clean the storage when docking at a friendly spaceport
        if (!isNull(interactionTarget.getMarket())) {
            HullModDataStorage.getInstance().cleanDataStorage();
            if (canInstallVESAI(interactionTarget)) {
                Global.getSector().addTransientScript(new VT_DockedAtSpaceportHelper(interactionTarget.getMarket()));
            }
        }

        // Give NPC-Fleets augments depending on fleet type, ship type and faction
        if (interactionTarget instanceof CampaignFleetAPI && !interactionTarget.isPlayerFleet()) {
            AugmentGenerator.generateFleetAugments((CampaignFleetAPI) interactionTarget, VT_Settings.aiHullmodChance);
        }
    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {
        Random random = new Random(Misc.getSalvageSeed(plugin.getBattle().getNonPlayerCombined()));

        List<FleetMemberData> casualties = plugin.getLoserData().getOwnCasualties();
        casualties.addAll(plugin.getWinnerData().getOwnCasualties());

        loot.addAll(prepareAugmentsForSalvage(getUnrecoverableShips(casualties), random));
    }

    @Override
    public void reportPlayerEngagement(EngagementResultAPI result) {
        EngagementResultForFleetAPI playerResult = result.getWinnerResult().isPlayer()
                                                   ? result.getWinnerResult()
                                                   : result.getLoserResult();

        for (FleetMemberAPI fleetMember : playerResult.getDeployed()) {
            applyAfterBattleDamageToAugments(result, fleetMember);
        }
    }

    @Override
    public void reportBattleOccurred(CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (!battle.isPlayerInvolved()) {
            return;
        }

        AugmentManagerIntel augmentManagerIntel = AugmentManagerIntel.getInstance();
        Global.getSector()
              .getCampaignUI()
              .addMessage(augmentManagerIntel, CommMessageAPI.MessageClickAction.INTEL_TAB, augmentManagerIntel);

        damagedShipsInLastBattle.clear();
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
        int damageAttempts = Math.round(fleetMember.getStatus().getHullDamageTaken() * 100 / 15);

        for (int i = 0; i < damageAttempts; i++) {
            if (MathUtils.rollSuccessful(VT_Settings.damageChanceOnDamage, random)) {
                damagedAugments.add(new DamagedAugmentData(hullModManager.damageRandomAugment(5, random)));
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

    private CargoAPI prepareAugmentsForSalvage(List<FleetMemberData> casualties, Random salvageRandom) {
        CargoAPI augmentLoot = Global.getFactory().createCargo(true);

        for (FleetMemberData casualty : casualties) {
            if (casualty.getMember().getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
                HullModManager hullModManager = HullModDataStorage.getInstance().getHullModManager(casualty.getMember().getId());
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
