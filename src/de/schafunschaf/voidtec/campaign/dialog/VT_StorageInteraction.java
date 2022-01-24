package de.schafunschaf.voidtec.campaign.dialog;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestPlugin;
import de.schafunschaf.voidtec.campaign.listeners.VT_AugmentChestStorageListener;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperLeaveStorage;
import de.schafunschaf.voidtec.helper.VoidTecUtils;
import org.lwjgl.input.Keyboard;

import java.util.Map;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class VT_StorageInteraction implements InteractionDialogPlugin {
    private final AugmentChestPlugin augmentChestPlugin;
    private final CargoAPI chestStorage;
    private InteractionDialogAPI dialog;
    private OptionPanelAPI optionPanel;
    private VisualPanelAPI visualPanel;
    private TextPanelAPI textPanel;
    private CampaignFleetAPI playerFleet;

    public VT_StorageInteraction(AugmentChestPlugin augmentChestPlugin) {
        this.augmentChestPlugin = augmentChestPlugin;
        this.chestStorage = augmentChestPlugin.getAugmentChestData().getChestStorage();
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        this.dialog = dialog;
        this.optionPanel = dialog.getOptionPanel();
        this.visualPanel = dialog.getVisualPanel();
        this.textPanel = dialog.getTextPanel();

        this.playerFleet = Global.getSector().getPlayerFleet();

        optionSelected("", OptionId.INIT);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (isNull(optionData)) {
            return;
        }

        OptionId selectedOption = (OptionId) optionData;

        switch (selectedOption) {
            case INIT:
                initOptions();
                break;
            case STORE_AUGMENTS:
                dialog.showCargoPickerDialog("Store Augments", "Store", "Cancel", false, 100f, VoidTecUtils.getAugmentsInPlayerCargo(), new VT_AugmentChestStorageListener(augmentChestPlugin, chestStorage, true));
                break;
            case RETRIEVE_AUGMENTS:
                dialog.showCargoPickerDialog("Take Augments", "Take", "Cancel", false, 100f, chestStorage, new VT_AugmentChestStorageListener(augmentChestPlugin, playerFleet.getCargo(), false));
                break;
            case CLOSE:
                Global.getSector().addTransientScript(new VT_DialogHelperLeaveStorage());
                dialog.dismiss();
                break;
        }
    }

    private void initOptions() {
        optionPanel.addOption("Store your augments in the chest", OptionId.STORE_AUGMENTS);
        optionPanel.addOption("Retrieve your augments from the chest", OptionId.RETRIEVE_AUGMENTS);
        optionPanel.addOption("Close the interface", OptionId.CLOSE);
        optionPanel.setShortcut(OptionId.CLOSE, Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    @Override
    public void optionMousedOver(String optionText, Object optionData) {

    }

    @Override
    public void advance(float amount) {

    }

    @Override
    public void backFromEngagement(EngagementResultAPI battleResult) {

    }

    @Override
    public Object getContext() {
        return null;
    }

    @Override
    public Map<String, MemoryAPI> getMemoryMap() {
        return null;
    }

    enum OptionId {
        INIT, LIST_AUGMENTS, STORE_AUGMENTS, RETRIEVE_AUGMENTS, CLOSE
    }
}
