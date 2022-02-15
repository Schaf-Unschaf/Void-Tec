package de.schafunschaf.voidtec.combat.vesai.statmodifiers.special;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;

import java.awt.Color;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class SModSlot extends BaseStatMod {

    public SModSlot(String statID) {
        super(statID);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 1f + generateModValue(statModValue, random, parentAugment.getAugmentQuality()) / 100f);
        } else {
            stats.getDynamic()
                 .getMod(Stats.MAX_PERMANENT_HULLMODS_MOD)
                 .modifyFlat(id, generateModValue(statModValue, random, parentAugment.getAugmentQuality()));
        }
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        setBulletMode(tooltip, bulletColor);
        MutableStat.StatMod statMod = stats.getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).getFlatBonus(id);
        if (isNull(statMod)) {
            return;
        }

        String description = "Permanent Build-In slots %s by %s";

        generateTooltip(tooltip, statMod, description, bulletColor, false, false);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue) {
        boolean isPositive = minValue >= 0;
        String incDec = isPositive ? "Increases" : "Decreases";
        String hlString = "permanent hullmods";
        String description = String.format("the ships maximum amount of %s", hlString);

        generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, false, hlString);
    }
}
