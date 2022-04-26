package de.schafunschaf.voidtec.combat.vesai.statmodifiers.special;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class SModSlot extends BaseStatMod {

    public SModSlot(String statID, String displayName) {
        super(statID, displayName);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue,
                            long randomSeed,
                            AugmentApplier parentAugment) {
        stats.getDynamic()
             .getMod(Stats.MAX_PERMANENT_HULLMODS_MOD)
             .modifyFlat(id, generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()));

    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        id = VoidTecEngineeringSuite.HULL_MOD_ID + "_" + id;
        stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus(id);
        if (isNull(statMod)) {
            return;
        }

        generateTooltip(tooltip, statMod, bulletColor, parentAugment);
    }

    @Override
    public LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue,
                                            boolean isFighterStat) {
        boolean isPositive = minValue >= 0;
        String incDec = isPositive ? "Increases" : "Decreases";
        String hlString = "permanent hullmods";
        String description = String.format("the maximum amount of %s", hlString);

        return generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, isFighterStat,
                                       hlString);
    }

    @Override
    public boolean isMult() {
        return false;
    }
}
