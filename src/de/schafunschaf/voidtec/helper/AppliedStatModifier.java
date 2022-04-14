package de.schafunschaf.voidtec.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppliedStatModifier {

    private final String statID;
    private int value;
    private final boolean isMultStat;
    private final boolean isFighterStat;
    private final boolean hasNegativeAsBenefit;

    public void update(int nextValue) {
        if (isMultStat) {
            float v1 = 1 + value / 100f;
            float v2 = 1 + nextValue / 100f;
            value = (int) ((v1 * v2 - 1) * 100);
        } else {
            value += nextValue;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AppliedStatModifier)) {
            return false;
        }

        AppliedStatModifier otherASM = (AppliedStatModifier) obj;

        return statID.equals(otherASM.statID) && isFighterStat == otherASM.isFighterStat();
    }
}
