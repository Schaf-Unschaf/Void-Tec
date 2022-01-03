package de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.AugmentApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.SlotCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@NoArgsConstructor
public abstract class BaseAugment implements AugmentApplier {
    protected String augmentID;
    protected String manufacturer;
    protected String name;
    protected String description;
    protected String primaryStatDescription;
    protected String secondaryStatDescription;
    protected int rarity;
    protected SlotCategory primarySlot;
    protected List<SlotCategory> secondarySlots;
    protected List<BaseStatMod> primaryStatMods;
    protected List<StatModValue<Float, Float, Boolean>> primaryStatValues;
    protected List<BaseStatMod> secondaryStatMods;
    protected List<StatModValue<Float, Float, Boolean>> secondaryStatValues;
    protected boolean qualityModifiesPrimary;
    protected boolean qualityModifiesSecondary;
    protected UpgradeQuality upgradeQuality;

    public BaseAugment(String augmentID,
                       String manufacturer,
                       String name,
                       String description,
                       String primaryStatDescription,
                       String secondaryStatDescription,
                       int rarity,
                       SlotCategory primarySlot,
                       List<SlotCategory> secondarySlots,
                       List<BaseStatMod> primaryStatMods,
                       List<StatModValue<Float, Float, Boolean>> primaryStatValues,
                       List<BaseStatMod> secondaryStatMods,
                       List<StatModValue<Float, Float, Boolean>> secondaryStatValues,
                       UpgradeQuality upgradeQuality) {
        this.augmentID = augmentID;
        this.manufacturer = manufacturer;
        this.name = name;
        this.description = description;
        this.primaryStatDescription = primaryStatDescription;
        this.secondaryStatDescription = secondaryStatDescription;
        this.rarity = rarity;
        this.primarySlot = primarySlot;
        this.secondarySlots = secondarySlots;
        this.primaryStatMods = primaryStatMods;
        this.primaryStatValues = primaryStatValues;
        this.secondaryStatMods = secondaryStatMods;
        this.secondaryStatValues = secondaryStatValues;
        if (isNull(upgradeQuality))
            this.upgradeQuality = UpgradeQuality.getRandomQuality(null);
        else
            this.upgradeQuality = upgradeQuality;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality, boolean isPrimary) {
        List<BaseStatMod> statMods = isPrimary ? getPrimaryStatMods() : getSecondaryStatMods();
        List<StatModValue<Float, Float, Boolean>> statModValues = isPrimary ? getPrimaryStatValues() : getSecondaryStatValues();

        for (int i = 0; i < statMods.size(); i++) {
            StatApplier statApplier = statMods.get(i);
            statApplier.apply(stats, id + "_" + getAugmentID(), statModValues.get(i), random, getUpgradeQuality());
        }
    }

    @Override
    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory, boolean isPrimary) {
        tooltip.addButton("", null, getUpgradeQuality().getColor(), getUpgradeQuality().getColor(), width, 0f, 3f);
        tooltip.addPara(String.format("%s (%s)", getName(), getUpgradeQuality().getName()), getUpgradeQuality().getColor(), 3f);
        tooltip.addPara(getDescription(), 3f);
        tooltip.addSpacer(3f);

        List<BaseStatMod> statMods = isPrimary ? getPrimaryStatMods() : getSecondaryStatMods();

        TooltipMakerAPI imageWithText = tooltip.beginImageWithText(slotCategory.getIcon(), 40f);
        for (StatApplier statApplier : statMods)
            statApplier.generateTooltipEntry(stats, id + getAugmentID(), imageWithText, upgradeQuality.getColor());
        tooltip.addImageWithText(3f);
        tooltip.addButton("", null, getUpgradeQuality().getColor(), getUpgradeQuality().getColor(), width, 0f, 3f);
    }
}
