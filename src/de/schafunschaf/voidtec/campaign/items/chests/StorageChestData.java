package de.schafunschaf.voidtec.campaign.items.chests;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.Color;
import java.util.List;

public interface StorageChestData {

    String getTitle();

    CargoAPI getAllowedCargo();

    String getName();

    void setName(String name);

    String getManufacturer();

    Color getColor();

    CargoAPI getChestStorage();

    int getMaxSize();

    int getCurrentSize();

    void setCurrentSize(int size);

    BaseChestData.UseMode getCurrentMode();

    void setCurrentMode(BaseChestData.UseMode mode);

    void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource);

    void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemPlugin.SpecialItemRendererAPI renderer);

    void openDialog(InteractionDialogAPI dialog, StorageChestPlugin storageChestPlugin);

    List<String> getAllowedItemsString();
}
