package de.schafunschaf.voidtec.combat.vesai.statmodifiers.weapon;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.ComparisonTools;

import java.awt.Color;
import java.util.Random;

public class WeaponRecoil extends BaseStatMod {

    public WeaponRecoil(String statID) {
        super(statID);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean> statModValue, Random random,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 1f + generateModValue(statModValue, random, parentAugment.getAugmentQuality()) / 100f);
        } else {
            stats.getRecoilDecayMult().modifyMult(id, 1f + generateModValue(statModValue, random, parentAugment.getAugmentQuality()) / 100f);
        }
        stats.getRecoilPerShotMult().modifyMult(id, 1f + generateModValue(statModValue, random, parentAugment.getAugmentQuality()) / 100f);
        stats.getMaxRecoilMult().modifyMult(id, 1f + generateModValue(statModValue, random, parentAugment.getAugmentQuality()) / 100f);
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getRecoilDecayMult().unmodify(id);
        stats.getRecoilPerShotMult().unmodify(id);
        stats.getMaxRecoilMult().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getRecoilDecayMult().getMultStatMod(id);

        String description = "Weapon recoil %s by %s";
        if (ComparisonTools.isNull(statMod)) {
            Float fighterStatValue = parentAugment.getFighterStatValue(id + "_" + statID);
            if (!ComparisonTools.isNull(fighterStatValue)) {
                description = "(Fighter) " + description;
                statMod = new MutableStat.StatMod(id + "_" + statID, null, fighterStatValue);
            } else {
                return;
            }
        }
        generateTooltip(tooltip, statMod, description, bulletColor, true, true);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue) {
        boolean isPositive = minValue <= 0;
        String incDec = isPositive ? "Dampens" : "Increases";
        String hlString1 = "recoil";
        String hlString2 = "weapons";
        String description = String.format("the %s of all %s", hlString1, hlString2);

        generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, true, hlString1, hlString2);
    }
}
