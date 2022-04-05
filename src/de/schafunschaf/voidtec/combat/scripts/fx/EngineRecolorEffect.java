package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;

import java.awt.Color;

public class EngineRecolorEffect implements CombatScriptRunner {

    @Override
    public void run(ShipAPI ship, float amount, AugmentApplier augment) {
        Color color = (Color) augment.getRightClickAction().getActionObject();
        ship.getEngineController().fadeToOtherColor(augment.getAugmentID(), color, Misc.scaleColor(color, 0.3f), 1f, 1f);
    }
}
