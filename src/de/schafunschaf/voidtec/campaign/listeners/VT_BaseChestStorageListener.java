package de.schafunschaf.voidtec.campaign.listeners;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.items.chests.StorageChestData;
import de.schafunschaf.voidtec.campaign.items.chests.StorageChestPlugin;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperLeaveToCargo;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ProgressBar;

import java.awt.Color;

public class VT_BaseChestStorageListener implements CargoPickerListener {

    protected final StorageChestPlugin storageChestPlugin;
    protected final CargoAPI targetStorage;
    protected final StorageChestData storageChestData;
    protected final InteractionDialogAPI dialog;
    protected boolean loadedChestCargo = false;

    public VT_BaseChestStorageListener(StorageChestPlugin storageChestPlugin, CargoAPI targetStorage, InteractionDialogAPI dialog) {
        this.storageChestPlugin = storageChestPlugin;
        this.targetStorage = targetStorage;
        this.storageChestData = storageChestPlugin.getChestData();
        this.dialog = dialog;
    }

    @Override
    public void pickedCargo(CargoAPI cargo) {
        cargo.removeAll(storageChestData.getChestStorage());
        int sumCargoAffected = 0;

        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            sumCargoAffected += (int) stack.getSize();
        }

        if (storageChestData.getCurrentSize() + sumCargoAffected > storageChestData.getMaxSize()) {
            return;
        }

        storageChestPlugin.addToSize(sumCargoAffected);

        targetStorage.addAll(cargo);

        CargoUtils.adjustItemInCargo(cargo, Global.getSector().getPlayerFleet().getCargo());

        closeChest();
    }

    @Override
    public void cancelledCargoSelection() {
        closeChest();
    }

    @Override
    public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource,
                                  CargoAPI combined) {
        if (!loadedChestCargo) {
            cargo.addAll(storageChestData.getChestStorage());
            loadedChestCargo = true;
        }
        combined.removeAll(storageChestData.getChestStorage());

        int sumCargoAffected = 0;
        for (CargoStackAPI stack : combined.getStacksCopy()) {
            sumCargoAffected += (int) stack.getSize();
        }

        float maxSize = storageChestData.getMaxSize();
        float currentSize = storageChestData.getCurrentSize() + sumCargoAffected;

        Color manufacturerColor = VoidTecUtils.getManufacturerColor(storageChestData.getManufacturer());

        UIComponentAPI storageMeter = ProgressBar.addBarLTR(panel, String.format("%s / %s", (int) currentSize, (int) maxSize),
                                                            Alignment.MID, Fonts.ORBITRON_16, 300f, 26f, 2f, 3f,
                                                            (100 / maxSize) * currentSize, 0f,
                                                            Misc.getTextColor(), manufacturerColor, Color.BLACK,
                                                            Misc.scaleColorOnly(manufacturerColor, 0.3f));

        storageChestPlugin.createTooltip(panel, false, null, null);
        panel.addPara("Allowed Items", Misc.getHighlightColor(), 10f);
        panel.addButton("", null, Color.BLACK, Misc.getBasePlayerColor(), Alignment.MID, CutStyle.ALL,
                        panel.computeStringWidth("Allowed Items"), 0f, 3f);
        panel.addSpacer(6f);
        for (String item : storageChestData.getAllowedItemsString()) {
            panel.addPara(String.format(" - %s", item), 0f);
        }

        storageMeter.getPosition().setXAlignOffset(130);
        storageMeter.getPosition().setYAlignOffset(-660f);
    }

    protected void closeChest() {
        Global.getSector().addTransientScript(new VT_DialogHelperLeaveToCargo());
        dialog.dismiss();
    }
}
