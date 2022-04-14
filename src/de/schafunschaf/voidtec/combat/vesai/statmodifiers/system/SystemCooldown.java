package de.schafunschaf.voidtec.combat.vesai.statmodifiers.system;

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

public class SystemCooldown extends BaseStatMod {

    public SystemCooldown(String statID, String displayName) {
        super(statID, displayName);
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, StatModValue<Float, Float, Boolean, Boolean> statModValue, long randomSeed,
                            AugmentApplier parentAugment) {
        if (parentAugment.getInstalledSlot().getSlotCategory() == SlotCategory.FLIGHT_DECK) {
            parentAugment.updateFighterStatValue(id + "_" + statID,
                                                 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
        } else {
            stats.getSystemCooldownBonus()
                 .modifyMult(id, 1f + generateModValue(statModValue, randomSeed, parentAugment.getAugmentQuality()) / 100f);
        }
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getSystemCooldownBonus().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, Color bulletColor,
                                     AugmentApplier parentAugment) {
        MutableStat.StatMod statMod = stats.getSystemCooldownBonus().getMultBonus(id);

        String description = "%s %s by %s";
        if (ComparisonTools.isNull(statMod)) {
            Float fighterStatValue = parentAugment.getFighterStatValue(id + "_" + statID);
            if (!ComparisonTools.isNull(fighterStatValue)) {
                
                statMod = new MutableStat.StatMod(id + "_" + statID, null, fighterStatValue);
            } else {
                return;
            }
        }
        generateTooltip(tooltip, statMod, description, bulletColor, parentAugment);
    }

    @Override
    public LabelAPI generateStatDescription(TooltipMakerAPI tooltip, Color bulletColor, float minValue, float maxValue,
                                            boolean isFighterStat) {
        boolean isPositive = minValue <= 0;
        String incDec = isPositive ? "Reduces" : "Increases";
        String hlString = "system ability cooldown";
        String description = String.format("the %s", hlString);

        return generateStatDescription(tooltip, description, incDec, bulletColor, minValue, maxValue, isPositive, isFighterStat,
                                       hlString);
    }

    @Override
    public boolean hasNegativeValueAsBenefit() {
        return true;
    }
}
