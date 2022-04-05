package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.ShipAPI;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.ColorShifter;

import java.awt.Color;

public class RainbowShieldEffect implements CombatScriptRunner {

    private final ColorShifter colorShifter = new ColorShifter(null);

    @Override
    public void run(ShipAPI ship, float amount, AugmentApplier augment) {
        Color shiftColor = colorShifter.shiftColor(0.4f);
        ship.getShield().setInnerColor(shiftColor.darker().darker());
        ship.getShield().setRingColor(shiftColor);
    }
}
