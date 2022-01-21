package de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments;

import de.schafunschaf.voidtec.scripts.combat.effects.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.util.TextWithHighlights;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AugmentData {
    protected String augmentID;
    protected String manufacturer;
    protected String name;
    protected TextWithHighlights description;
    protected int rarity;
    protected SlotCategory primarySlot;
    protected List<BaseStatMod> primaryStatMods = new ArrayList<>();
    protected List<StatModValue<Float, Float, Boolean>> primaryStatValues = new ArrayList<>();
    protected List<SlotCategory> secondarySlots = new ArrayList<>();
    protected List<BaseStatMod> secondaryStatMods = new ArrayList<>();
    protected List<StatModValue<Float, Float, Boolean>> secondaryStatValues = new ArrayList<>();
    protected String[] augmentQualityRange;
    protected TextWithHighlights combatScriptDescription;
    protected boolean equalQualityRoll = false;
    protected CombatScriptRunner combatScript = null;
}
