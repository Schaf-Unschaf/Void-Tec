package de.schafunschaf.voidtec.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;

public class VT_DockedAtSpaceportHelper implements EveryFrameScript {
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (!Global.getSector().getCampaignUI().isShowingDialog())
            Global.getSector().removeScript(this);
    }
}
