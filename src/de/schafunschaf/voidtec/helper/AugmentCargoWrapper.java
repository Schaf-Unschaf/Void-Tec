package de.schafunschaf.voidtec.helper;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentApplier;
import lombok.Getter;

@Getter
public class AugmentCargoWrapper {
    public enum CargoSource {
        PLAYER_FLEET,
        CARGO_CHEST,
        LOCAL_STORAGE
    }

    private final AugmentApplier augment;
    private final CargoStackAPI augmentCargoStack;
    private final CargoSource cargoSource;
    private final CargoAPI sourceCargo;

    public AugmentCargoWrapper(CargoStackAPI augmentCargoStack, CargoSource cargoSource, CargoAPI sourceCargo) {
        this.augmentCargoStack = augmentCargoStack;
        this.cargoSource = cargoSource;
        this.sourceCargo = sourceCargo;
        this.augment = VoidTecUtils.getAugmentFromStack(augmentCargoStack);
    }
}
