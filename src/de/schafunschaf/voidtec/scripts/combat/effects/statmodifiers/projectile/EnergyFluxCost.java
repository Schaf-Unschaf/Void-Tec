package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.projectile;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;
import java.util.Random;

public class EnergyFluxCost extends BaseStatMod {

    public EnergyFluxCost(String statID) {
        super(statID);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 generateModValue(statModValue, random, parentAugment.getAugmentQuality()));
        } else {
            stats.getEnergyWeaponFluxCostMod()
                 .modifyPercent(id, -generateModValue(statModValue, random, parentAugment.getAugmentQuality()));
        }
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getEnergyWeaponFluxCostMod().getPercentBonus(id);

        String description = "Energy flux cost %s by %s";
        if (ComparisonTools.isNull(statMod)) {
            Float fighterStatValue = parentAugment.getFighterStatValue(id + "_" + statID);
            if (!ComparisonTools.isNull(fighterStatValue)) {
                description = "(Fighter) " + description;
                statMod = new MutableStat.StatMod(id + "_" + statID, null, fighterStatValue);
            } else {
                return;
            }
        }
        generateTooltip(tooltip, statMod, description, bulletColor, true);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue) {
        boolean isPositive = minValue <= 0;
        String incDec = isPositive ? "Lowers" : "Raises";
        String hlString1 = "flux need";
        String hlString2 = "energy based weapons";
        String description = String.format("the %s to fire %s", hlString1, hlString2);

        generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, true, hlString1, hlString2);
    }
}
