package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.combat.ShipAPI;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import lombok.experimental.Delegate;

import java.util.HashSet;
import java.util.Set;

public class ShipStatEffectManager {

    @Delegate
    private final Set<StatApplier> uniqueStatAppliers = new HashSet<>();

    public void runScripts(ShipAPI ship, float amount) {
        for (StatApplier statApplier : uniqueStatAppliers) {
            statApplier.runCombatScript(ship, amount, null);
        }
    }
}
