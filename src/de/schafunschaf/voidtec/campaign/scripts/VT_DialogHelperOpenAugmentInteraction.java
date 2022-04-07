package de.schafunschaf.voidtec.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.campaign.dialog.VT_AugmentInteraction;
import de.schafunschaf.voidtec.combat.vesai.RightClickAction;
import lombok.SneakyThrows;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class VT_DialogHelperOpenAugmentInteraction implements EveryFrameScript {

    private final RightClickAction rightClickAction;
    private final Robot robot;
    private boolean success = false;

    @SneakyThrows
    public VT_DialogHelperOpenAugmentInteraction(RightClickAction rightClickAction) {
        this.rightClickAction = rightClickAction;
        this.robot = new Robot();
    }

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
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
            success = Global.getSector().getCampaignUI().showInteractionDialog(new VT_AugmentInteraction(rightClickAction), null);
        }
    }
}
