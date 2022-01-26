package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.special;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;

import java.awt.Color;
import java.util.Random;

public class SModSlot extends BaseStatMod {

    public SModSlot(String statID) {
        super(statID);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 generateModValue(statModValue, random, parentAugment.getAugmentQuality()));
        } else {
            stats.getDynamic()
                 .getMod(Stats.MAX_PERMANENT_HULLMODS_MOD)
                 .modifyFlat(id, -generateModValue(statModValue, random, parentAugment.getAugmentQuality()));
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

        String description = "Permanent Build-In slots %s by %s";

        int value = (int) statMod.value;
        int maxValue = stats.getDynamic().getStat(Stats.MAX_PERMANENT_HULLMODS_MOD).getModifiedInt();
        boolean isPositive = value >= 0f;
        String incDec = isPositive ? "increased" : "decreased";

        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();

        tooltip.addPara(description, 0f, new Color[]{bulletColor, hlColor, hlColor}, incDec, String.valueOf(Math.abs(value)));
        unindent(tooltip);
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
