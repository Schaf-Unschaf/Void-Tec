package de.schafunschaf.voidtec.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;

public class VT_WelcomeMessageScript implements EveryFrameScript {

    IntervalUtil timer = new IntervalUtil(2f, 2f);

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
        float days = Global.getSector().getClock().convertToDays(amount);
        timer.advance(days);

        if (timer.intervalElapsed()) {
            Global.getSector().getCampaignUI().addMessage(AugmentManagerIntel.getInstance(), CommMessageAPI.MessageClickAction.INTEL_TAB);
            Global.getSector().removeTransientScript(this);
        }
    }
}
