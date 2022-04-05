package de.schafunschaf.voidtec.combat.vesai;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;

public interface RightClickAction {

    void run();

    void openDialog(InteractionDialogAPI dialog);

    Object getActionObject();

    void setActionObject(Object object);
}
