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
import java.util.ArrayList;
import java.util.List;

public class VT_BaseChestStorageListener implements CargoPickerListener {

    protected final StorageChestPlugin chestPlugin;
    protected final StorageChestData chestData;
    protected final CargoAPI chestInventory;
    protected final CargoAPI sourceCargo;
    protected final InteractionDialogAPI dialog;
    protected boolean loadedChestCargo = false;

    public VT_BaseChestStorageListener(StorageChestPlugin chestPlugin, CargoAPI sourceCargo, InteractionDialogAPI dialog) {
        this.chestPlugin = chestPlugin;
        this.chestData = chestPlugin.getChestData();
        this.chestInventory = chestData.getChestStorage();
        this.sourceCargo = sourceCargo;
        this.dialog = dialog;
    }

    @Override
    public void pickedCargo(CargoAPI cargo) {
        List<CargoStackAPI> cargoToAdd = new ArrayList<>();
        List<CargoStackAPI> cargoOverflow = new ArrayList<>();
        int sumChestCargo = 0;

        // Distribute stacks
        for (CargoStackAPI stackToTransfer : cargo.getStacksCopy()) {
            boolean chestOverflowing = sumChestCargo + stackToTransfer.getSize() > chestData.getMaxSize();

            if (chestOverflowing) {
                cargoOverflow.add(stackToTransfer);
            } else {
                cargoToAdd.add(stackToTransfer);
                sumChestCargo += stackToTransfer.getSize();
            }
        }

        chestInventory.clear();
        chestInventory.addAll(CargoUtils.addCargo(cargoToAdd, null));
        sourceCargo.addAll(CargoUtils.addCargo(cargoOverflow, null));
        closeChest();
    }

    @Override
    public void cancelledCargoSelection() {
        closeChest();
    }

    @Override
    public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource,
                                  CargoAPI combined) {
        // cargo = initial chest inventory
        // combined = current chest inventory
        CargoAPI chestStorage = chestData.getChestStorage();

        if (!loadedChestCargo) {
            cargo.addAll(chestStorage);
            loadedChestCargo = true;
        }

        int sumCargoAffected = 0;
        for (CargoStackAPI stack : combined.getStacksCopy()) {
            if (stack != pickedUp) {
                sumCargoAffected += (int) stack.getSize();
            }
        }

        float maxSize = chestData.getMaxSize();
        float currentSize = sumCargoAffected;

        Color manufacturerColor = VoidTecUtils.getManufacturerColor(chestData.getManufacturer());

        UIComponentAPI storageMeter = ProgressBar.addBarLTR(panel, String.format("%s / %s", (int) currentSize, (int) maxSize),
                                                            Alignment.MID, Fonts.ORBITRON_16, 300f, 26f, 2f, 3f,
                                                            (100 / maxSize) * currentSize, 0f,
                                                            Misc.getTextColor(), manufacturerColor, Color.BLACK,
                                                            Misc.scaleColorOnly(manufacturerColor, 0.3f));

        chestPlugin.createTooltip(panel, false, null, null);
        panel.addPara("Allowed Items", Misc.getHighlightColor(), 10f);
        panel.addButton("", null, Color.BLACK, Misc.getBasePlayerColor(), Alignment.MID, CutStyle.ALL,
                        panel.computeStringWidth("Allowed Items"), 0f, 3f);
        panel.addSpacer(6f);
        for (String item : chestData.getAllowedItemsString()) {
            panel.addPara(String.format(" - %s", item), 0f);
        }

        storageMeter.getPosition().setXAlignOffset(130);
        storageMeter.getPosition().setYAlignOffset(-660f);
    }

    protected void closeChest() {
        CargoAPI playerCargo = Global.getSector().getPlayerFleet().getCargo();
        playerCargo.addAll(sourceCargo);
        playerCargo.sort();

        int sumChestCargo = 0;
        for (CargoStackAPI stack : chestInventory.getStacksCopy()) {
            sumChestCargo += stack.getSize();
        }

        chestPlugin.setSize(sumChestCargo);
        Global.getSector().addTransientScript(new VT_DialogHelperLeaveToCargo());
        dialog.dismiss();
    }
}
