package de.schafunschaf.voidtec.campaign.listeners;

import com.fs.starfarer.api.campaign.CampaignTerrainAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.campaign.listeners.ShowLootListener;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.IndEvo_ids;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import de.schafunschaf.voidtec.Settings;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VT_LootListener implements ShowLootListener {

    // Fleet loot gets handled via VT_CampaignListener
    @Override
    public void reportAboutToShowLootToPlayer(CargoAPI loot, InteractionDialogAPI dialog) {
        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        String entityType = interactionTarget.getCustomEntityType();

        if (interactionTarget instanceof CampaignTerrainAPI && ((CampaignTerrainAPI) interactionTarget).getPlugin() instanceof DebrisFieldTerrainPlugin) {
            DebrisFieldTerrainPlugin debrisFieldTerrainPlugin = (DebrisFieldTerrainPlugin) ((CampaignTerrainAPI) interactionTarget).getPlugin();

            switch (debrisFieldTerrainPlugin.params.source) {
                case BATTLE: // after battle
                case GEN: // sector generation
                case MIXED: // mission generated
                case SALVAGE: // Unknown
                case PLAYER_SALVAGE: // after station salvage
            }
        }

        if (!isNull(entityType)) {
            switch (entityType) {
                case Entities.WRECK:
                    ShipRecoverySpecial.PerShipData shipData = ((DerelictShipEntityPlugin) interactionTarget.getCustomPlugin()).getData().ship;
                    break;

                case Entities.SUPPLY_CACHE:
                case Entities.SUPPLY_CACHE_SMALL:

                case Entities.EQUIPMENT_CACHE:
                case Entities.EQUIPMENT_CACHE_SMALL:

                case Entities.WEAPONS_CACHE:
                case Entities.WEAPONS_CACHE_LOW:
                case Entities.WEAPONS_CACHE_HIGH:
                case Entities.WEAPONS_CACHE_REMNANT:

                case Entities.WEAPONS_CACHE_SMALL:
                case Entities.WEAPONS_CACHE_SMALL_LOW:
                case Entities.WEAPONS_CACHE_SMALL_HIGH:
                case Entities.WEAPONS_CACHE_SMALL_REMNANT:

                case Entities.ALPHA_SITE_WEAPONS_CACHE:

                case Entities.STATION_MINING:

                case Entities.STATION_RESEARCH:

                case Entities.ORBITAL_HABITAT:

            }

            if (Settings.isIndEvoActive) {
                switch (entityType) {
                    case IndEvo_ids.ARSENAL_ENTITY:

                    case IndEvo_ids.LAB_ENTITY:

                }
            }
        }

        if (!isNull(interactionTarget.getMarket())) {
            for (MarketConditionAPI condition : interactionTarget.getMarket().getConditions()) {
                if (condition.getId().equals(Conditions.RUINS_SCATTERED)) {

                }
                if (condition.getId().equals(Conditions.RUINS_WIDESPREAD)) {

                }
                if (condition.getId().equals(Conditions.RUINS_EXTENSIVE)) {

                }
                if (condition.getId().equals(Conditions.RUINS_VAST)) {

                }
            }

        }

    }
}
