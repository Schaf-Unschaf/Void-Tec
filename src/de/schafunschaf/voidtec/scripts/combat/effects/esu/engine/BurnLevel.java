package de.schafunschaf.voidtec.scripts.combat.effects.esu.engine;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.bountiesexpanded.util.ComparisonTools;

import java.awt.*;
import java.util.Random;

public class BurnLevel extends BaseStatMod {
    public BurnLevel(float baseValue) {
        super(baseValue);
        this.category = UpgradeCategory.ENGINE;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        stats.getMaxBurnLevel().modifyFlat(id, Math.round(generateModValue(random, quality)), quality.getName());
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {
        stats.getMaxBurnLevel().unmodify(id);
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        MutableStat.StatMod statMod = stats.getMaxBurnLevel().getFlatStatMod(id);
        if (ComparisonTools.isNull(statMod))
            return;

        String description = "Burn level %s by %s";
        generateTooltip(tooltip, statMod, description, false);
    }

    @Override
    protected void generateTooltip(TooltipMakerAPI tooltip, MutableStat.StatMod statMod, String description, boolean flipColors) {
        float value = statMod.value;
        boolean isPositive = value >= 1f;
        String incDec = isPositive ? "increased" : "decreased";
        if (flipColors)
            isPositive = !isPositive;
        Color hlColor = isPositive ? Misc.getPositiveHighlightColor() : Misc.getNegativeHighlightColor();
        Color qualityColor = UpgradeQuality.getColorByName(statMod.desc);
        tooltip.addPara("  • " + description + "  (%s)", 0f, new Color[]{hlColor, hlColor, qualityColor}, incDec, String.valueOf(Math.abs(Math.round(value))), statMod.desc);
    }
}
