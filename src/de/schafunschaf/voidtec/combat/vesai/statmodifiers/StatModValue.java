package de.schafunschaf.voidtec.combat.vesai.statmodifiers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class StatModValue<A, B, C, D> {

    public A minValue;
    public B maxValue;
    public C getsModified;
    public D invertModifier;
}
