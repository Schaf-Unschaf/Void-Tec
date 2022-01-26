package de.schafunschaf.voidtec.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.VT_Settings;
import de.schafunschaf.voidtec.campaign.LootCategory;
import de.schafunschaf.voidtec.campaign.ids.VT_Items;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentItemData;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentSlot;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.HullModDataStorage;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.HullModManager;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.FleetMemberData;
import static com.fs.starfarer.api.campaign.FleetEncounterContextPlugin.Status;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VT_CampaignListener extends BaseCampaignEventListener {

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

        // Give NPC-Fleets augments depending on fleet type and faction
        if (interactionTarget instanceof CampaignFleetAPI && !interactionTarget.isPlayerFleet()) {
            FleetDataAPI fleetData = ((CampaignFleetAPI) interactionTarget).getFleetData();
            LootCategory lootCategory = LootCategory.getFleetType(fleetData.getFleet());
            if (isNull(lootCategory)) {
                return;
            }

            for (FleetMemberAPI fleetMember : fleetData.getMembersListCopy()) {
                if (fleetMember.isStation()) {
                    continue;
                }

                Random random = new Random(fleetMember.getId().hashCode() * 1337L);
                if (random.nextInt(100) + 1 <= VT_Settings.aiHullmodChance) {
                    ShipVariantAPI variant = fleetMember.getVariant();
                    if (!variant.hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
                        variant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);
                        HullModManager hullModManager = new HullModManager(fleetMember);
                        hullModManager.fillUnlockedSlots(fleetData.getFleet().getFaction(), lootCategory.getQualityRange(), random);
                        HullModDataStorage.getInstance().storeShipID(fleetMember.getId(), hullModManager);
                    }
                }
            }
        }
    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {
        Random random = new Random(Misc.getSalvageSeed(plugin.getBattle().getNonPlayerCombined()));

        List<FleetMemberData> casualties = plugin.getLoserData().getOwnCasualties();
        casualties.addAll(plugin.getWinnerData().getOwnCasualties());

        loot.addAll(prepareAugmentsForSalvage(getUnrecoverableShips(casualties), random));
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
                        if (salvageRandom.nextInt(100) + 1 <= VT_Settings.damageChance) {
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
