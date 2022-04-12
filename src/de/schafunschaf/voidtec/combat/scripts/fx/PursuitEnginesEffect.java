package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;

import java.awt.Color;

public class PursuitEnginesEffect implements CombatScriptRunner {

    private final Color engineColor = new Color(0, 150, 255, 255);

    @Override
    public void run(ShipAPI ship, float amount, AugmentApplier augment) {
        if (ship.isEngineBoostActive()) {
            float modifier = augment.getAugmentQuality().getModifier();
            float effectLevel = modifier - 0.5f;
            ShipEngineControllerAPI engineController = ship.getEngineController();
            engineController.extendFlame(augment.getAugmentID(), effectLevel, effectLevel, effectLevel);
            engineController.fadeToOtherColor(augment.getAugmentID(), engineColor, null, 1f, 0.5f);
        }
    }
}
