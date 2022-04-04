package de.schafunschaf.voidtec.combat.vesai.statmodifiers.special;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
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
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
        } else {
            stats.getDynamic()
                 .getMod(Stats.MAX_PERMANENT_HULLMODS_MOD)
                 .modifyFlat(id, generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()));
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

        generateTooltip(tooltip, statMod, description, bulletColor, parentAugment);
    }

    @Override
    public LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue) {
        boolean isPositive = minValue >= 0;
        String incDec = isPositive ? "Increases" : "Decreases";
        String hlString = "permanent hullmods";
        String description = String.format("the ships maximum amount of %s", hlString);

        return generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, hlString);
    }

    @Override
    public boolean isPercentage() {
        return false;
    }
}
