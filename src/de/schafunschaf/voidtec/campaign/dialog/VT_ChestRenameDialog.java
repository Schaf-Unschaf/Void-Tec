package de.schafunschaf.voidtec.campaign.dialog;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomDialogDelegate;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TextFieldAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.items.chests.StorageChestPlugin;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperLeaveToCargo;
import lombok.RequiredArgsConstructor;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class VT_ChestRenameDialog implements CustomDialogDelegate {

    private final InteractionDialogAPI dialog;
    private final StorageChestPlugin storageChestPlugin;
    private TextFieldAPI textFieldAPI = null;

    @Override
    public void createCustomDialog(CustomPanelAPI panel) {
        TooltipMakerAPI uiElement = panel.createUIElement(300, 70, false);
        uiElement.setParaFont(Fonts.INSIGNIA_LARGE);
        uiElement.addPara("Enter a new name:", 0f);
        textFieldAPI = uiElement.addTextField(300, 10f);
        textFieldAPI.setColor(Misc.getHighlightColor());
        textFieldAPI.setText(storageChestPlugin.getName());
        textFieldAPI.setUndoOnEscape(true);
        panel.addUIElement(uiElement);
    }

    @Override
    public boolean hasCancelButton() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Rename";
    }

    @Override
    public String getCancelText() {
        return null;
    }

    @Override
    public void customDialogConfirm() {
        if (!isNull(textFieldAPI)) {
            storageChestPlugin.setName(textFieldAPI.getText());
        }

        closeDialog();
    }

    @Override
    public void customDialogCancel() {
        closeDialog();
    }

    @Override
    public CustomUIPanelPlugin getCustomPanelPlugin() {
        return null;
    }

    private void closeDialog() {
        Global.getSector().addTransientScript(new VT_DialogHelperLeaveToCargo());
        dialog.dismiss();
    }
}
