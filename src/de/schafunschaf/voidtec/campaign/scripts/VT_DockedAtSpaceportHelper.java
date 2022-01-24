package de.schafunschaf.voidtec.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import lombok.Getter;

@Getter
public class VT_DockedAtSpaceportHelper implements EveryFrameScript {
    private final MarketAPI market;

    public VT_DockedAtSpaceportHelper(MarketAPI market) {
        this.market = market;
    }

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
        if (!Global.getSector().getCampaignUI().isShowingDialog()) {
            Global.getSector().removeTransientScript(this);
        }
    }
}
