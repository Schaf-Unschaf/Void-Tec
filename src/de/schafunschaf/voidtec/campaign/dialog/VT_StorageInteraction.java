package de.schafunschaf.voidtec.campaign.dialog;

import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import de.schafunschaf.voidtec.VT_Strings;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestPlugin;
import de.schafunschaf.voidtec.campaign.listeners.VT_AugmentChestStorageListener;
import de.schafunschaf.voidtec.util.VoidTecUtils;

import java.util.Map;

public class VT_StorageInteraction implements InteractionDialogPlugin {

    private final AugmentChestPlugin augmentChestPlugin;
    private final CargoAPI chestStorage;

    public VT_StorageInteraction(AugmentChestPlugin augmentChestPlugin) {
        this.augmentChestPlugin = augmentChestPlugin;
        this.chestStorage = augmentChestPlugin.getAugmentChestData().getChestStorage();
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        dialog.getTextPanel().addPara(VT_Strings.VT_SHEEP_WIKI);
        dialog.showCargoPickerDialog("Store Augments", "Store", "Cancel", false, 100f, VoidTecUtils.getAugmentsInPlayerCargo(),
                                     new VT_AugmentChestStorageListener(augmentChestPlugin, chestStorage, dialog));
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {}

    @Override
    public void optionMousedOver(String optionText, Object optionData) {}

    @Override
    public void advance(float amount) {}

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {}

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }
}
