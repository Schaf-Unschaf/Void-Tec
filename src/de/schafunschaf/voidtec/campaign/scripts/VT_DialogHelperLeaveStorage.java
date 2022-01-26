package de.schafunschaf.voidtec.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CoreUITabId;

public class VT_DialogHelperLeaveStorage implements EveryFrameScript {

    private boolean success = false;

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if (success) {
            Global.getSector().removeTransientScript(this);
        } else {
            Global.getSector().setPaused(true);
            Global.getSector().getCampaignUI().showCoreUITab(CoreUITabId.CARGO);
            success = Global.getSector().getCampaignUI().getCurrentCoreTab().equals(CoreUITabId.CARGO);
        }
    }
}
