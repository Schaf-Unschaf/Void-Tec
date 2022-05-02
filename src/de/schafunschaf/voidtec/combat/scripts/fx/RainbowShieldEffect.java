package de.schafunschaf.voidtec.combat.scripts.fx;

import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.helper.ColorShifter;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class RainbowShieldEffect implements CombatScriptRunner {

    private final ColorShifter colorShifter = new ColorShifter(null);

    @Override
    public void run(ShipAPI ship, float amount, AugmentApplier augment) {
        ShieldAPI shield = ship.getShield();
        if (isNull(shield)) {
            return;
        }

        Color shiftColor = colorShifter.shiftColor(0.4f);
        shield.setInnerColor(shiftColor.darker().darker());
        shield.setRingColor(shiftColor);
    }
}
