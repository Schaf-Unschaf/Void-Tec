package de.schafunschaf.voidtec.campaign.scripts;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.campaign.dialog.VT_StorageInteraction;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestPlugin;
import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.KeyEvent;

public class VT_DialogHelperOpenStorage implements EveryFrameScript {
    private final AugmentChestPlugin augmentChestPlugin;
    private final Robot robot;
    private boolean success = false;

    @SneakyThrows
    public VT_DialogHelperOpenStorage(AugmentChestPlugin augmentChestPlugin) {
        this.augmentChestPlugin = augmentChestPlugin;
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
        if (success)
            Global.getSector().removeTransientScript(this);
        else {
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
            success = Global.getSector().getCampaignUI().showInteractionDialog(new VT_StorageInteraction(augmentChestPlugin), null);
        }
    }
}
