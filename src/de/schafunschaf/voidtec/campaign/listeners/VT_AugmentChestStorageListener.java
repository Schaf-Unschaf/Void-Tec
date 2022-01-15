package de.schafunschaf.voidtec.campaign.listeners;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.CargoPickerListener;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.items.augments.AugmentChestPlugin;
import de.schafunschaf.voidtec.helper.AugmentUtils;
import de.schafunschaf.voidtec.util.ColorShifter;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@RequiredArgsConstructor
public class VT_AugmentChestStorageListener implements CargoPickerListener {
    private final AugmentChestPlugin augmentChestPlugin;
    private final CargoAPI targetStorage;
    private final boolean transferFromPlayer;
    private ColorShifter colorShifter = new ColorShifter(Color.RED);

    @Override
    public void pickedCargo(CargoAPI cargo) {
        int sumCargoAffected = 0;
        for (CargoStackAPI stack : cargo.getStacksCopy())
            sumCargoAffected += (int) stack.getSize();

        if (transferFromPlayer && augmentChestPlugin.getCurrentSize() + sumCargoAffected > augmentChestPlugin.getMaxSize())
            return;

        augmentChestPlugin.addToSize(transferFromPlayer ? sumCargoAffected : -sumCargoAffected);

        targetStorage.addAll(cargo);

        if (transferFromPlayer)
            AugmentUtils.adjustItemInCargo(cargo, Global.getSector().getPlayerFleet().getCargo());
    }

    @Override
    public void cancelledCargoSelection() {

    }

    @Override
    public void recreateTextPanel(TooltipMakerAPI panel, CargoAPI cargo, CargoStackAPI pickedUp, boolean pickedUpFromSource, CargoAPI combined) {
        int sumCargoAffected = 0;
        for (CargoStackAPI stack : combined.getStacksCopy())
            sumCargoAffected += (int) stack.getSize();

        sumCargoAffected = transferFromPlayer ? sumCargoAffected : -sumCargoAffected;

        float maxSize = augmentChestPlugin.getMaxSize();
        float currentSize = augmentChestPlugin.getCurrentSize() + sumCargoAffected;
        Color spaceHLColor = currentSize < maxSize ? Misc.getHighlightColor() : Misc.getNegativeHighlightColor();

//        panel.setParaFont(Fonts.INSIGNIA_LARGE);


        maxSize = 10;
//        currentSize = sumCargoAffected;
        Color barColor = colorShifter.shiftColor(0.5f);
        float outerWidth = 90;
        float innerWidth = Math.min(Math.max((currentSize / maxSize) * outerWidth + 8f, 8f), outerWidth);
        ButtonAPI outerButton = panel.addButton("", null, Color.BLACK, Color.DARK_GRAY, Alignment.MID, CutStyle.ALL, outerWidth, 22f, 10f);
        LabelAPI numberDisplay = panel.addPara(String.format("%s/%s", (int) currentSize, (int) maxSize), 3f, Misc.getGrayColor(), spaceHLColor, String.valueOf((int) currentSize), String.valueOf((int) maxSize));
        ButtonAPI bgButton = panel.addButton("", null, Color.BLACK, Color.BLACK, Alignment.MID, CutStyle.ALL, outerWidth, 16f, 10f);
        ButtonAPI innerButton = panel.addButton("", null, Color.BLACK, Misc.scaleColorOnly(barColor, 0.5f), Alignment.MID, CutStyle.ALL, innerWidth, 16f, 0);

        panel.addPara("Allowed Items", Misc.getHighlightColor(), 10f).getPosition().inTR(0f, 20f);
        panel.addButton("", null, Color.BLACK, Misc.getBasePlayerColor(), Alignment.MID, CutStyle.ALL, panel.computeStringWidth("Allowed Items"), 0f, 3f);
        panel.addPara(" - Augments", 6f);

        outerButton.getPosition().setYAlignOffset(-662f);
        outerButton.getPosition().setXAlignOffset(124f);
//        numberDisplay.setAlignment(Alignment.MID);
        numberDisplay.getPosition().rightOfMid(outerButton, 20);
        bgButton.getPosition().rightOfMid(outerButton, -outerWidth - 2);
        innerButton.getPosition().rightOfMid(outerButton, 0);
        innerButton.getPosition().setXAlignOffset(-outerWidth - 2);
    }

    private void addStorageMeter(TooltipMakerAPI panel) {

    }
}
