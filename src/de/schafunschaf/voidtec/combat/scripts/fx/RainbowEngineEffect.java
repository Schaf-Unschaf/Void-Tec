package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.ColorShifter;

import java.awt.Color;

public class RainbowEngineEffect implements CombatScriptRunner {

    private final ColorShifter colorShifter = new ColorShifter(null);

    @Override
    public void run(ShipAPI ship, float amount, AugmentApplier augment) {
        Color shiftColor = colorShifter.shiftColor(0.4f);
        ship.getEngineController().fadeToOtherColor(augment.getAugmentID(), shiftColor, Misc.scaleColor(shiftColor, 0.3f), 1f, 1f);
    }
}
