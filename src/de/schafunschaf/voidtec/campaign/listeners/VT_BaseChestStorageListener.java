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
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ProgressBar;

import java.awt.Color;

public class VT_BaseChestStorageListener implements CargoPickerListener {

    protected final StorageChestPlugin chestPlugin;
    protected final StorageChestData chestData;
    protected final CargoAPI chestInventory;
    protected final CargoAPI allowedCargo;
    protected final InteractionDialogAPI dialog;
    protected boolean loadedChestCargo = false;

    public VT_BaseChestStorageListener(StorageChestPlugin chestPlugin, CargoAPI allowedCargo, InteractionDialogAPI dialog) {
        this.chestPlugin = chestPlugin;
        this.chestData = chestPlugin.getChestData();
        this.chestInventory = chestData.getChestStorage();
        this.allowedCargo = allowedCargo;
        this.dialog = dialog;
    }

    @Override
    public void pickedCargo(CargoAPI cargo) {
        int sumCargoAffected = 0;

        for (CargoStackAPI stack : cargo.getStacksCopy()) {
            sumCargoAffected += (int) stack.getSize();
        }

        // Enough space to store items?
        if (chestData.getCurrentSize() + sumCargoAffected < chestData.getMaxSize()) {
            chestPlugin.setSize(sumCargoAffected);
            chestInventory.clear();
            chestInventory.addAll(cargo);
        }

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
        playerCargo.addAll(allowedCargo);
        playerCargo.sort();

        Global.getSector().addTransientScript(new VT_DialogHelperLeaveToCargo());
        dialog.dismiss();
    }
}
