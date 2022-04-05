package de.schafunschaf.voidtec.combat.scripts.interactions;

import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import de.schafunschaf.voidtec.campaign.dialog.VT_ColorPickerDialog;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperOpenAugmentInteraction;
import de.schafunschaf.voidtec.combat.vesai.RightClickAction;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.VoidTecUtils;

import java.awt.Color;

public class OpenColorPickerAction implements RightClickAction {

    private Color customColor = Color.BLACK;

    @Override
    public void run() {
        VoidTecUtils.openDialogPlugin(new VT_DialogHelperOpenAugmentInteraction(this));
    }

    @Override
    public void openDialog(InteractionDialogAPI dialog) {
        dialog.getTextPanel().addPara(VT_Strings.VT_SHEEP_WIKI);

        CustomDialogDelegate delegate = new VT_ColorPickerDialog(dialog, this);
        dialog.showCustomDialog(310, 300, delegate);
    }

    @Override
    public Object getActionObject() {
        return customColor;
    }

    @Override
    public void setActionObject(Object object) {
        this.customColor = (Color) object;
    }
}
