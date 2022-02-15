package de.schafunschaf.voidtec.combat.vesai.augments;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.combat.vesai.AfterCreationEffect;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.RainbowString;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Strings;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.Color;
import java.util.*;

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
    protected List<StatApplier> primaryStatMods;
    protected List<StatModValue<Float, Float, Boolean>> primaryStatValues;
    protected List<SlotCategory> secondarySlots;
    protected List<StatApplier> secondaryStatMods;
    protected List<StatModValue<Float, Float, Boolean>> secondaryStatValues;
    protected AugmentQuality augmentQuality;
    protected TextWithHighlights combatScriptDescription;
    protected CombatScriptRunner combatScriptRunner;
    protected TextWithHighlights additionalDescription;
    protected AfterCreationEffect afterCreationEffect;
    protected AugmentQuality initialQuality;
    private AugmentSlot installedSlot;
    private Map<String, Float> appliedFighterValues;

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
                              ? AugmentQuality.getRandomQualityInRange(augmentData.getAugmentQualityRange(), random,
                                                                       augmentData.isEqualQualityRoll())
                              : augmentQuality;
        this.initialQuality = augmentQuality;
        this.combatScriptDescription = augmentData.getCombatScriptDescription();
        this.combatScriptRunner = augmentData.getCombatScript();
        this.additionalDescription = augmentData.getAdditionalDescription();
        this.afterCreationEffect = augmentData.getAfterCreationEffect();
        this.appliedFighterValues = new HashMap<>();
    }

    @Override
    public void applyAfterCreation(ShipAPI ship, String id) {
        if (!isNull(afterCreationEffect)) {
            afterCreationEffect.applyAfterCreation(ship, id);
        }
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, Random random) {
        if (augmentQuality == AugmentQuality.DESTROYED) {
            return;
        }

        boolean inPrimarySlot = isInPrimarySlot();

        List<StatApplier> statMods = inPrimarySlot ? getPrimaryStatMods() : getSecondaryStatMods();
        List<StatModValue<Float, Float, Boolean>> statModValues = inPrimarySlot ? getPrimaryStatValues() : getSecondaryStatValues();

        for (int i = 0; i < statMods.size(); i++) {
            StatApplier statApplier = statMods.get(i);
            statApplier.applyToShip(stats, id + "_" + getAugmentID(), statModValues.get(i), random, this);
        }
    }

    @Override
    public void applyToFighter(MutableShipStatsAPI stats, String id) {
        if (augmentQuality == AugmentQuality.DESTROYED) {
            return;
        }

        List<StatApplier> statMods = isInPrimarySlot() ? getPrimaryStatMods() : getSecondaryStatMods();

        for (StatApplier statApplier : statMods) {
            float fighterStatValue = getFighterStatValue(id + "_" + augmentID + "_" + statApplier.getStatID());
            statApplier.applyToFighter(stats, id, fighterStatValue);
        }
    }

    @Override
    public void generateTooltip(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip, float width, SlotCategory slotCategory,
                                boolean isItemTooltip, Color bulletColorOverride) {
        tooltip.addButton("", null, getAugmentQuality().getColor(), getAugmentQuality().getColor(), width, 0f, 3f);

        if (getName().toLowerCase().contains("rainbow")) {
            RainbowString rainbowString = new RainbowString(getName(), Color.RED, 20);
            tooltip.addPara(rainbowString.getConvertedString(), 3f, rainbowString.getHlColors(), rainbowString.getHlStrings());
        } else {
            tooltip.addPara(String.format("%s (%s)", getName(), getAugmentQuality().getName()), getAugmentQuality().getColor(), 3f);
        }

        if (isItemTooltip || slotCategory == SlotCategory.SPECIAL || slotCategory == SlotCategory.COSMETIC) {
            tooltip.addPara(getDescription().getDisplayString(), 3f, Misc.getHighlightColor(), getDescription().getHighlights());
        }
        tooltip.addSpacer(3f);

        List<StatApplier> statMods = isInPrimarySlot() ? getPrimaryStatMods() : getSecondaryStatMods();
        if (!isNull(statMods) && !statMods.isEmpty()) {
            Color bulletColor = isNull(bulletColorOverride) ? primarySlot.getColor() : bulletColorOverride;

            if (isNull(bulletColorOverride) && !isNull(installedSlot)) {
                bulletColor = installedSlot.getSlotCategory().getColor();
            }

            TooltipMakerAPI imageWithText = tooltip.beginImageWithText(slotCategory.getIcon(), 40f);
            if (augmentQuality == AugmentQuality.DESTROYED) {
                imageWithText.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), 0f);
            } else {
                for (StatApplier statApplier : statMods) {
                    statApplier.generateTooltipEntry(stats, id + "_" + getAugmentID(), imageWithText, bulletColor, this);
                }
            }

            tooltip.addImageWithText(3f);
        }

        addAdditionalDescriptions(tooltip);

        tooltip.addButton("", null, getAugmentQuality().getColor(), getAugmentQuality().getColor(), width, 0f, 3f);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, float padding, Boolean isPrimary, Color bulletColorOverride) {
        if (augmentQuality == AugmentQuality.DESTROYED) {
            tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), 0f);
            return;
        }

        boolean inPrimarySlot = isNull(isPrimary) ? isInPrimarySlot() : isPrimary;

        List<StatApplier> statMods = inPrimarySlot ? getPrimaryStatMods() : getSecondaryStatMods();
        if (isNull(statMods) || statMods.isEmpty()) {
            if (isPrimary) {
                addAdditionalDescriptions(tooltip);
            }

            return;
        }

        List<StatModValue<Float, Float, Boolean>> statModValues = inPrimarySlot ? getPrimaryStatValues() : getSecondaryStatValues();
        Color bulletColor = isNull(bulletColorOverride) ? primarySlot.getColor() : bulletColorOverride;

        if (isNull(bulletColorOverride) && !isNull(installedSlot)) {
            bulletColor = installedSlot.getSlotCategory().getColor();
        }

        for (int i = 0; i < statMods.size(); i++) {
            StatApplier statApplier = statMods.get(i);
            StatModValue<Float, Float, Boolean> statModValue = statModValues.get(i);
            float mult = statModValue.getsModified ? augmentQuality.getModifier() : 1f;
            float minValue = statModValue.minValue * mult;
            float maxValue = statModValue.maxValue * mult;
            statApplier.generateStatDescription(tooltip, bulletColor, minValue, maxValue);
        }

        if (isPrimary) {
            tooltip.addSpacer(6f);
            addAdditionalDescriptions(tooltip);
        }
    }

    private void addAdditionalDescriptions(TooltipMakerAPI tooltip) {
        if (!isNull(combatScriptDescription) && !combatScriptDescription.getDisplayString().isEmpty()) {
            tooltip.addPara(getCombatScriptDescription().getDisplayString(), 3f, Misc.getHighlightColor(),
                            getCombatScriptDescription().getHighlights());
        }

        if (!isNull(additionalDescription) && !additionalDescription.getDisplayString().isEmpty()) {
            tooltip.addPara(getAdditionalDescription().getDisplayString(), 3f, Misc.getHighlightColor(),
                            getAdditionalDescription().getHighlights());
        }
    }

    @Override
    public void runCustomScript(ShipAPI ship, float amount) {
        if ((installedSlot.getSlotCategory() == SlotCategory.COSMETIC
                || installedSlot.getSlotCategory() == SlotCategory.SPECIAL)
                && !isNull(combatScriptRunner)) {
            combatScriptRunner.run(ship, amount, this);
        }
    }

    @Override
    public List<StatApplier> getActiveStatMods() {
        return isInPrimarySlot() ? primaryStatMods : secondaryStatMods;
    }

    @Override
    public boolean isRepairable() {
        return !isDestroyed() && augmentQuality != initialQuality;
    }

    @Override
    public boolean isDestroyed() {
        return augmentQuality == AugmentQuality.DESTROYED;
    }

    @Override
    public AugmentApplier damageAugment(int numLevelsDamaged) {
        for (int i = 0; i < numLevelsDamaged; i++) {
            augmentQuality = augmentQuality.getLowerQuality();
        }

        return this;
    }

    @Override
    public AugmentApplier repairAugment(int numLevelsRepaired) {
        if (augmentQuality != AugmentQuality.DESTROYED) {
            for (int i = 0; i < numLevelsRepaired; i++) {
                augmentQuality = augmentQuality.getHigherQuality();
            }
        }

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

    @Override
    public void updateFighterStatValue(String id, float value) {
        appliedFighterValues.put(id, value);
    }

    @Override
    public Float getFighterStatValue(String id) {
        return appliedFighterValues.get(id);
    }

    @Override
    public boolean isInPrimarySlot() {
        if (isNull(installedSlot)) {
            return false;
        }

        return installedSlot.getSlotCategory() == primarySlot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(augmentID, augmentQuality);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseAugment that = (BaseAugment) o;
        return augmentID.equals(that.augmentID) && augmentQuality == that.augmentQuality;
    }
}
