package de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StatModValue<A, B, C> {
    public A minValue;
    public B maxValue;
    public C getsModified;
}