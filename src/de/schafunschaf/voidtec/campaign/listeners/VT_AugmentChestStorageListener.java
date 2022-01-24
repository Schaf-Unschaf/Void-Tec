package de.schafunschaf.voidtec.campaign.listeners;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.ButtonAPI;
import com.fs.starfarer.api.ui.CutStyle;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestData;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestPlugin;
import de.schafunschaf.voidtec.helper.VoidTecUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ProgressBar.addStorageMeter;

public class VT_AugmentChestStorageListener implements CargoPickerListener {
    private final AugmentChestPlugin augmentChestPlugin;
    private final CargoAPI targetStorage;
    private final boolean transferFromPlayer;
    private final AugmentChestData augmentChestData;

    public VT_AugmentChestStorageListener(AugmentChestPlugin augmentChestPlugin, CargoAPI targetStorage,
                                          boolean transferFromPlayer) {
        this.augmentChestPlugin = augmentChestPlugin;
        this.targetStorage = targetStorage;
        this.transferFromPlayer = transferFromPlayer;
        this.augmentChestData = augmentChestPlugin.getAugmentChestData();
    }

    @Override
    public void pickedCargo(CargoAPI cargo) {
        int sumCargoAffected = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            sumCargoAffected += (int) stack.getSize();
        }

        if (transferFromPlayer && augmentChestData.getCurrentSize() + sumCargoAffected > augmentChestData.getMaxSize()) {
            return;
        }

        augmentChestPlugin.addToSize(transferFromPlayer ? sumCargoAffected : -sumCargoAffected);

        targetStorage.addAll(cargo);

        if (transferFromPlayer) {
            VoidTecUtils.adjustItemInCargo(cargo, Global.getSector().getPlayerFleet().getCargo());
        }
    }

    @Override
    public void cancelledCargoSelection() {

    }

    @Override
    public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp,
                                  boolean pickedUpFromSource, CargoAPI combined) {
        int sumCargoAffected = 0;
        for (CargoStackAPI stack : combined.getStacksCopy()) {
            sumCargoAffected += (int) stack.getSize();
        }

        sumCargoAffected = transferFromPlayer ? sumCargoAffected : -sumCargoAffected;

        float maxSize = augmentChestData.getMaxSize();
        float currentSize = augmentChestData.getCurrentSize() + sumCargoAffected;

        ButtonAPI outerButton = addStorageMeter(panel, 200f, 22f, currentSize, maxSize, Misc.scaleColorOnly(Misc.getBasePlayerColor(), 0.8f), Misc.scaleColorOnly(Misc.getDarkPlayerColor(), 0.2f), Misc.getBasePlayerColor(), 0f);

        augmentChestPlugin.createTooltip(panel, false, null, null);
        panel.addPara("Allowed Items", Misc.getHighlightColor(), 10f);
        panel.addButton("", null, Color.BLACK, Misc.getBasePlayerColor(), Alignment.MID, CutStyle.ALL, panel.computeStringWidth("Allowed Items"), 0f, 3f);
        panel.addPara(" - Augments", 6f);

        outerButton.getPosition().setXAlignOffset(184f);
        outerButton.getPosition().setYAlignOffset(-663f);
    }
}
