package de.schafunschaf.voidtec.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import de.schafunschaf.voidtec.campaign.scripts.VT_DockedAtSpaceportHelper;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VT_CampaignListener extends BaseCampaignEventListener {

    public VT_CampaignListener(boolean permaRegister) {
        super(permaRegister);
    }

    @Override
    public void reportShownInteractionDialog(InteractionDialogAPI dialog) {
        SectorEntityToken interactionTarget = dialog.getInteractionTarget();
        if (isNull(interactionTarget))
            return;

        if (!isNull(interactionTarget.getMarket()))
            if (canInstallVESAI(interactionTarget))
                Global.getSector().addScript(new VT_DockedAtSpaceportHelper());

            if (!isNull(interactionTarget.getCargo()) && !isNull(interactionTarget.getCargo().getFleetData())) {
                CampaignFleetAPI fleet = interactionTarget.getCargo().getFleetData().getFleet();
            }
    }

    private boolean canInstallVESAI(SectorEntityToken interactionTarget) {
        boolean hasSpaceport = interactionTarget.getMarket().hasSpaceport();
        boolean isNotHostile = interactionTarget.getFaction().getRelationshipLevel(Factions.PLAYER).isAtWorst(RepLevel.SUSPICIOUS);

        return hasSpaceport && isNotHostile;
    }

    @Override
    public void reportEncounterLootGenerated(FleetEncounterContextPlugin plugin, CargoAPI loot) {

    }
}
