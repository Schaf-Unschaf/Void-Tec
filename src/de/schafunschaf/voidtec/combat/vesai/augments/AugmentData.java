package de.schafunschaf.voidtec.combat.vesai.augments;

import de.schafunschaf.voidtec.combat.vesai.AfterCreationEffect;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
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
    protected String manufacturer;
    protected String name;
    protected TextWithHighlights description;
    protected int rarity;
    protected SlotCategory primarySlot;
    protected List<StatApplier> primaryStatMods = new ArrayList<>();
    protected List<StatModValue<Float, Float, Boolean>> primaryStatValues = new ArrayList<>();
    protected List<SlotCategory> secondarySlots = new ArrayList<>();
    protected List<StatApplier> secondaryStatMods = new ArrayList<>();
    protected List<StatModValue<Float, Float, Boolean>> secondaryStatValues = new ArrayList<>();
    protected String[] augmentQualityRange;
    protected TextWithHighlights combatScriptDescription = null;
    protected TextWithHighlights additionalDescription = null;
    protected boolean equalQualityRoll = false;
    protected CombatScriptRunner combatScript = null;
    protected AfterCreationEffect afterCreationEffect = null;
}
