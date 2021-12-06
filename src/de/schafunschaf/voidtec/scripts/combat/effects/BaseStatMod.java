package de.schafunschaf.voidtec.scripts.combat.effects;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import lombok.Getter;

import java.awt.*;
import java.util.Random;

@Getter
public abstract class BaseStatMod implements StatApplier {
    protected final float baseValue;
    protected UpgradeCategory category;
    protected int maxNumUpgrades;

    public BaseStatMod(float baseValue) {
        this.baseValue = baseValue;
    }

    protected float generateModValue(Random random, UpgradeQuality quality) {
        boolean isNegativeStat = random.nextInt(101) <= quality.getChanceForNegativeStats();
        float modValue = random.nextInt((int) (baseValue * 100)) + 1;
        modValue = isNegativeStat ? -modValue * quality.getNegativeModifier() / 100f : modValue * quality.getPositiveModifier() / 100f;
        return Math.round(modValue * 100) / 100f;
    }

    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, boolean flipColors) {
        float value = statMod.value;
        boolean isPositive = value >= 1f;
        String incDec = isPositive ? "increased" : "decreased";
        if (flipColors)
            isPositive = !isPositive;
        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        Color qualityColor = UpgradeQuality.getColorByName(statMod.desc);
        tooltip.addPara("  • " + description + "  (%s)", 0f, new Color[]{hlColor, hlColor, qualityColor}, incDec, Math.abs(Math.round((value - 1f) * 100)) + "%", statMod.desc);
    }

    @Override
    public boolean canApply(MutableShipStatsAPI stats) {
        return true;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {

    }
}
