package de.schafunschaf.voidtec.campaign.dialog;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import de.schafunschaf.voidtec.combat.vesai.RightClickAction;

import java.util.Map;

public class VT_AugmentInteraction implements InteractionDialogPlugin {

    private final RightClickAction rightClickAction;

    public VT_AugmentInteraction(RightClickAction rightClickAction) {
        this.rightClickAction = rightClickAction;
    }

    @Override
    public void init(InteractionDialogAPI dialog) {
        rightClickAction.openDialog(dialog);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {

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
}
