package de.schafunschaf.voidtec.combat.vesai.statmodifiers.projectile;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;

public class EnergyFluxCost extends BaseStatMod {

    public EnergyFluxCost(String statID, String displayName) {
        super(statID, displayName);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
        } else {
            stats.getEnergyWeaponFluxCostMod()
                 .modifyMult(id, 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
        }
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getEnergyWeaponFluxCostMod().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getEnergyWeaponFluxCostMod().getMultBonus(id);

        String description = "%s %s by %s";
        if (ComparisonTools.isNull(statMod)) {
            Float fighterStatValue = parentAugment.getFighterStatValue(id + "_" + statID);
            if (!ComparisonTools.isNull(fighterStatValue)) {
                
                statMod = new MutableStat.StatMod(id + "_" + statID, null, fighterStatValue);
            } else {
                return;
            }
        }
        generateTooltip(tooltip, statMod, bulletColor, parentAugment);
    }

    @Override
    public LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue,
                                            boolean isFighterStat) {
        boolean isPositive = minValue <= 0;
        String incDec = isPositive ? "Lowers" : "Raises";
        String hlString1 = "flux need";
        String hlString2 = "energy based weapons";
        String description = String.format("the %s to fire %s", hlString1, hlString2);

        return generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, isFighterStat,
                                       hlString1,
                                       hlString2);
    }

    @Override
    public boolean hasNegativeValueAsBenefit() {
        return true;
    }
}
