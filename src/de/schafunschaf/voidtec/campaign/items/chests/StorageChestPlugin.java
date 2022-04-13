package de.schafunschaf.voidtec.campaign.items.chests;

import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public interface StorageChestPlugin {

    void setSize(int num);

    StorageChestData getChestData();

    String getName();

    void setName(String name);

    void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource);
}
