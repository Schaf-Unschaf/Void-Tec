package de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.VT_Strings;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.*;
import de.schafunschaf.voidtec.util.TextWithHighlights;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@NoArgsConstructor
public class BaseAugment implements AugmentApplier {
    protected String augmentID;
    protected String manufacturer;
    protected String name;
    protected TextWithHighlights description;
    protected int rarity;
    protected SlotCategory primarySlot;
    protected List<BaseStatMod> primaryStatMods;
    protected List<StatModValue<Float, Float, Boolean>> primaryStatValues;
    protected List<SlotCategory> secondarySlots;
    protected List<BaseStatMod> secondaryStatMods;
    protected List<StatModValue<Float, Float, Boolean>> secondaryStatValues;
    protected AugmentQuality augmentQuality;
    protected TextWithHighlights combatScriptDescription;
    protected CombatScriptRunner combatScript;
    protected AugmentQuality initialQuality;
    private AugmentSlot installedSlot;

    public BaseAugment(AugmentData augmentData, AugmentQuality augmentQuality, Random random) {
        this.augmentID = augmentData.getAugmentID();
        this.manufacturer = augmentData.getManufacturer();
        this.name = augmentData.getName();
        this.description = augmentData.getDescription();
        this.rarity = augmentData.getRarity();
        this.primarySlot = augmentData.getPrimarySlot();
        this.primaryStatMods = augmentData.getPrimaryStatMods();
        this.primaryStatValues = augmentData.getPrimaryStatValues();
        this.secondarySlots = augmentData.getSecondarySlots();
        this.secondaryStatMods = augmentData.getSecondaryStatMods();
        this.secondaryStatValues = augmentData.getSecondaryStatValues();
        this.augmentQuality = isNull(augmentQuality)
                ? AugmentQuality.getRandomQualityInRange(augmentData.getAugmentQualityRange(), random, augmentData.isEqualQualityRoll())
                : augmentQuality;
        this.initialQuality = augmentQuality;
        this.combatScriptDescription = augmentData.getCombatScriptDescription();
        this.combatScript = augmentData.getCombatScript();
    }

    public BaseAugment(String augmentID,
                       String manufacturer,
                       String name,
                       TextWithHighlights description,
                       int rarity,
                       SlotCategory primarySlot,
                       List<BaseStatMod> primaryStatMods,
                       List<StatModValue<Float, Float, Boolean>> primaryStatValues,
                       List<SlotCategory> secondarySlots,
                       List<BaseStatMod> secondaryStatMods,
                       List<StatModValue<Float, Float, Boolean>> secondaryStatValues,
                       AugmentQuality augmentQuality,
                       TextWithHighlights combatScriptDescription,
                       CombatScriptRunner combatScript
    ) {
        this.augmentID = augmentID;
        this.manufacturer = manufacturer;
        this.name = name;
        this.description = description;
        this.rarity = rarity;
        this.primarySlot = primarySlot;
        this.primaryStatMods = primaryStatMods;
        this.primaryStatValues = primaryStatValues;
        this.secondarySlots = secondarySlots;
        this.secondaryStatMods = secondaryStatMods;
        this.secondaryStatValues = secondaryStatValues;
        this.augmentQuality = augmentQuality;
        this.initialQuality = augmentQuality;
        this.combatScriptDescription = combatScriptDescription;
        this.combatScript = combatScript;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, AugmentQuality quality, boolean isPrimary) {
        if (augmentQuality == AugmentQuality.DESTROYED)
            return;

        List<BaseStatMod> statMods = isPrimary ? getPrimaryStatMods() : getSecondaryStatMods();
        List<StatModValue<Float, Float, Boolean>> statModValues = isPrimary ? getPrimaryStatValues() : getSecondaryStatValues();

        for (int i = 0; i < statMods.size(); i++) {
            StatApplier statApplier = statMods.get(i);
            statApplier.apply(stats, id + "_" + getAugmentID(), statModValues.get(i), random, getAugmentQuality());
        }
    }

    @Override
    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory, boolean isPrimary, boolean isItemTooltip) {
        tooltip.addButton("", null, getAugmentQuality().getColor(), getAugmentQuality().getColor(), width, 0f, 3f);
        tooltip.addPara(String.format("%s (%s)", getName(), getAugmentQuality().getName()), getAugmentQuality().getColor(), 3f);
        if (isItemTooltip || slotCategory == SlotCategory.SPECIAL)
            tooltip.addPara(getDescription().getDisplayString(), 3f, Misc.getHighlightColor(), getDescription().getHighlights());
        tooltip.addSpacer(3f);

        List<BaseStatMod> statMods = isPrimary ? getPrimaryStatMods() : getSecondaryStatMods();
        if (!isNull(statMods) && !statMods.isEmpty()) {
            Color bulletColor = primarySlot.getColor();

            if (!isNull(installedSlot))
                bulletColor = installedSlot.getSlotCategory().getColor();

            TooltipMakerAPI imageWithText = tooltip.beginImageWithText(slotCategory.getIcon(), 40f);
            if (augmentQuality == AugmentQuality.DESTROYED)
                imageWithText.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), 0f);
            else
                for (StatApplier statApplier : statMods)
                    statApplier.generateTooltipEntry(stats, id + "_" + getAugmentID(), imageWithText, bulletColor);

            tooltip.addImageWithText(3f);
        }

        if (!isNull(combatScriptDescription) && !combatScriptDescription.getDisplayString().isEmpty()) {
            tooltip.addPara(getCombatScriptDescription().getDisplayString(), 3f, Misc.getHighlightColor(), getCombatScriptDescription().getHighlights());
        }

        tooltip.addButton("", null, getAugmentQuality().getColor(), getAugmentQuality().getColor(), width, 0f, 3f);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, boolean isPrimary, float padding) {
        if (augmentQuality == AugmentQuality.DESTROYED) {
            tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), 0f);
            return;
        }

        List<BaseStatMod> statMods = isPrimary ? getPrimaryStatMods() : getSecondaryStatMods();
        if (isNull(statMods) || statMods.isEmpty())
            return;

        List<StatModValue<Float, Float, Boolean>> statModValues = isPrimary ? getPrimaryStatValues() : getSecondaryStatValues();
        Color bulletColor = primarySlot.getColor();

        if (!isNull(installedSlot))
            bulletColor = installedSlot.getSlotCategory().getColor();

        for (int i = 0; i < statMods.size(); i++) {
            StatApplier statApplier = statMods.get(i);
            float avgModValue = statModValues.get(i).maxValue - statModValues.get(i).minValue;
            statApplier.generateStatDescription(tooltip, bulletColor, avgModValue);
        }
    }

    @Override
    public void runCombatScript(ShipAPI ship, float amount) {
        if (!isNull(combatScript))
            combatScript.run(ship, amount);
    }

    @Override
    public AugmentApplier damageAugment(int numLevelsDamaged) {
        for (int i = 0; i < numLevelsDamaged; i++)
            augmentQuality = augmentQuality.getLowerQuality();

        return this;
    }

    @Override
    public AugmentApplier repairAugment(int numLevelsRepaired) {
        for (int i = 0; i < numLevelsRepaired; i++)
            augmentQuality = augmentQuality.getHigherQuality();

        return this;
    }

    @Override
    public void installAugment(AugmentSlot augmentSlot) {
        this.installedSlot = augmentSlot;
    }

    @Override
    public void removeAugment() {
        installedSlot = null;
    }
}
