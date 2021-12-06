package de.schafunschaf.voidtec.scripts.combat.effects.esu;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UpgradeCategory {
    ENGINE(""),
    FLUX(""),
    SENSOR(""),
    SHIELD(""),
    DURABILITY(""),
    RESISTANCE(""),
    WEAPON(""),
    PROJECTILE(""),
    LOGISTIC("");

    String image;
}
