package de.schafunschaf.voidtec.combat.vesai.augments;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatApplier;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.TextWithHighlights;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class AugmentData {

    protected String augmentID;
    protected List<String> manufacturer;
    protected String name;
    protected TextWithHighlights description;
    protected int rarity;
    protected SlotCategory primarySlot;
    protected List<StatApplier> primaryStatMods = new ArrayList<>();
    protected List<StatModValue<Float, Float, Boolean, Boolean>> primaryStatValues = new ArrayList<>();
    protected List<SlotCategory> secondarySlots = new ArrayList<>();
    protected List<StatApplier> secondaryStatMods = new ArrayList<>();
    protected List<StatModValue<Float, Float, Boolean, Boolean>> secondaryStatValues = new ArrayList<>();
    protected String[] augmentQualityRange;
    protected TextWithHighlights additionalDescription = null;
    protected boolean equalQualityRoll = false;
    protected String beforeCreationEffectPath = null;
    protected String afterCreationEffectPath = null;
    protected String combatScriptPath = null;
    protected String rightClickActionPath = null;
    protected boolean uniqueMod = false;
    protected List<String> incompatibleWith = new ArrayList<>();
    protected List<String> allowedFactions = new ArrayList<>();
    protected List<String> forbiddenFactions = new ArrayList<>();
    protected List<String> tags = new ArrayList<>();

    protected void storeAugment() {
        AugmentDataManager.storeAugmentData(augmentID, this);
    }
}
