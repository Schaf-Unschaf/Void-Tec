package de.schafunschaf.voidtec.combat.vesai.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import de.schafunschaf.voidtec.combat.vesai.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import de.schafunschaf.voidtec.ids.VT_Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseAugment implements AugmentApplier {

    protected String augmentID;
    protected String manufacturer;
    protected String name;
    protected TextWithHighlights description;
    protected int rarity;
    protected SlotCategory primarySlot;
    protected List<StatApplier> primaryStatMods;
    protected List<StatModValue<Float, Float, Boolean, Boolean>> primaryStatValues;
    protected List<SlotCategory> secondarySlots;
    protected List<StatApplier> secondaryStatMods;
    protected List<StatModValue<Float, Float, Boolean, Boolean>> secondaryStatValues;
    protected AugmentQuality augmentQuality;
    protected TextWithHighlights additionalDescription;
    protected BeforeCreationEffect beforeCreationEffect;
    protected AfterCreationEffect afterCreationEffect;
    protected CombatScriptRunner combatScriptRunner;
    protected RightClickAction rightClickAction;
    protected Map<String, Float> appliedFighterValues;
    protected boolean uniqueMod;
    protected boolean stackable;
    protected AugmentQuality initialQuality;
    protected AugmentSlot installedSlot;

    public BaseAugment(AugmentData augmentData, AugmentQuality augmentQuality) {
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
                              ? AugmentQuality.getRandomQualityInRange(augmentData.getAugmentQualityRange(), null,
                                                                       augmentData.isEqualQualityRoll())
                              : augmentQuality;
        this.initialQuality = augmentQuality;
        this.additionalDescription = augmentData.getAdditionalDescription();
        this.beforeCreationEffect = ((BeforeCreationEffect) createInstanceFromPath(augmentData.getBeforeCreationEffectPath()));
        this.afterCreationEffect = ((AfterCreationEffect) createInstanceFromPath(augmentData.getAfterCreationEffectPath()));
        this.combatScriptRunner = ((CombatScriptRunner) createInstanceFromPath(augmentData.getCombatScriptPath()));
        this.rightClickAction = ((RightClickAction) createInstanceFromPath(augmentData.getRightClickActionPath()));
        this.appliedFighterValues = new HashMap<>();
        this.uniqueMod = augmentData.isUniqueMod();
        this.stackable = isNull(rightClickAction);
    }

    @SuppressWarnings("deprecation")
    @SneakyThrows
    private Object createInstanceFromPath(String rightClickActionPath) {
        if (isNull(rightClickActionPath) || rightClickActionPath.isEmpty()) {
            return null;
        }

        try {
            Class<?> aClass = Global.getSettings().getScriptClassLoader().loadClass(rightClickActionPath);

            return aClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void applyToShip(MutableShipStatsAPI stats, String id, int slotIndex) {
        if (augmentQuality == AugmentQuality.DESTROYED) {
            return;
        }

        boolean inPrimarySlot = isInPrimarySlot();

        List<StatApplier> statMods = inPrimarySlot ? getPrimaryStatMods() : getSecondaryStatMods();
        List<StatModValue<Float, Float, Boolean, Boolean>> statModValues = inPrimarySlot
                                                                           ? getPrimaryStatValues()
                                                                           : getSecondaryStatValues();

        for (int i = 0; i < statMods.size(); i++) {
            long hullModManagerSeed = HullModDataStorage.getInstance().getHullModManager(stats.getFleetMember().getId()).getRandomSeed();
            long randomSeed = slotIndex * (i + 1) * 100 * hullModManagerSeed;
            StatApplier statApplier = statMods.get(i);
            statApplier.applyToShip(stats, id + "_" + getAugmentID(), statModValues.get(i), randomSeed, this);
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
                                Color bulletColorOverride) {
        List<StatApplier> statMods = isInPrimarySlot() ? getPrimaryStatMods() : getSecondaryStatMods();
        if (!isNull(statMods) && !statMods.isEmpty()) {
            Color bulletColor = isNull(bulletColorOverride) ? primarySlot.getColor() : bulletColorOverride;

            if (isNull(bulletColorOverride) && !isNull(installedSlot)) {
                bulletColor = installedSlot.getSlotCategory().getColor();
            }

            if (augmentQuality == AugmentQuality.DESTROYED) {
                tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), 0f);
            } else {
                for (StatApplier statApplier : statMods) {
                    statApplier.generateTooltipEntry(stats, id + "_" + getAugmentID(), tooltip, bulletColor, this);
                }
            }
        }

        addAdditionalDescription(tooltip);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, float padding, Boolean isPrimary, Color bulletColorOverride) {
        generateStatDescription(tooltip, padding, isPrimary, bulletColorOverride, augmentQuality);
    }

    @Override
    public void generateStatDescription(TooltipMakerAPI tooltip, float padding, Boolean isPrimary, Color bulletColorOverride,
                                        AugmentQuality quality) {
        if (quality == AugmentQuality.DESTROYED) {
            tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, quality.getColor(), 0f);
            return;
        }

        boolean inPrimarySlot = isNull(isPrimary) ? isInPrimarySlot() : isPrimary;

        List<StatApplier> statMods = inPrimarySlot ? getPrimaryStatMods() : getSecondaryStatMods();
        if (isNull(statMods) || statMods.isEmpty()) {
            if (isPrimary) {
                addAdditionalDescription(tooltip);
            }

            return;
        }

        List<StatModValue<Float, Float, Boolean, Boolean>> statModValues = inPrimarySlot
                                                                           ? getPrimaryStatValues()
                                                                           : getSecondaryStatValues();
        Color bulletColor = isNull(bulletColorOverride) ? primarySlot.getColor() : bulletColorOverride;

        if (isNull(bulletColorOverride) && !isNull(installedSlot)) {
            bulletColor = installedSlot.getSlotCategory().getColor();
        }

        for (int i = 0; i < statMods.size(); i++) {
            StatApplier statApplier = statMods.get(i);
            StatModValue<Float, Float, Boolean, Boolean> statModValue = statModValues.get(i);
            float mult = 1f;
            if (statModValue.getsModified) {
                mult = statModValue.invertModifier
                       ? AugmentQuality.getHighestQuality().getModifier() + 1 - quality.getModifier()
                       : quality.getModifier();
            }
            float minValue = statModValue.minValue * mult;
            float maxValue = statModValue.maxValue * mult;
            tooltip.addSpacer(3f);
            statApplier.generateStatDescription(tooltip, bulletColor, minValue, maxValue);
        }

        if (isPrimary) {
            tooltip.addSpacer(6f);
            addAdditionalDescription(tooltip);
        }
    }

    @Override
    public void applyBeforeCreation(MutableShipStatsAPI stats, String id) {
        if (!isNull(beforeCreationEffect)) {
            beforeCreationEffect.applyBeforeCreation(stats, id);
        }
    }

    @Override
    public void applyAfterCreation(ShipAPI ship, String id) {
        if (!isNull(afterCreationEffect)) {
            afterCreationEffect.applyAfterCreation(ship, id);
        }
    }

    @Override
    public void runCombatScript(ShipAPI ship, float amount) {
        if (!isNull(combatScriptRunner)) {
            combatScriptRunner.run(ship, amount, this);
        }
    }

    @Override
    public List<StatApplier> getActiveStatMods() {
        return isInPrimarySlot() ? primaryStatMods : secondaryStatMods;
    }

    @Override
    public void runRightClickAction() {
        if (!isNull(rightClickAction)) {
            rightClickAction.run();
        }
    }

    @Override
    public void collectAppliedStats(MutableShipStatsAPI stats, String id) {
        for (StatApplier statMod : getActiveStatMods()) {
            statMod.generateTooltipEntry(stats, id + "_" + getAugmentID(), null, null, this);
        }
    }

    @Override
    public boolean isRepairable() {
        return !isDestroyed() && augmentQuality != AugmentQuality.CUSTOMISED && augmentQuality != initialQuality && augmentQuality != AugmentQuality.DOMAIN;
    }

    @Override
    public boolean isDestroyed() {
        return augmentQuality == AugmentQuality.DESTROYED;
    }

    @Override
    public AugmentApplier damageAugment(int numLevelsDamaged) {
        if (augmentQuality == AugmentQuality.CUSTOMISED) {
            return this;
        }

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
        installedSlot = augmentSlot;
    }

    @Override
    public void uninstall() {
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

    private void addAdditionalDescription(TooltipMakerAPI tooltip) {
        if (!isNull(rightClickAction) && rightClickAction.getActionObject() instanceof Color) {
            getAdditionalDescription().setHlColor(((Color) rightClickAction.getActionObject()));
        }

        if (!isNull(additionalDescription) && !additionalDescription.getDisplayString().isEmpty()) {
            tooltip.addPara(getAdditionalDescription().getDisplayString(), 3f, getAdditionalDescription().getHlColor(),
                            getAdditionalDescription().getHighlights());
        }
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
        if (o == null || getClass() != o.getClass() || !stackable) {
            return false;
        }
        BaseAugment that = (BaseAugment) o;
        return augmentID.equals(that.augmentID) && augmentQuality == that.augmentQuality;
    }
}
