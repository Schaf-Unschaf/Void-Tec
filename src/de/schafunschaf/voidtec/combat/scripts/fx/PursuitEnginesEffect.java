package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipEngineControllerAPI;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.engine.VT_PursuitEngines;

public class PursuitEnginesEffect implements CombatScriptRunner {

    @Override
    public void run(ShipAPI ship, float amount, AugmentApplier augment) {
        if (ship.isEngineBoostActive()) {
            float modifier = augment.getAugmentQuality().getModifier();
            float effectLevel = modifier - 0.5f;
            ShipEngineControllerAPI engineController = ship.getEngineController();
            engineController.extendFlame(augment.getAugmentID(), effectLevel, effectLevel, effectLevel);
            engineController.fadeToOtherColor(augment.getAugmentID(), VT_PursuitEngines.engineColor, null, 1f, 0.5f);
        }
    }
}
