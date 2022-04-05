package de.schafunschaf.voidtec.campaign.dialog;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import de.schafunschaf.voidtec.campaign.items.chests.StorageChestPlugin;

import java.util.Map;

public class VT_StorageInteraction implements InteractionDialogPlugin {

    private final StorageChestPlugin storageChestPlugin;

    public VT_StorageInteraction(StorageChestPlugin storageChestPlugin) {
        this.storageChestPlugin = storageChestPlugin;
    }

    @Override
    public void init(final InteractionDialogAPI dialog) {
        storageChestPlugin.getChestData().openDialog(dialog, storageChestPlugin);
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
